package com.example.SkyBook.dto.response;

import com.example.SkyBook.enums.Role;
import java.time.LocalDateTime;

public record UserResponseDto(

        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}