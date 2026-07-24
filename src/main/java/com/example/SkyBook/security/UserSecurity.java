package com.example.SkyBook.security;

import com.example.SkyBook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isSelf(Long userId, Authentication authentication) {
        if (authentication == null) return false;
        return userRepository.findByEmail(authentication.getName())
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }
}