package com.zwind.identityservice.modules.permissions;

import com.zwind.identityservice.dto.response.ApiResponse;
import com.zwind.identityservice.modules.permissions.dto.CreatePermissionDto;
import com.zwind.identityservice.modules.permissions.dto.GrantPermissionRequestDto;
import com.zwind.identityservice.modules.permissions.dto.PermissionResponseDto;
import com.zwind.identityservice.modules.permissions.dto.RevokePermissionRequestDto;
import com.zwind.identityservice.modules.roles.dto.RoleResponseDto;
import com.zwind.identityservice.modules.roles.entity.Role;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponseDto> create(
            @Valid @RequestBody CreatePermissionDto createPermissionDto
    ){
        return ApiResponse.<PermissionResponseDto>builder()
                .code(HttpStatus.CREATED.name())
                .result(permissionService.create(createPermissionDto))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponseDto>> findAll(){
        return ApiResponse.<List<PermissionResponseDto>>builder()
                .code(HttpStatus.OK.name())
                .result(permissionService.findAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionResponseDto> findById(@PathVariable("id") String id){
        return ApiResponse.<PermissionResponseDto>builder()
                .code(HttpStatus.OK.name())
                .result(permissionService.findById(id))
                .build();
    }

    @PatchMapping("/granted")
    public ApiResponse<Role> grantedPermission(@Valid @RequestBody GrantPermissionRequestDto requestDto){
        return ApiResponse.<Role>builder()
                .code(HttpStatus.OK.name())
                .result(permissionService.grantPermission(requestDto))
                .build();
    }

    @PatchMapping("/revoked")
    public ApiResponse<Role> revokedPermission(@Valid @RequestBody RevokePermissionRequestDto requestDto){
        return ApiResponse.<Role>builder()
                .code(HttpStatus.OK.name())
                .result(permissionService.revokePermission(requestDto))
                .build();
    }
}
