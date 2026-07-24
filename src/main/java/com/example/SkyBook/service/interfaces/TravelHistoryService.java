package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.response.TravelHistoryResponseDto;
import com.example.SkyBook.dto.response.TravelStatisticsResponseDto;

import java.util.List;

public interface TravelHistoryService {

    List<TravelHistoryResponseDto> getTravelHistory(Long userId);

    TravelStatisticsResponseDto getTravelStatistics(Long userId);

    List<String> getVisitedCities(Long userId);

    String getLastDestination(Long userId);
}
