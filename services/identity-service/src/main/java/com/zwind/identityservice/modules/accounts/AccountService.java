package com.zwind.identityservice.modules.accounts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.zwind.common_lib.exception.HttpError;
import com.zwind.common_lib.exception.HttpException;
import com.zwind.identityservice.constant.RabbitMQConstantConfig;
import com.zwind.identityservice.enums.AccountStatus;
import com.zwind.identityservice.event.producer.RabbitMQProducer;
import com.zwind.identityservice.modules.accounts.dto.AccountResponseDto;
import com.zwind.identityservice.modules.accounts.dto.CreateAccountDto;
import com.zwind.identityservice.modules.accounts.entity.Account;
import com.zwind.identityservice.modules.accounts.mapper.AccountMapper;
import com.zwind.identityservice.modules.accounts.repository.AccountRepository;
import com.zwind.identityservice.modules.redis.RedisService;
import com.zwind.identityservice.modules.roles.entity.Role;
import com.zwind.identityservice.modules.roles.repositiory.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountService {
    AccountRepository accountRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;
    RedisService redisService;
    RoleRepository roleRepository;
    RabbitMQProducer rabbitMQProducer;

    @Transactional
    public AccountResponseDto create(CreateAccountDto createAccountDto) {
        Set<Role> roles = new HashSet<>();
        Map<String, Object> data = new HashMap<>();

        Account exists = accountRepository.findByEmailAndProvider(
                createAccountDto.getEmail(), "onlyone"
        ).orElse(null);

        if(exists != null) {
            if(AccountStatus.ACTIVE.equals(exists.getAccountStatus()))
                throw new HttpException(HttpError.USER_EXISTS);
            else if (AccountStatus.PENDING.equals(exists.getAccountStatus()))
                throw new HttpException(HttpError.USER_PENDING);
        }

        Account account = accountMapper.toAccount(createAccountDto);
        account.setPassword(passwordEncoder.encode(createAccountDto.getPassword()));

        roles.add(roleRepository.findByName("USER").orElseThrow(
                () -> new HttpException(HttpError.ROLE_NOT_EXISTS)
        ));

        account.setRoles(roles);
        Account result = accountRepository.save(account);

        data.put("name", createAccountDto.getName());
        data.put("email", createAccountDto.getEmail());
        data.put("accountId", result.getId());

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        rabbitMQProducer.sendEvent(RabbitMQConstantConfig.IDENTITY_SERVICE_EXCHANGE,
                                RabbitMQConstantConfig.USER_CREATED_RK, data);
                    }
                }
        );

        return accountMapper.toAccountResponse(result);
    }

    @Transactional(readOnly = true)
    public AccountResponseDto findById(String id) {
        return accountMapper.toAccountResponse(accountRepository.findById(id).orElseThrow(
                () -> new HttpException(HttpError.USER_NOT_EXISTS)
        ));
    }

    public AccountResponseDto accountInfo(){
        var context = SecurityContextHolder.getContext();
        String id = Objects.requireNonNull(context.getAuthentication()).getName();
        AccountResponseDto account = redisService.getKey("AID_" + id, AccountResponseDto.class);
        if(account != null) return account;

        return accountMapper.toAccountResponse(
                accountRepository.findById(id)
                        .orElseThrow(() -> new HttpException(HttpError.USER_NOT_EXISTS))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountResponseDto> findAll() {
        return accountRepository.findAll()
                .stream().map(accountMapper::toAccountResponse).toList();
    }
}
