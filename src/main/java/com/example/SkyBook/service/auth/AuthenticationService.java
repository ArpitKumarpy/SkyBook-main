package com.example.SkyBook.service.auth;

import com.example.SkyBook.dto.auth.AuthResponseDto;
import com.example.SkyBook.dto.auth.LoginRequestDto;
import com.example.SkyBook.dto.auth.RegisterRequestDto;

public interface AuthenticationService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);

}