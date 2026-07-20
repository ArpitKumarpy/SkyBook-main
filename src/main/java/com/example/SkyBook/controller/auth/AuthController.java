package com.example.SkyBook.controller.auth;

import com.example.SkyBook.dto.auth.AuthResponseDto;
import com.example.SkyBook.dto.auth.LoginRequestDto;
import com.example.SkyBook.dto.auth.RegisterRequestDto;
import com.example.SkyBook.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(
            @Valid @RequestBody RegisterRequestDto request) {

        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(
            @Valid @RequestBody LoginRequestDto request) {

        return authenticationService.login(request);
    }
}