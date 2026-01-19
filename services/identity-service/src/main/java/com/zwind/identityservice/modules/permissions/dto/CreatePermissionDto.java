package com.zwind.identityservice.modules.permissions.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePermissionDto {

    @NotBlank(message = "PERMISSION_NAME_REQUIRED")
    String name;
    String description;
}
