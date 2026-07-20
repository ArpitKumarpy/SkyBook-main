package com.example.SkyBook.service.interfaces;

import com.example.SkyBook.dto.request.UserProfileUpdateDto;
import com.example.SkyBook.dto.request.UserRequestDto;
import com.example.SkyBook.dto.response.UserResponseDto;
import com.example.SkyBook.enums.Role;


import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto request);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long userId);
    void deleteUser(Long userId);
    UserResponseDto updateUser(Long userId, UserProfileUpdateDto request, String requesterEmail);
    UserResponseDto updateUserRole(Long userId, Role role);

}