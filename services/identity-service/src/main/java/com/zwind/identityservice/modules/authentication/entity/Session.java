package com.zwind.identityservice.modules.authentication.entity;

import com.zwind.identityservice.enums.AuthLevel;
import com.zwind.identityservice.enums.SessionStatus;
import com.zwind.identityservice.modules.authentication.dto.SessionStage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    String token;

    @Column(unique = true, nullable = false)
    String accessToken;
    String userId;

    @Enumerated(EnumType.STRING)
    AuthLevel authLevel;
    SessionStage.DeviceDetails deviceDetails;
    int riskScore;

    @Enumerated(EnumType.STRING)
    SessionStatus status;
    boolean isConsumed;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private LocalDateTime expireTime;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
