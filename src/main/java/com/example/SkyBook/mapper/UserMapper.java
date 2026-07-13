package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.UserRequestDto;
import com.example.SkyBook.dto.response.UserResponseDto;
import com.example.SkyBook.entity.User;

public class UserMapper {

    private UserMapper() {
    }

    public static User fromRequestDto(UserRequestDto dto) {

        return User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .password(dto.password())
                .build();
    }

    public static UserResponseDto toResponseDto(User user) {

        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}