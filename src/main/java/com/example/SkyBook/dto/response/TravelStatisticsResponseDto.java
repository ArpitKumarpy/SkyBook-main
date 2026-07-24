package com.example.SkyBook.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelStatisticsResponseDto {

    private Integer totalFlights;
    private String favouriteDestination;
    private String lastDestination;
    private List<String> citiesVisited;
}