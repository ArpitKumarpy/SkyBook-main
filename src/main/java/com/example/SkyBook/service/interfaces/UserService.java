package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.UserRequestDto;
import com.example.SkyBook.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto request);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long userId);
    UserResponseDto updateUser(Long userId, UserRequestDto request);
    void deleteUser(Long userId);

}