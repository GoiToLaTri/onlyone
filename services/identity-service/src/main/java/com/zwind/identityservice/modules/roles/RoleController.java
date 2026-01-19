package com.zwind.identityservice.modules.roles;

import com.zwind.identityservice.dto.response.ApiResponse;
import com.zwind.identityservice.modules.accounts.dto.AccountResponseDto;
import com.zwind.identityservice.modules.roles.dto.CreateRoleDto;
import com.zwind.identityservice.modules.roles.dto.GrantRoleRequestDto;
import com.zwind.identityservice.modules.roles.dto.RevokeRoleRequestDto;
import com.zwind.identityservice.modules.roles.dto.RoleResponseDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponseDto> create(@Valid @RequestBody CreateRoleDto createRoleDto) {
        return ApiResponse.<RoleResponseDto>builder()
                .code(HttpStatus.CREATED.name())
                .result(roleService.create(createRoleDto))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponseDto>> findAll() {
        return ApiResponse.<List<RoleResponseDto>>builder()
                .code(HttpStatus.OK.name())
                .result(roleService.findAll())
                .build();
    }

    @PatchMapping("/granted")
    public ApiResponse<AccountResponseDto> grantRole(@Valid @RequestBody GrantRoleRequestDto requestDto) {
        return ApiResponse.<AccountResponseDto>builder()
                .code(HttpStatus.OK.name())
                .result(roleService.grantRole(requestDto))
                .build();
    }

    @PatchMapping("/revoked")
    public ApiResponse<AccountResponseDto> revokeRole(@Valid @RequestBody RevokeRoleRequestDto requestDto) {
        return ApiResponse.<AccountResponseDto>builder()
                .code(HttpStatus.OK.name())
                .result(roleService.revokeRole(requestDto))
                .build();
    }
}
