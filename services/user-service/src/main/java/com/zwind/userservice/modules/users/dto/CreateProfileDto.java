package com.zwind.userservice.modules.users.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProfileDto {
    String name;
    String email;
    String accountId;
}
