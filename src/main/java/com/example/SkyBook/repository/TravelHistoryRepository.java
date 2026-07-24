package com.example.SkyBook.repository;

import com.example.SkyBook.entity.TravelHistory;
import com.example.SkyBook.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TravelHistoryRepository extends JpaRepository<TravelHistory, Long> {

    List<TravelHistory> findByUser(User user);

}

