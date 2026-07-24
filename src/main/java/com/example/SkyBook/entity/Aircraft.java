package com.example.SkyBook.entity;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(
            mappedBy = "aircraft",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
 
    private List<Seat> seats = new ArrayList<>();
 
}