package com.tms.mapper;

import com.tms.config.MapperConfig;
import com.tms.dto.user.UserRegistrationRequestDto;
import com.tms.dto.user.UserResponseDto;
import com.tms.dto.user.UserUpdateRoleRequestDto;
import com.tms.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    void updateUser(UserRegistrationRequestDto requestDtoDto, @MappingTarget User user);

    @Mapping(target = "roles", ignore = false)
    void updateRoles(UserUpdateRoleRequestDto requestDto, @MappingTarget User user);
}
