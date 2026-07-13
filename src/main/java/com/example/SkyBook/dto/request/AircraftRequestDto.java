package com.example.SkyBook.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AircraftRequestDto {

    @NotBlank(message = "Model number is required")
    @Size(max = 50, message = "Model number cannot exceed 50 characters")
    private String modelNo;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be greater than 0")
    private Integer totalSeats;
}