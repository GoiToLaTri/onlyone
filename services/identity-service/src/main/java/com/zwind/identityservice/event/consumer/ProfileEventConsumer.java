package com.zwind.identityservice.event.consumer;

import com.zwind.identityservice.constant.RabbitMQConstantConfig;
import com.zwind.identityservice.dto.response.CreateProfileResponse;
import com.zwind.identityservice.enums.AccountStatus;
import com.zwind.identityservice.enums.ProfileStatus;
import com.zwind.identityservice.event.payload.EventPayload;
import com.zwind.identityservice.exception.AppError;
import com.zwind.identityservice.exception.AppException;
import com.zwind.identityservice.modules.accounts.entity.Account;
import com.zwind.identityservice.modules.accounts.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileEventConsumer {
    AccountRepository accountRepository;

    @RabbitListener(queues = RabbitMQConstantConfig.PROFILE_EVENTS_QUEUE)
    public void updateStatus(EventPayload<CreateProfileResponse> payload) {
        try {
            log.info(":::: pattern {}", payload.getPattern());
            log.info(":::: event id: {}", payload.getEventId());
            log.info(":::: timestamp {}", payload.getTimestamp());

            log.info(":::: data {}", payload.getData());
            Account account = accountRepository.findById(payload.getData().getAccountId())
                    .orElseThrow(() -> new AppException(AppError.USER_NOT_EXISTS));

            if(ProfileStatus.SUCCESS.equals(payload.getData().getStatus()))
                account.setAccountStatus(AccountStatus.ACTIVE);
            else account.setAccountStatus(AccountStatus.DELETE);

            accountRepository.save(account);

        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
