package com.example.SkyBook.dto.response;

import com.example.SkyBook.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PassengerResponseDto(

        Long passengerId,
        String firstName,
        String lastName,
        Gender gender,
        LocalDate dateOfBirth,
        String passportNumber,
        String nationality,
        Long bookingId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}