package com.tms.dto.user;

import com.tms.model.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Set;

public record UserUpdateRoleRequestDto(
        @NotNull @Positive
        Long userId,
        @NotEmpty
        Set<Role.RoleName> roles
) {
}
