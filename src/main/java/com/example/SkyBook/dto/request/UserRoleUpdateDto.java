package com.example.SkyBook.dto.request;

import com.example.SkyBook.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateDto(
        @NotNull(message = "Role is required")
        Role role
) {
}