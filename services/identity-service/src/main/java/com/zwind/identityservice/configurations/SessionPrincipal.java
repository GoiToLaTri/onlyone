package com.zwind.identityservice.configurations;

import com.zwind.identityservice.enums.AuthLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
@Builder
public class SessionPrincipal implements Serializable {
   String userId;
   AuthLevel authLevel;
}
