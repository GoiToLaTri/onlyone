package com.zwind.userservice.modules.users;

import com.zwind.userservice.modules.users.dto.CreateProfileDto;
import com.zwind.userservice.modules.users.dto.UserResponseDto;
import com.zwind.userservice.modules.users.repository.UserRepository;
import com.zwind.userservice.modules.users.entity.User;
import com.zwind.userservice.modules.users.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public UserResponseDto create(CreateProfileDto createProfileDto) {
        User user = userMapper.toUser(createProfileDto);
        User result = userRepository.save(user);
    
        return userMapper.toUserResponse(result);
    }

    public UserResponseDto findByPublicId(String publicId) {
        return userMapper.toUserResponse(userRepository.findByPublicId(publicId)
                .orElse(null));
    }

    public UserResponseDto findByAccountId(String id) {
        return userMapper.toUserResponse(userRepository.findByAccountId(id)
                .orElse(null));
    }

    public UserResponseDto getInfo() {
        var context = SecurityContextHolder.getContext();
        log.info(":::: context: {}", context.toString());
        return userMapper.toUserResponse(userRepository
            .findByAccountId(context.getAuthentication().getPrincipal().toString())
            .orElse(null));
    }
}
