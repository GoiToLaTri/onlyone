package com.zwind.identityservice.modules.accounts.mapper;

import com.zwind.identityservice.modules.accounts.dto.AccountResponseDto;
import com.zwind.identityservice.modules.accounts.dto.CreateAccountDto;
import com.zwind.identityservice.modules.accounts.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toAccount(CreateAccountDto createAccountDto);

    AccountResponseDto toAccountResponse(Account account);
}
