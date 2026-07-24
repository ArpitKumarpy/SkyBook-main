package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.response.TravelHistoryResponseDto;
import com.example.SkyBook.dto.response.TravelStatisticsResponseDto;
import com.example.SkyBook.entity.TravelHistory;
import com.example.SkyBook.entity.User;
import com.example.SkyBook.mapper.TravelHistoryMapper;
import com.example.SkyBook.repository.TravelHistoryRepository;
import com.example.SkyBook.repository.UserRepository;
import com.example.SkyBook.service.interfaces.TravelHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelHistoryServiceImpl implements TravelHistoryService {

    private final TravelHistoryRepository travelHistoryRepository;
    private final UserRepository userRepository;
    private final TravelHistoryMapper travelHistoryMapper;

    @Override
    public List<TravelHistoryResponseDto> getTravelHistory(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TravelHistory> travelHistoryList =
                travelHistoryRepository.findByUser(user);

        return travelHistoryList.stream()
                .map(travelHistoryMapper::toResponseDto)
                .toList();
    }


    @Override
    public TravelStatisticsResponseDto getTravelStatistics(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TravelHistory> travelHistoryList = travelHistoryRepository.findByUser(user);

        int totalFlights = travelHistoryList.size();

        String lastDestination = travelHistoryList.isEmpty()
                ? "N/A"
                : travelHistoryList.get(travelHistoryList.size() - 1)
                .getSchedule()
                .getFlight()
                .getDestination();

        Map<String, Long> destinationCount = travelHistoryList.stream()
                .collect(Collectors.groupingBy(
                        history -> history.getSchedule().getFlight().getDestination(),
                        Collectors.counting()));

        String favouriteDestination = destinationCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        List<String> citiesVisited = travelHistoryList.stream()
                .map(history -> history.getSchedule().getFlight().getDestination())
                .distinct()
                .toList();

        return TravelStatisticsResponseDto.builder()
                .totalFlights(totalFlights)
                .favouriteDestination(favouriteDestination)
                .lastDestination(lastDestination)
                .citiesVisited(citiesVisited)
                .build();
    }

    @Override
    public List<String> getVisitedCities(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return travelHistoryRepository.findByUser(user)
                .stream()
                .map(history -> history.getSchedule().getFlight().getDestination())
                .distinct()
                .toList();
    }

    @Override
    public String getLastDestination(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TravelHistory> travelHistoryList = travelHistoryRepository.findByUser(user);

        if (travelHistoryList.isEmpty()) {
            return "No Travel History";
        }

        return travelHistoryList.get(travelHistoryList.size() - 1)
                .getSchedule()
                .getFlight()
                .getDestination();
    }

}