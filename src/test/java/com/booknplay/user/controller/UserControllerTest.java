package com.booknplay.user.controller;

import com.booknplay.user.dto.UserDto;
import com.booknplay.user.entity.User;
import com.booknplay.user.service.UserService;
import com.booknplay.user.utility.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void registerReturnsCreatedUser() {
        User user = User.builder()
                .name("Alex Example")
                .email("alex@example.com")
                .password("password")
                .role(Role.CUSTOMER)
                .phone("555-0100")
                .build();
        UserDto userDto = userDto();
        when(userService.register(user)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.register(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(userDto, response.getBody());
        verify(userService).register(user);
    }

    @Test
    void loginReturnsAuthenticatedUser() {
        UserDto userDto = userDto();
        when(userService.login("alex@example.com", "password")).thenReturn(userDto);

        ResponseEntity<UserDto> response =
                userController.login("alex@example.com", "password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(userDto, response.getBody());
        verify(userService).login("alex@example.com", "password");
    }

    @Test
    void getProfileReturnsRequestedUser() {
        UserDto userDto = userDto();
        when(userService.getProfile(42L)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getProfile(42L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(userDto, response.getBody());
        verify(userService).getProfile(42L);
    }

    @Test
    void updateProfileReturnsUpdatedUser() {
        User user = User.builder()
                .name("Alex Updated")
                .email("alex@example.com")
                .password("password")
                .role(Role.CUSTOMER)
                .phone("555-0101")
                .build();
        UserDto userDto = userDto();
        when(userService.updateProfile(42L, user)).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.updateProfile(42L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(userDto, response.getBody());
        verify(userService).updateProfile(42L, user);
    }

    @Test
    void resetPasswordReturnsConfirmationMessage() {
        ResponseEntity<String> response =
                userController.resetPassword("alex@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset request received", response.getBody());
        verify(userService).resetPassword("alex@example.com");
    }

    @Test
    void confirmPasswordResetReturnsSuccessMessage() {
        ResponseEntity<String> response =
                userController.confirmPasswordReset("reset-token", "new-password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset successfully", response.getBody());
        verify(userService).confirmPasswordReset("reset-token", "new-password");
    }

    private UserDto userDto() {
        return UserDto.builder()
                .id(42L)
                .name("Alex Example")
                .email("alex@example.com")
                .phone("555-0100")
                .role(Role.CUSTOMER)
                .build();
    }
}
