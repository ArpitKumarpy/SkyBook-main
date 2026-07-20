package com.example.SkyBook.scheduler;

import com.example.SkyBook.entity.Flight;
import com.example.SkyBook.entity.Schedule;
import com.example.SkyBook.enums.FlightStatus;
import com.example.SkyBook.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Time-based flight status automation.
 *
 * IMPORTANT CAVEAT: status lives on Flight, not on an individual Schedule,
 * but one Flight can have many Schedules (recurring departures on different
 * dates — see Flight.schedules). That means "departed" can't correctly be
 * decided from a single schedule's time; it's only safe to auto-mark a
 * flight DEPARTED once none of its schedules are still in the future.
 *
 * This only automates the SCHEDULED -> DEPARTED transition. DELAYED and
 * CANCELLED aren't handled here because nothing in the data model (as
 * provided) represents *why* a flight would be delayed/cancelled — that
 * needs an explicit trigger (ops input, an external feed, etc.), not a
 * clock. If FlightStatus has an ARRIVED value and product wants flights to
 * move to it automatically too, add a similar check using arrivalTime.
 *
 * Assumption flagged: FlightStatus.SCHEDULED / FlightStatus.DEPARTED are
 * inferred from the frontend's status dropdown (SCHEDULED, DELAYED,
 * CANCELLED, DEPARTED) — the enums/ package itself wasn't provided, so
 * double-check these constant names compile against the real enum.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FlightStatusScheduler {

    private final FlightRepository flightRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateDepartedFlights() {
        LocalDateTime now = LocalDateTime.now();

        List<Flight> scheduledFlights = flightRepository.findAll().stream()
                .filter(flight -> flight.getStatus() == FlightStatus.SCHEDULED)
                .toList();

        for (Flight flight : scheduledFlights) {
            List<Schedule> schedules = flight.getSchedules();
            if (schedules == null || schedules.isEmpty()) continue;

            boolean allDeparted = schedules.stream()
                    .allMatch(schedule -> LocalDateTime.of(schedule.getDepartureDate(), schedule.getDepartureTime()).isBefore(now));

            if (allDeparted) {
                flight.setStatus(FlightStatus.DEPARTED);
                flightRepository.save(flight);
                log.info("Flight {} marked DEPARTED — all {} schedule(s) have passed", flight.getFlightNumber(), schedules.size());
            }
        }
    }
}
