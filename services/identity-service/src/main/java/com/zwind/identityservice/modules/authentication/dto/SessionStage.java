package com.zwind.identityservice.modules.authentication.dto;

import com.zwind.identityservice.enums.AuthLevel;
import com.zwind.identityservice.enums.SessionStatus;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionStage {
    String userId;
    AuthLevel authLevel;
    DeviceDetails deviceDetails;
    int riskScore;
    SessionStatus status;
    Long refreshTokenId;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    @Data
    public static class DeviceDetails {
        private String userAgent;      // UA thô
        private String acceptLang;
        private String platform;       // Sec-CH-UA-Platform
        private String platformVersion;// Sec-CH-UA-Platform-Version
        private String architecture;    // Sec-CH-UA-Arch
        private String model;
        private String canvasId;       // Fingerprint từ JS
        private String screenRes;      // Độ phân giải màn hình
    }
}
