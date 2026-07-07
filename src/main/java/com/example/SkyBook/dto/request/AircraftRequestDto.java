package com.example.SkyBook.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AircraftRequestDto {

    @NotBlank(message = "Model number is required")
    private String modelNo;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be greater than 0")
    private Integer totalSeats;
}