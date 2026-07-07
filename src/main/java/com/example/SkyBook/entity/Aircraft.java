package com.example.SkyBook.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aircraft")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model_no", nullable = false)
    private String modelNo;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
}