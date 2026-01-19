package com.zwind.identityservice.modules.accounts.entity;

import com.zwind.identityservice.enums.AccountStatus;
import com.zwind.identityservice.modules.authentication.entity.Session;
import com.zwind.identityservice.modules.roles.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String password;

    @Column(updatable = false)
    String email;

    @Column(nullable = false)
    String provider;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    AccountStatus accountStatus;

    @Column(nullable = false)
    boolean isBlock;

    @Column(nullable = false, updatable = false)
    LocalDate createAt;

    @ManyToMany
    Set<Role> roles;

    @OneToMany
    Set<Session> sessions;

    @PrePersist
    protected void onCreate(){
        if(createAt == null)
            createAt = LocalDate.now();

        if(provider == null)
            provider = "onlyone";

        if(accountStatus == null)
            accountStatus = AccountStatus.PENDING;

        if(isBlock)
            isBlock = false;
    }
}
