package com.example.SkyBook.controller;

import com.example.SkyBook.dto.request.UserProfileUpdateDto;
import com.example.SkyBook.dto.request.UserRequestDto;
import com.example.SkyBook.dto.request.UserRoleUpdateDto;
import com.example.SkyBook.dto.response.UserResponseDto;
import com.example.SkyBook.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class UserController {

    private final UserService userService;

    // Admin-only now — real signups go through POST /api/auth/register.
    // This exists only so an admin can provision an account directly
    // (e.g. creating another admin) without going through self-registration.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto request) {
        return userService.createUser(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelf(#userId, authentication)")
    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PreAuthorize("@userSecurity.isSelf(#userId, authentication)")
    @PutMapping("/{userId}")
    public UserResponseDto updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileUpdateDto request,
            Authentication authentication) {

        return userService.updateUser(userId, request, authentication.getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/role")
    public UserResponseDto updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UserRoleUpdateDto request) {

        return userService.updateUserRole(userId, request.role());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}