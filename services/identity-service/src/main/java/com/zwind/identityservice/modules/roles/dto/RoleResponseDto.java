package com.zwind.identityservice.modules.roles.dto;

import com.zwind.identityservice.modules.permissions.dto.PermissionResponseDto;
import com.zwind.identityservice.modules.permissions.entity.Permission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponseDto {
    String name;
    String description;
    List<PermissionResponseDto> permissions;
}
