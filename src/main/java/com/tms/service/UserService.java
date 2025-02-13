package com.tms.service;

import com.tms.dto.user.UserRegistrationRequestDto;
import com.tms.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto registration(UserRegistrationRequestDto requestDto);
}
