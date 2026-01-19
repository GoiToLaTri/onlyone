package com.zwind.identityservice.configurations;

import com.zwind.identityservice.constant.PredefinedData;
import com.zwind.identityservice.enums.AccountStatus;
import com.zwind.identityservice.enums.Permissions;
import com.zwind.identityservice.enums.Roles;
import com.zwind.identityservice.modules.accounts.entity.Account;
import com.zwind.identityservice.modules.accounts.repository.AccountRepository;
import com.zwind.identityservice.modules.permissions.entity.Permission;
import com.zwind.identityservice.modules.permissions.repositiory.PermissionRepository;
import com.zwind.identityservice.modules.roles.entity.Role;
import com.zwind.identityservice.modules.roles.repositiory.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )
    ApplicationRunner applicationRunner(AccountRepository accountRepository,
                                        RoleRepository roleRepository,
                                        PermissionRepository permissionRepository) {
        log.info("Initializing application.....");

        return args -> {
            Set<Permission> permissions = new HashSet<>();
            for(Permissions p: Permissions.values()){
                if(!permissionRepository.existsByName(p.name())) {
                    Permission permission = Permission.builder()
                            .name(p.name())
                            .description("Permission for " + p.name())
                            .roles(new HashSet<>())
                            .build();

                    permissionRepository.save(permission);
                    permissions.add(permission);
                }
            }

            var roles = new HashSet<Role>();
            if(!roleRepository.existsByName(Roles.ADMIN.name())){
                Role adminRole = Role.builder().name(Roles.ADMIN.name())
                        .description("Role for ADMIN")
                        .permissions(permissions)
                        .build();
                roleRepository.save(adminRole);
                roles.add(adminRole);

                for (Permission permission : permissions) {
                    permission.getRoles().add(adminRole);
                    permissionRepository.save(permission);
                }
            }

            if(!roleRepository.existsByName(Roles.USER.name())) {
                Role userRole = Role.builder()
                        .name(Roles.USER.name())
                        .description("Role for USER")
                        .build();

                roleRepository.save(userRole);
            }

            if(!accountRepository.existsByEmail(PredefinedData.ACC_EMAIL)){
                Account adminAccount = Account.builder()
                        .email(PredefinedData.ACC_EMAIL)
                        .password(passwordEncoder.encode(PredefinedData.ACC_PASSWORD))
                        .accountStatus(AccountStatus.ACTIVE)
                        .roles(roles)
                        .build();

                accountRepository.save(adminAccount);
            }
            log.warn("Admin account has been created with default password: admin, please change it ");

            log.info("Application initialization completed .....");
        };
    }
}
