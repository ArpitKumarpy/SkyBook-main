package com.example.SkyBook.repository;

import com.example.SkyBook.entity.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    
    boolean existsByModelNo(String modelNo);

}