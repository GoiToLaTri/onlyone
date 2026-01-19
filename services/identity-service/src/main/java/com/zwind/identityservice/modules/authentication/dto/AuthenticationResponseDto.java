package com.zwind.identityservice.modules.authentication.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponseDto {
    String token;
    String refreshToken;
    boolean authenticated;
}
