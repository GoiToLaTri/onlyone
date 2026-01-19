package com.zwind.identityservice.modules.permissions;

import com.zwind.identityservice.exception.AppError;
import com.zwind.identityservice.exception.AppException;
import com.zwind.identityservice.modules.permissions.dto.CreatePermissionDto;
import com.zwind.identityservice.modules.permissions.dto.GrantPermissionRequestDto;
import com.zwind.identityservice.modules.permissions.dto.PermissionResponseDto;
import com.zwind.identityservice.modules.permissions.dto.RevokePermissionRequestDto;
import com.zwind.identityservice.modules.permissions.entity.Permission;
import com.zwind.identityservice.modules.permissions.mapper.PermissionMapper;
import com.zwind.identityservice.modules.permissions.repositiory.PermissionRepository;
import com.zwind.identityservice.modules.roles.entity.Role;
import com.zwind.identityservice.modules.roles.repositiory.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    RoleRepository roleRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponseDto create(CreatePermissionDto createPermissionDto) {
        return permissionMapper.toPermissionResponseDto(permissionRepository
                .save(permissionMapper.toPermission(createPermissionDto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponseDto> findAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponseDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponseDto findByName(String name) {
        return permissionMapper.toPermissionResponseDto(permissionRepository.findByName(name).orElseThrow(
                () -> new AppException(AppError.PERMISSION_NOT_EXISTS)
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponseDto findById(String id) {
        return permissionMapper.toPermissionResponseDto(
                permissionRepository.findById(id).orElseThrow(
                        () -> new AppException(AppError.PERMISSION_NOT_EXISTS)
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Role grantPermission(GrantPermissionRequestDto requestDto){
        Role role = roleRepository.findByName(requestDto.getRole())
                .orElseThrow(() -> new AppException(AppError.ROLE_NOT_EXISTS, requestDto.getRole()));

        Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllByNameIn(requestDto.getPermissions()));

        if(permissions.size() != requestDto.getPermissions().size()) {
            Set<String> existingNames = permissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());

            Set<String> missingNames = requestDto.getPermissions().stream()
                    .filter(p -> !existingNames.contains(p))
                    .collect(Collectors.toSet());

            throw new AppException(AppError.PERMISSION_NOT_EXISTS, missingNames.toString());
        }
        role.getPermissions().addAll(permissions);

        return roleRepository.save(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Role revokePermission(RevokePermissionRequestDto requestDto) {
        Role role = roleRepository.findByName(requestDto.getRole())
                .orElseThrow(() -> new AppException(AppError.ROLE_NOT_EXISTS));

        Set<String> rolePermissionNames = role.getPermissions().stream().map(
                Permission::getName).collect(Collectors.toSet());

        Set<String> requestPermissions = requestDto.getPermissions();

        if(!rolePermissionNames.containsAll(requestPermissions)) {
            AtomicReference<Set<String>> missingPermissions = new AtomicReference<>(
                    new HashSet<>(requestPermissions));
            missingPermissions.get().removeAll(rolePermissionNames);

            throw new AppException(AppError.PERMISSION_NOT_EXISTS_IN_ROLE,
                    requestPermissions.toString(), role.getName());
        }

        role.getPermissions().removeIf(permission
                -> requestPermissions.contains(permission.getName()));

        return roleRepository.save(role);
    }
}
