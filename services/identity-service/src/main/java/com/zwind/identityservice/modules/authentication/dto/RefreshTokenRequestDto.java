package com.zwind.identityservice.modules.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenRequestDto {
    @NotBlank(message = "REQUEST_TOKEN_REQUIRED")
    String token;
}
