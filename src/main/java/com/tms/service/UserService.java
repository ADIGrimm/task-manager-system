package com.tms.service;

import com.tms.dto.user.UserRegistrationRequestDto;
import com.tms.dto.user.UserResponseDto;
import com.tms.dto.user.UserUpdateRoleRequestDto;

public interface UserService {
    UserResponseDto registration(UserRegistrationRequestDto requestDto);

    UserResponseDto updateRoles(UserUpdateRoleRequestDto requestDto);
    
    UserResponseDto getInfo(Long userId);

    UserResponseDto updateInfo(Long userId, UserRegistrationRequestDto requestDto);
}
