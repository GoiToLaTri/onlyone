package com.zwind.identityservice.event.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventPayload<T>{
    String eventId;
    Long timestamp;
    String pattern;
    T data;
}
