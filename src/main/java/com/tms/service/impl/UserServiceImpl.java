package com.tms.service.impl;

import com.tms.dto.user.UserRegistrationRequestDto;
import com.tms.dto.user.UserResponseDto;
import com.tms.dto.user.UserUpdateRoleRequestDto;
import com.tms.exception.EntityNotFoundException;
import com.tms.exception.RegistrationException;
import com.tms.mapper.UserMapper;
import com.tms.model.Role;
import com.tms.model.User;
import com.tms.repository.role.RoleRepository;
import com.tms.repository.user.UserRepository;
import com.tms.service.UserService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDto registration(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new RegistrationException(
                    String.format("User with this email: %s already exists", requestDto.email()));
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setRoles(Set.of(roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find role " + Role.RoleName.ROLE_USER)
                )));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateRoles(UserUpdateRoleRequestDto requestDto) {
        User user = userRepository.findById(
                requestDto.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + requestDto.userId()
                ));
        Set<Role> updatedRoles = requestDto.roles().stream()
                .map(
                        roleName -> roleRepository.findByName(roleName).orElseThrow(
                                () -> new EntityNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        if (requestDto.roles().contains(Role.RoleName.ROLE_SUPER_ADMIN)) {
            throw new IllegalArgumentException(
                    "You cannot assign " + Role.RoleName.ROLE_SUPER_ADMIN
            );
        }
        userMapper.updateRoles(requestDto, user);
        user.setRoles(updatedRoles);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getInfo(Long userId) {
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user")));
    }

    @Override
    public UserResponseDto updateInfo(Long userId, UserRegistrationRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user"));
        userMapper.updateUser(requestDto, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
