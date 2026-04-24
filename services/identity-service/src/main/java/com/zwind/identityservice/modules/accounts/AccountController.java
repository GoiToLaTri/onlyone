package com.zwind.identityservice.modules.accounts;

import com.zwind.common_lib.dto.response.ApiResponse;
import com.zwind.identityservice.modules.accounts.dto.AccountResponseDto;
import com.zwind.identityservice.modules.accounts.dto.CreateAccountDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountController {
    AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    ApiResponse<AccountResponseDto> create(@Valid @RequestBody CreateAccountDto createAccountDto) {
        return ApiResponse.<AccountResponseDto>builder()
                .code(HttpStatus.ACCEPTED.name())
                .result(accountService.create(createAccountDto))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<AccountResponseDto> findById(@PathVariable("id") String id) {
        return ApiResponse.<AccountResponseDto>builder()
                .code(HttpStatus.OK.name())
                .result(accountService.findById(id))
                .build();
    }

    @GetMapping("/info")
    ApiResponse<AccountResponseDto> info() {
        return ApiResponse.<AccountResponseDto>builder()
                .code(HttpStatus.OK.name())
                .result(accountService.accountInfo())
                .build();
    }

    @GetMapping
    ApiResponse<List<AccountResponseDto>> findAll() {
        return ApiResponse.<List<AccountResponseDto>>builder()
                .code(HttpStatus.OK.name())
                .result(accountService.findAll())
                .build();
    }
}
