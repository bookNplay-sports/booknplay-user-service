package com.booknplay.user.controller;

import com.booknplay.user.entity.User;
import com.booknplay.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.booknplay.user.dto.UserDto;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/users")
@Tag(
        name = "User Management",
        description = "APIs for user registration, login, and profile management"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Allows a new user to register for an account."
    )
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }
    @Operation(
            summary = "Login to the application",
            description = "Allows a registered user to login to the application."
    )
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(
            @RequestParam String email,
            @RequestParam String password) {
        return ResponseEntity.ok(
                userService.login(email, password));
    }
    @Operation(
            summary = "Get user profile",
            description = "Retrieves the profile information for a specific user."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getProfile(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                userService.getProfile(userId));
    }
    @Operation(
            summary = "Update user profile",
            description = "Updates the profile information for a specific user."
    )
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateProfile(
            @PathVariable Long userId,
            @RequestBody User user) {
        return ResponseEntity.ok(
                userService.updateProfile(userId, user));
    }
    @Operation(
            summary = "Reset user password",
            description = "Allows a user to request a password reset."
    )
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok("Password reset request received");
    }
    @Operation(
            summary = "Confirm password reset",
            description = "Allows a user to confirm a password reset."
    )
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> confirmPasswordReset(
            @RequestParam String token,
            @RequestParam String newPassword) {
        userService.confirmPasswordReset(token, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }
}
