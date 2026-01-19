package com.zwind.userservice.modules.users.dto;

import com.zwind.userservice.enums.ProfileStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProfileResponse {
    String correlationId;
    ProfileStatus status;
    String userId;
}
