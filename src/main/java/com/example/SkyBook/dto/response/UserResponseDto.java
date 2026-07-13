package com.example.SkyBook.dto.response;

import java.time.LocalDateTime;

public record UserResponseDto(

        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}