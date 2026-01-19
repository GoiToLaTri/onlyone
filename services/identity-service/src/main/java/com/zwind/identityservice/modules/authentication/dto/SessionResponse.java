package com.zwind.identityservice.modules.authentication.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionResponse {
    Long id;
    String model;
    String devicePlatform;
    LocalDateTime lastActive;
    boolean isCurrent;
}
