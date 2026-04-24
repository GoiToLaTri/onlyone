package com.zwind.userservice.modules.users;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zwind.common_lib.dto.response.ApiResponse;
import com.zwind.userservice.modules.users.dto.UserResponseDto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @GetMapping("/info")
    public ApiResponse<UserResponseDto> getInfo() {
        return ApiResponse.<UserResponseDto>builder()
                .code(HttpStatus.OK.name())
                .result(userService.getInfo())
                .build();
    }
    
}
