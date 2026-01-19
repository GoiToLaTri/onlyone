package com.zwind.identityservice.modules.accounts.dto;

import com.zwind.identityservice.modules.roles.dto.RoleResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponseDto {
    String id;
    String email;
    String provider;
    Set<RoleResponseDto> roles;
}
