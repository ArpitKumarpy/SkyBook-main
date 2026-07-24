package com.example.SkyBook.controller;

import com.example.SkyBook.dto.response.TravelHistoryResponseDto;
import com.example.SkyBook.dto.response.TravelStatisticsResponseDto;
import com.example.SkyBook.entity.Booking;
import com.example.SkyBook.entity.Flight;
import com.example.SkyBook.entity.TravelHistory;
import com.example.SkyBook.entity.User;
import com.example.SkyBook.enums.TravelStatus;

import com.example.SkyBook.service.interfaces.TravelHistoryService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/travel-history")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/" )

public class TravelHistoryController {
    private final TravelHistoryService travelHistoryService;
    //travel history of user
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{userId}")

    public List<TravelHistoryResponseDto> getTravelHistory(@PathVariable Long userId){
        return travelHistoryService.getTravelHistory(userId);
    }
    //travel statistics
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/statistics/{userId}")
    public TravelStatisticsResponseDto getTravelStatistics(
            @PathVariable Long userId) {

        return travelHistoryService.getTravelStatistics(userId);
    }


    //visited cities
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/cities/{userId}")

    public List<String> getVisitedCities(@PathVariable Long userId){
        return travelHistoryService.getVisitedCities(userId);
    }

    //last destination
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/last-location/{userId}")
    public String getLastDestination(
            @PathVariable Long userId) {

        return travelHistoryService.getLastDestination(userId);
    }



}
