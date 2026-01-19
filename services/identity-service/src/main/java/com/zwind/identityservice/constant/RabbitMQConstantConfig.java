package com.zwind.identityservice.constant;

import org.springframework.stereotype.Component;

@Component
public class RabbitMQConstantConfig {
    // ---------- Queue constant ----------
    public static final String PROFILE_EVENTS_QUEUE = "q.profile.events";
    public static final String USER_DEAD_LETTER_QUEUE = "q.user.dlq";

    // ---------- Exchange constant ----------
    public static final String IDENTITY_SERVICE_EXCHANGE = "x.service.identity";
    public static final String PROFILE_EVENTS_EXCHANGE = "x.profile.events";
    public static final String USER_DEAD_LETTER_EXCHANGE = "x.user.dlx";

    // ---------- Routing key constant ----------
    public static final String USER_CREATED_RK = "user.created";
    public static final String PROFILE_CREATED_STATUS_RK = "profile.created.*";
    public static final String USER_DEAD_RK = "user.dead";

    private RabbitMQConstantConfig() {}
}
