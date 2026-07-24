package com.example.SkyBook.entity;

import com.example.SkyBook.enums.TravelStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TravelHistory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="booking_id")
    private Booking booking;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="schedule_id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TravelStatus travelStatus;


    @Column(nullable = false)
    private LocalDateTime createdAt;

}
