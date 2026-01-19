package com.zwind.identityservice.modules.authentication;

import com.zwind.identityservice.constant.RabbitMQConstantConfig;
import com.zwind.identityservice.enums.AuthLevel;
import com.zwind.identityservice.enums.SessionStatus;
import com.zwind.identityservice.exception.AppError;
import com.zwind.identityservice.exception.AppException;
import com.zwind.identityservice.event.producer.RabbitMQProducer;
import com.zwind.identityservice.modules.accounts.dto.AccountResponseDto;
import com.zwind.identityservice.modules.accounts.entity.Account;
import com.zwind.identityservice.modules.accounts.mapper.AccountMapper;
import com.zwind.identityservice.modules.accounts.repository.AccountRepository;
import com.zwind.identityservice.modules.authentication.dto.*;
import com.zwind.identityservice.modules.authentication.entity.Session;
import com.zwind.identityservice.modules.authentication.mapper.SessionMapper;
import com.zwind.identityservice.modules.authentication.repository.SessionRepository;
import com.zwind.identityservice.modules.redis.RedisService;
import com.zwind.identityservice.provider.JwtTokenProvider;
import com.zwind.identityservice.validation.SessionValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    AccountRepository accountRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;
    RedisService redisService;
    SessionRepository sessionRepository;
    SessionValidator sessionValidator;
    SessionMapper sessionMapper;
    RabbitMQProducer rabbitMQProducer;
    RabbitMQConstantConfig rabbitMQConstantConfig;
    JwtTokenProvider jwtTokenProvider;

    public AuthenticationResponseDto authenticate(
            AuthenticationRequestDto authenticationRequestDto,
            HttpServletRequest request
    ) {
        Account account = accountRepository.findByEmail(authenticationRequestDto.getEmail())
                .orElseThrow(() -> new AppException(AppError.USER_NOT_EXISTS));

        boolean authenticated = passwordEncoder.matches(authenticationRequestDto.getPassword(),
                account.getPassword());
        if(!authenticated) throw new AppException(AppError.UNAUTHENTICATED);

        SessionStage session = createSession(account.getId(), request);
        AuthLevel authLevel = AuthLevel.PASSWORD;
        session.setAuthLevel(authLevel);
        session.setUserId(account.getId());

        String token = generateOpaqueToken();

        String refreshToken = signRefreshToken(session, token,null);

        AccountResponseDto accountResponseDto = accountMapper.toAccountResponse(account);

        CompletableFuture.runAsync(()->redisService.setKey("SID_" + token, session, 1, TimeUnit.HOURS));
        CompletableFuture.runAsync(()->redisService.setKey("AID_" + session.getUserId(),
                accountResponseDto,15, TimeUnit.MINUTES));

        return AuthenticationResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    public String introspect(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(AppError.USER_NOT_EXISTS));
        return jwtTokenProvider.generateToken(account);
    }

    public AuthenticationResponseDto refreshToken(RefreshTokenRequestDto requestDto,
                                                  HttpServletRequest request){
        Session currentSession = sessionRepository.findByToken(requestDto.getToken())
                .orElseThrow(() -> new AppException(AppError.SESSION_NOT_EXISTS));
//        Map<String, Object> payload = new HashMap<>();

        if(currentSession.isConsumed()) {
            revokeAllUserSessions(currentSession.getUserId());
//            payload.put("userId", currentSession.getUserId());
//            payload.put("message", "You have been logged out for security reasons");
//            payload.put("timestamp", LocalDateTime.now().toString());
//            rabbitMQProducer.sendMessage(rabbitMQConstantConfig
//                    .getForceLoggedOutRoutingKey(), payload);
            throw new AppException(AppError.TOKEN_ALREADY_USED);
        }

        sessionValidator.validateRest(sessionMapper.toSessionStage(currentSession), request);

        currentSession.setConsumed(true);
        sessionRepository.save(currentSession);

        SessionStage newSession = createSession(currentSession.getUserId(), request);
        String token = generateOpaqueToken();

        if(currentSession.getRiskScore() > 50)
            newSession.setAuthLevel(AuthLevel.MFA);
        log.info(String.valueOf(AuthLevel.valueOf(currentSession.getAuthLevel().name())));
        newSession.setAuthLevel(AuthLevel.valueOf(currentSession.getAuthLevel().name()));
        String refreshToken = signRefreshToken(newSession, token, currentSession.getExpireTime());
        newSession.setUserId(currentSession.getUserId());

        redisService.deleteKey("SID_" + requestDto.getToken());
        redisService.setKey("SID_" + token, newSession, 1, TimeUnit.HOURS);

        return AuthenticationResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    public List<SessionResponse> findAllActiveToken(){
        var context = SecurityContextHolder.getContext();
        String id = Objects.requireNonNull(context.getAuthentication()).getName();
        return sessionRepository.findAllByUserIdAndIsConsumedAndStatus(
                id, false, SessionStatus.ACTIVE).stream()
                .map(session -> SessionResponse.builder()
                .id(session.getId())
                .model(session.getDeviceDetails().getModel())
                .devicePlatform(session.getDeviceDetails().getPlatform())
                .lastActive(session.getUpdatedAt())
                .isCurrent(session.getAccessToken().equals(getCurrentAccessToken()))
                .build()).toList();
    }

    public void logout(LogoutRequestDto requestDto){
        SessionStage sessionStage = redisService.getKey("SID_" + requestDto.getToken(),
                SessionStage.class);
        Session currentRefreshToken = null;

        if(sessionStage != null && sessionStage.getStatus() == SessionStatus.ACTIVE) {
            redisService.deleteKey("SID_" + requestDto.getToken());

            currentRefreshToken = sessionRepository
                    .findByIdAndIsConsumed(sessionStage.getRefreshTokenId(), false)
                    .orElse(null);
        }

        if(currentRefreshToken == null)
            currentRefreshToken = sessionRepository
                    .findByTokenAndIsConsumed(requestDto.getRefreshToken(), false)
                    .orElseThrow(() -> new AppException(AppError.SESSION_NOT_EXISTS));

        currentRefreshToken.setStatus(SessionStatus.LOGOUT);
        currentRefreshToken.setConsumed(true);

        sessionRepository.save(currentRefreshToken);
    }

    private String getCurrentAccessToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("authorization");
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }

    private SessionStage createSession(String userId, HttpServletRequest request) {
        SessionStage.DeviceDetails deviceDetails = getDeviceDetail(request);

        return SessionStage.builder()
                .userId(userId)
                .deviceDetails(deviceDetails)
                .status(SessionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private SessionStage.DeviceDetails getDeviceDetail(HttpServletRequest request) {
        final String UNKNOWN;
        String userAgent;
        String acceptLang;
        String platform;
        String platformVersion;
        String architecture;
        String model;
        String canvasId;
        String screenRes;

        UNKNOWN = "unknown";

        userAgent = request.getHeader("User-Agent");
        acceptLang = request.getHeader("Accept-Language");

        platform = cleanHeader(request.getHeader("Sec-CH-UA-Platform"));
        platformVersion = cleanHeader(request.getHeader("Sec-CH-UA-Platform-Version"));
        architecture = cleanHeader(request.getHeader("Sec-CH-UA-Arch"));
        model = cleanHeader(request.getHeader("Sec-CH-UA-Model"));

        canvasId = request.getHeader("X-Device-Canvas");
        screenRes = request.getHeader("X-Device-Screen");

        return SessionStage.DeviceDetails.builder()
                .userAgent(userAgent != null ? userAgent : UNKNOWN)
                .acceptLang(acceptLang != null ? acceptLang : UNKNOWN)
                .platform(platform != null ? platform : UNKNOWN)
                .platformVersion(platformVersion != null ? platformVersion : UNKNOWN)
                .architecture(architecture != null ? architecture : UNKNOWN)
                .model(model != null ? model:UNKNOWN)
                .canvasId(canvasId != null ? canvasId : UNKNOWN)
                .screenRes(screenRes != null ? screenRes : UNKNOWN)
                .build();
    }

    private String cleanHeader(String value) {
        if (value == null) return null;
        return value.replace("\"", "").trim();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private void revokeAllUserSessions(String userId){
        List<Session> activeSessions = sessionRepository
                .findAllByUserIdAndStatus(userId, SessionStatus.ACTIVE);

        if(!activeSessions.isEmpty()){
            List<String> redisKeys = activeSessions.stream()
                    .map(s -> "SID_" + s.getAccessToken())
                            .toList();
            redisService.deleteMultipleKey(redisKeys);
            sessionRepository.updateStatusByUserId(userId, SessionStatus.REVOKED);
        }
    }

    private String signRefreshToken(SessionStage sessionStage,
                                    String accessToken,
                                    LocalDateTime expireTime) {
        String token = generateOpaqueToken();

        Session session = Session.builder()
                .userId(sessionStage.getUserId())
                .authLevel(sessionStage.getAuthLevel())
                .riskScore(sessionStage.getRiskScore())
                .deviceDetails(sessionStage.getDeviceDetails())
                .status(sessionStage.getStatus())
                .isConsumed(false)
                .token(token)
                .accessToken(accessToken)
                .expireTime(Optional.ofNullable(expireTime)
                        .orElse(LocalDateTime.now().plusHours(2)))
                .build();

        // 2. Lưu vào DB
        session = sessionRepository.save(session);

        // 3. CẬP NHẬT ID VÀO sessionStage
        // Cái ID này sẽ giúp link từ Access Token (Redis) xuống Refresh Token (DB)
        sessionStage.setRefreshTokenId(session.getId());

        return token;
    }
}
