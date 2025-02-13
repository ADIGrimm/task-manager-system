package com.tms.controller;

import com.tms.dto.user.UserLoginRequestDto;
import com.tms.dto.user.UserLoginResponseDto;
import com.tms.dto.user.UserRegistrationRequestDto;
import com.tms.dto.user.UserResponseDto;
import com.tms.security.AuthenticationService;
import com.tms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Registration user",
            description = "Registers user and returns a part of request data on success")
    public UserResponseDto registration(@Valid @RequestBody UserRegistrationRequestDto requestDto) {
        return userService.registration(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user",
            description = "Authenticates a user and returns JWT token")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
