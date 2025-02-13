package com.tms.dto.user;

import com.tms.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@FieldMatch(firstField = "password", secondField = "repeatPassword")
public record UserRegistrationRequestDto(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NotBlank
        String repeatPassword,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName
) {
}
