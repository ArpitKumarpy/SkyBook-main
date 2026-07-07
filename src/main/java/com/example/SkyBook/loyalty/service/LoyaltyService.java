package com.example.SkyBook.loyalty.service;

import com.example.SkyBook.loyalty.dto.LoyaltyAccountDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoyaltyService {
    private final Map<Long, LoyaltyAccountDto> accounts = new ConcurrentHashMap<>();

    public LoyaltyAccountDto getAccount(Long userId) {
        return accounts.computeIfAbsent(userId, id -> createDefaultAccount(id));
    }

    public LoyaltyAccountDto earnPoints(Long userId, int points) {
        LoyaltyAccountDto account = getAccount(userId);
        account.setPoints(account.getPoints() + points);
        account.setTier(resolveTier(account.getPoints()));
        return account;
    }

    public LoyaltyAccountDto applyCoupon(Long userId, String couponCode) {
        LoyaltyAccountDto account = getAccount(userId);
        account.setCoupon(couponCode);
        return account;
    }

    private LoyaltyAccountDto createDefaultAccount(Long userId) {
        LoyaltyAccountDto account = new LoyaltyAccountDto();
        account.setUserId(userId);
        account.setPoints(0);
        account.setTier("BRONZE");
        account.setCoupon("NONE");
        return account;
    }

    private String resolveTier(int points) {
        if (points >= 5000) {
            return "GOLD";
        }
        if (points >= 1000) {
            return "SILVER";
        }
        return "BRONZE";
    }
}
