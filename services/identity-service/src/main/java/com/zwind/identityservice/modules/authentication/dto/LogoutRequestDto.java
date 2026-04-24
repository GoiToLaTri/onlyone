package com.zwind.identityservice.modules.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogoutRequestDto {
    @NotBlank(message = "ACCESS_TOKEN_REQUIRED")
    String token;

    @NotBlank(message = "REFRESH_TOKEN_REQUIRED")
    String refreshToken;
}
