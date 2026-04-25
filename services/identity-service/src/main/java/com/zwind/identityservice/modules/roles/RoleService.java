package com.zwind.identityservice.modules.roles;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.zwind.common_lib.exception.HttpError;
import com.zwind.common_lib.exception.HttpException;
import com.zwind.identityservice.modules.accounts.dto.AccountResponseDto;
import com.zwind.identityservice.modules.accounts.entity.Account;
import com.zwind.identityservice.modules.accounts.mapper.AccountMapper;
import com.zwind.identityservice.modules.accounts.repository.AccountRepository;
import com.zwind.identityservice.modules.roles.dto.CreateRoleDto;
import com.zwind.identityservice.modules.roles.dto.GrantRoleRequestDto;
import com.zwind.identityservice.modules.roles.dto.RevokeRoleRequestDto;
import com.zwind.identityservice.modules.roles.dto.RoleResponseDto;
import com.zwind.identityservice.modules.roles.entity.Role;
import com.zwind.identityservice.modules.roles.mapper.RoleMapper;
import com.zwind.identityservice.modules.roles.repositiory.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    AccountRepository accountRepository;
    AccountMapper accountMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponseDto create(CreateRoleDto createRoleDto) {
        Role role = roleMapper.toRole(createRoleDto);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponseDto> findAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponseDto grantRole(GrantRoleRequestDto requestDto) {
        String currentAdminID = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();

        Account account = accountRepository.findById(requestDto.getAccountId())
                .orElseThrow(() -> new HttpException(HttpError.USER_NOT_EXISTS));

        if(currentAdminID.equals(account.getId()))
            throw new HttpException(HttpError.CANNOT_CHANGE_SELF_ROLE);

        Set<Role> roles = new HashSet<>(roleRepository.findAllByNameIn(requestDto.getRoles()));
        if(roles.size() != requestDto.getRoles().size()){
            Set<String> existRoles = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            Set<String> missingNames = requestDto.getRoles().stream()
                    .filter(r -> !existRoles.contains(r))
                    .collect(Collectors.toSet());

            throw new HttpException(HttpError.ROLE_NOT_EXISTS, missingNames.toString());
        }
        account.getRoles().addAll(roles);

        return accountMapper.toAccountResponse(accountRepository.save(account));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponseDto revokeRole(RevokeRoleRequestDto requestDto){
        String currentAdminID = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();

        Account account = accountRepository.findById(requestDto.getAccountId())
                .orElseThrow(() -> new HttpException(HttpError.USER_NOT_EXISTS));

        if(currentAdminID.equals(account.getId()))
            throw new HttpException(HttpError.CANNOT_CHANGE_SELF_ROLE);

        Set<String> roleNames = account.getRoles().stream()
                .map(Role::getName).collect(Collectors.toSet());

        Set<String> notExistsRoles = requestDto.getRoles().stream()
                .filter(r -> !roleNames.contains(r)).collect(Collectors.toSet());

        if(!notExistsRoles.isEmpty())
            throw new HttpException(HttpError.USER_NOT_EXISTS_ROLES,
                    account.getId(), notExistsRoles.toString());

        account.getRoles().removeIf(role
                -> requestDto.getRoles().contains(role.getName()));

        return accountMapper.toAccountResponse(accountRepository.save(account));
    }
}
