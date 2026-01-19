package com.zwind.userservice.event.consumer;

import com.zwind.userservice.constants.RabbitMQConstantConfig;
import com.zwind.userservice.enums.ProfileStatus;
import com.zwind.userservice.event.payload.EventPayload;
import com.zwind.userservice.event.producer.RabbitMQProducer;
import com.zwind.userservice.modules.users.UserService;
import com.zwind.userservice.modules.users.dto.CreateProfileDto;
import com.zwind.userservice.modules.users.dto.CreateProfileResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileConsumer {
    UserService userService;
    RabbitMQProducer rabbitMQProducer;

    @RabbitListener(queues = RabbitMQConstantConfig.USER_CREATE_PROFILE_QUEUE)
    public void create(EventPayload<CreateProfileDto> payload){
        try{
            log.info(":::: pattern {}", payload.getPattern());
            log.info(":::: event id: {}", payload.getEventId());
            log.info(":::: timestamp {}", payload.getTimestamp());
            log.info(":::: {}", payload.getData());
            userService.create(payload.getData());

            CreateProfileResponse response = CreateProfileResponse.builder()
                    .correlationId(payload.getEventId())
                    .status(ProfileStatus.SUCCESS)
                    .userId(payload.getData().getAccountId())
                    .build();

            rabbitMQProducer.sendEvent(RabbitMQConstantConfig.PROFILE_EVENTS_EXCHANGE,
                    RabbitMQConstantConfig.PROFILE_CREATED_SUCCESS_RK, response);
        } catch (DataIntegrityViolationException e) {
            // Ví dụ: Lỗi nghiệp vụ vĩnh viễn (Data sai) -> Không retry, bắn FAILED ngay

            CreateProfileResponse response = CreateProfileResponse.builder()
                    .correlationId(payload.getEventId())
                    .status(ProfileStatus.FAILED)
                    .userId(payload.getData().getAccountId())
                    .build();

            log.error(":::: Business Error (No Retry): {}", e.getMessage());
            rabbitMQProducer.sendEvent(payload.getEventId(),
                    RabbitMQConstantConfig.PROFILE_CREATED_FAILED_RK, response);

        }
        catch (Exception e) {
            log.error(e.getMessage());

            throw e;
        }
    }
}
