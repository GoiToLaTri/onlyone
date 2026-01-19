package com.zwind.identityservice.components;

import com.zwind.identityservice.configurations.SessionAuthentication;
import com.zwind.identityservice.configurations.SessionPrincipal;
import com.zwind.identityservice.exception.AppError;
import com.zwind.identityservice.exception.AppException;
import com.zwind.identityservice.modules.accounts.dto.AccountResponseDto;
import com.zwind.identityservice.modules.accounts.mapper.AccountMapper;
import com.zwind.identityservice.modules.accounts.repository.AccountRepository;
import com.zwind.identityservice.modules.authentication.dto.SessionStage;
import com.zwind.identityservice.modules.redis.RedisService;
import com.zwind.identityservice.validation.SessionValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SessionAuthFilter extends OncePerRequestFilter {
    private final RedisService redisService;
    private final SessionValidator sessionValidator;
    private final HandlerExceptionResolver resolver;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    SessionAuthFilter(RedisService redisService,
                      SessionValidator sessionValidator,
                      AccountRepository accountRepository,
                      AccountMapper accountMapper,
                      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.redisService = redisService;
        this.sessionValidator = sessionValidator;
        this.resolver = resolver;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        try{
            String token = extractSessionToken(request);
            if(token != null) {
                SessionStage sessionStage = redisService.getKey("SID_" + token, SessionStage.class);
                if(sessionStage != null && sessionStage.getRiskScore() < 40)
                    processSessionAuthentication(sessionStage, request);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }

    private void processSessionAuthentication(SessionStage sessionStage, HttpServletRequest request){
        try {
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

            sessionValidator.validateRest(sessionStage, request);

            AccountResponseDto accountResponseDto;

            accountResponseDto = redisService.getKey(
                    "AID_" + sessionStage.getUserId(),
                    AccountResponseDto.class);

            if(accountResponseDto == null) {
                accountResponseDto = accountMapper.toAccountResponse(
                        accountRepository.findByIdWithRoles(sessionStage.getUserId())
                                .orElseThrow(() -> new AppException(AppError.USER_NOT_EXISTS))
                );

                redisService.setKey("AID_" + sessionStage.getUserId(),
                        accountResponseDto, 15, TimeUnit.MINUTES);
            }

            accountResponseDto.getRoles().forEach(role
                    -> {authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                            role.getPermissions().forEach(permission
                                    -> authorities.add(new SimpleGrantedAuthority(permission.getName())));                        }
                );

            if(authorities.isEmpty())
                authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

            SessionPrincipal principal = SessionPrincipal.builder()
                    .userId(sessionStage.getUserId())
                    .authLevel(sessionStage.getAuthLevel())
                    .build();

            Authentication authentication = new SessionAuthentication(principal, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new AppException(AppError.UNAUTHENTICATED);
        }
    }

    private String extractSessionToken(HttpServletRequest request) {
        String header = request.getHeader("authorization");
        if (header == null || !header.startsWith("Bearer "))
            return null;

        String[] parts = header.split(" ");
        return (parts.length == 2) ? parts[1] : null;
    }
}
