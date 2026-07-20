package com.example.SkyBook.dto.request;

import com.example.SkyBook.enums.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PassengerRequestDto(

        @NotBlank(message = "First name is required")
        @Size(max = 50)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50)
        String lastName,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotBlank(message = "Passport number is required")
        @Size(min = 6, max = 20)
        String passportNumber,

        @NotBlank(message = "Nationality is required")
        String nationality

) {
}