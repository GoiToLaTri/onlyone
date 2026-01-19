package com.zwind.userservice.constants;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class RabbitMQConstantConfig {
    // ---------- QUEUE CONSTANTS ----------
    public static final String USER_CREATE_PROFILE_QUEUE = "q.service.user.create-profile";
    public static final String IDENTITY_UPDATE_STATUS_QUEUE = "q.service.identity.update-status";
    public static final String USER_DEAD_LETTER_QUEUE = "q.user.dlq";

    // ---------- EXCHANGE CONSTANTS ----------
    public static final String USER_SERVICE_EXCHANGE = "x.service.user";
    public static final String IDENTITY_SERVICE_EXCHANGE = "x.service.identity";
    public static final String PROFILE_EVENTS_EXCHANGE = "x.profile.events";
    public static final String USER_DEAD_LETTER_EXCHANGE = "x.user.dlx";

    // ---------- ROUTING KEY CONSTANTS ----------
    public static final String USER_CREATED_RK = "user.created";
    public static final String PROFILE_CREATED_SUCCESS_RK = "profile.created.success";
    public static final String PROFILE_CREATED_FAILED_RK = "profile.created.failed";
    public static final String USER_DEAD_RK = "user.dead";

    private RabbitMQConstantConfig() {}
}
