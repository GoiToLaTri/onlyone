package com.zwind.identityservice.dto.response;

import com.zwind.identityservice.enums.ProfileStatus;
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
    String accountId;
}
