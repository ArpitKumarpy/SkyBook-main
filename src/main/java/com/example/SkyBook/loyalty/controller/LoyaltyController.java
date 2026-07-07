package com.example.SkyBook.loyalty.controller;

import com.example.SkyBook.loyalty.dto.LoyaltyAccountDto;
import com.example.SkyBook.loyalty.service.LoyaltyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty")
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/users/{userId}")
    public LoyaltyAccountDto getAccount(@PathVariable Long userId) {
        return loyaltyService.getAccount(userId);
    }

    @PostMapping("/users/{userId}/points")
    public LoyaltyAccountDto earnPoints(@PathVariable Long userId, @RequestParam int points) {
        return loyaltyService.earnPoints(userId, points);
    }

    @PostMapping("/users/{userId}/coupon")
    public LoyaltyAccountDto applyCoupon(@PathVariable Long userId, @RequestParam String code) {
        return loyaltyService.applyCoupon(userId, code);
    }
}
