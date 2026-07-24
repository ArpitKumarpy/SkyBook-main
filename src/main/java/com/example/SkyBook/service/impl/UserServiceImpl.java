package com.example.SkyBook.service.impl;

import com.example.SkyBook.dto.request.UserProfileUpdateDto;
import com.example.SkyBook.dto.request.UserRequestDto;
import com.example.SkyBook.dto.response.UserResponseDto;
import com.example.SkyBook.entity.User;
import com.example.SkyBook.enums.Role;
import com.example.SkyBook.exception.AccessDeniedException;
import com.example.SkyBook.exception.DuplicateResourceException;
import com.example.SkyBook.exception.ResourceNotFoundException;
import com.example.SkyBook.mapper.UserMapper;
import com.example.SkyBook.repository.UserRepository;
import com.example.SkyBook.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto createUser(UserRequestDto request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists.");
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new DuplicateResourceException("Phone number already exists.");
        }

        User user = UserMapper.fromRequestDto(request);
        // fromRequestDto stores the raw password as-is — encode it here
        // before persisting, same as AuthenticationServiceImpl.register does.
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toResponseDto).toList();
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));
        return UserMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserProfileUpdateDto request, String requesterEmail) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));

        if (!user.getEmail().equals(requesterEmail)) {
            throw new AccessDeniedException("You can only update your own profile.");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        User updatedUser = userRepository.save(user);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto updateUserRole(Long userId, Role role) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));

        user.setRole(role);

        User updatedUser = userRepository.save(user);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));
        userRepository.delete(user);
    }
}