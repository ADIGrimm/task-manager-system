package com.tms.controller;

import com.tms.dto.user.UserRegistrationRequestDto;
import com.tms.dto.user.UserResponseDto;
import com.tms.dto.user.UserUpdateRoleRequestDto;
import com.tms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Operations related to users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USER')")
public class UserController implements UserContextHelper {
    private final UserService userService;

    @PutMapping("/updateRole")
    @Operation(summary = "Update user roles",
            description = "Update user roles and returns OK status on success")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
    public void updateRoles(@Valid @RequestBody UserUpdateRoleRequestDto requestDto) {
        userService.updateRoles(requestDto);
    }

    @GetMapping("/info")
    @Operation(summary = "Get user info",
            description = "Return info of current user")
    public UserResponseDto getInfo(Authentication authentication) {
        return userService.getInfo(getUserId(authentication));
    }

    @PutMapping("/update")
    @Operation(summary = "Update user info",
            description = "Update user info and return info DTO of user")
    public UserResponseDto updateInfo(
            Authentication authentication,
            @Valid @RequestBody UserRegistrationRequestDto requestDto
    ) {
        return userService.updateInfo(getUserId(authentication), requestDto);
    }
}
