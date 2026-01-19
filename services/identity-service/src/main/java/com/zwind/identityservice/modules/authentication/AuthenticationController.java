package com.zwind.identityservice.modules.authentication;

import com.zwind.identityservice.dto.response.ApiResponse;
import com.zwind.identityservice.modules.authentication.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;

    @GetMapping("/session/active")
    public ApiResponse<List<SessionResponse>> sessionActive() {
        return ApiResponse.<List<SessionResponse>>builder()
                .code(HttpStatus.OK.name())
                .result(authenticationService.findAllActiveToken())
                .build();
    }

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponseDto> authenticated(
            @Valid @RequestBody AuthenticationRequestDto authenticationRequestDto,
            HttpServletRequest request
    ) {
        return ApiResponse.<AuthenticationResponseDto>builder()
                .code(HttpStatus.OK.name())
                .result(authenticationService.authenticate(authenticationRequestDto, request))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponseDto> refresh(
            @Valid @RequestBody RefreshTokenRequestDto requestDto,
            HttpServletRequest request
    ) {
        return ApiResponse.<AuthenticationResponseDto>builder()
                .code(HttpStatus.OK.name())
                .result(authenticationService.refreshToken(requestDto, request))
                .build();
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequestDto requestDto) {
        authenticationService.logout(requestDto);
    }
}
