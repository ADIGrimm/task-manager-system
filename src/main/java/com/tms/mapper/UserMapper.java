package com.tms.mapper;

import com.tms.config.MapperConfig;
import com.tms.dto.user.UserRegistrationRequestDto;
import com.tms.dto.user.UserResponseDto;
import com.tms.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
