package com.zwind.identityservice.modules.authentication.mapper;

import com.zwind.identityservice.modules.authentication.dto.SessionStage;
import com.zwind.identityservice.modules.authentication.entity.Session;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionMapper {
    SessionStage toSessionStage(Session session);
}
