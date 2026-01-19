package com.zwind.identityservice.modules.permissions.mapper;

import com.zwind.identityservice.modules.permissions.dto.CreatePermissionDto;
import com.zwind.identityservice.modules.permissions.dto.PermissionResponseDto;
import com.zwind.identityservice.modules.permissions.entity.Permission;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(CreatePermissionDto createPermissionDto);

    PermissionResponseDto toPermissionResponseDto(Permission permission);
}
