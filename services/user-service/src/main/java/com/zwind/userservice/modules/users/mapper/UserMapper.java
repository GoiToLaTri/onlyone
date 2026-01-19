package com.zwind.userservice.modules.users.mapper;

import com.zwind.userservice.modules.users.dto.CreateProfileDto;
import com.zwind.userservice.modules.users.dto.UserResponseDto;
import com.zwind.userservice.modules.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateProfileDto createProfileDto);

    @Mapping(target = "createdAt", source = "createdAt")
    UserResponseDto toUserResponse(User user);
}
