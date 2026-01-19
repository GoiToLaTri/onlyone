package com.zwind.identityservice.modules.accounts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAccountDto {
    @NotBlank(message = "NAME_REQUIRED")
    @Size(min = 1, message = "NAME_INVALID")
    String name;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 4, message = "PASSWORD_INVALID")
    String password;

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    String email;
}
