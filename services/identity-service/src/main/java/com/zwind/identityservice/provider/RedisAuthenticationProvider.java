package com.zwind.identityservice.provider;

import com.zwind.grpc_security_starter.interceptors.GrpcAuthenticationProvider;
import com.zwind.grpc_security_starter.interceptors.GrpcAuthenticationToken;
import com.zwind.identityservice.exception.AppError;
import com.zwind.identityservice.exception.AppException;
import com.zwind.identityservice.modules.accounts.dto.AccountResponseDto;
import com.zwind.identityservice.modules.accounts.mapper.AccountMapper;
import com.zwind.identityservice.modules.accounts.repository.AccountRepository;
import com.zwind.identityservice.modules.authentication.dto.SessionStage;
import com.zwind.identityservice.modules.redis.RedisService;
import com.zwind.identityservice.modules.roles.dto.RoleResponseDto;
import com.zwind.identityservice.validation.SessionValidator;
import io.grpc.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisAuthenticationProvider implements GrpcAuthenticationProvider {
    SessionValidator sessionValidator;
    RedisService redisService;
    AccountRepository accountRepository;
    AccountMapper accountMapper;

    @Override
    public GrpcAuthenticationToken authenticate(Metadata metadata) {

       if(metadata == null) return GrpcAuthenticationToken.unauthenticated();

       String rawToken = metadata.get(Metadata.Key.of("Authorization",
               Metadata.ASCII_STRING_MARSHALLER));

       if(rawToken == null || !rawToken.startsWith("Bearer ")) return GrpcAuthenticationToken.unauthenticated();
       String token = rawToken.replaceFirst("^Bearer\\s+", "");
       SessionStage sessionStage = redisService.getKey("SID_" + token, SessionStage.class);
       if(sessionStage == null) return GrpcAuthenticationToken.unauthenticated();

       sessionValidator.validateGrpc(sessionStage, metadata);

        AccountResponseDto accountResponseDto;

        accountResponseDto = redisService.getKey(
                "AID_" + sessionStage.getUserId(),
                AccountResponseDto.class);

        if(accountResponseDto == null) {
            accountResponseDto = accountMapper.toAccountResponse(
                    accountRepository.findByIdWithRoles(sessionStage.getUserId())
                            .orElseThrow(() -> new AppException(AppError.USER_NOT_EXISTS))
            );
        }

       List<String> roles = accountResponseDto.getRoles().stream()
                .map(RoleResponseDto::getName)
                .toList();

       return GrpcAuthenticationToken.authenticated(sessionStage.getUserId(),
               null, roles);
    }
}
