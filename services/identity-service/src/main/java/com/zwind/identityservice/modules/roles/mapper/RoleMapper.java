package com.zwind.identityservice.modules.roles.mapper;

import com.zwind.identityservice.modules.roles.dto.CreateRoleDto;
import com.zwind.identityservice.modules.roles.dto.RoleResponseDto;
import com.zwind.identityservice.modules.roles.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(CreateRoleDto createRoleDto);

    RoleResponseDto toRoleResponse(Role role);
}
