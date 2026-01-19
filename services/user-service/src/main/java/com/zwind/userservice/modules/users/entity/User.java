package com.zwind.userservice.modules.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    @Email()
    String email;

    @Column(nullable = false)
    String accountId;

    @Column(nullable = false)
    String publicId;

    LocalDate dayOfBirth;

    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        if(publicId == null)
            this.publicId = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12);

        if(createdAt == null)
            this.createdAt = LocalDateTime.now();
    }
}
