package com.example.SkyBook.dto.auth;

public record AuthResponseDto(

        String token,
        String tokenType,
        Long userId,
        String email,
        String role

) {
}