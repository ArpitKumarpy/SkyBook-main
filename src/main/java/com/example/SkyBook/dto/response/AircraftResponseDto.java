package com.example.SkyBook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class AircraftResponseDto {

    private Long id;
    private String modelNo;
    private Integer totalSeats;
}