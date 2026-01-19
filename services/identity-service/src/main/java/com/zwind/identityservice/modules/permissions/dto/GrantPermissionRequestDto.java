package com.zwind.identityservice.modules.permissions.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GrantPermissionRequestDto {
    @NotBlank(message = "ROLE_NAME_REQUIRED")
    String role;
    Set<String> permissions;
}
