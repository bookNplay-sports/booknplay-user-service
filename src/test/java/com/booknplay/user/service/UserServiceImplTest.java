package com.booknplay.user.service;

import com.booknplay.user.dto.UserDto;
import com.booknplay.user.entity.PasswordReset;
import com.booknplay.user.entity.User;
import com.booknplay.user.exception.DuplicateEmailException;
import com.booknplay.user.exception.InvalidCredentialsException;
import com.booknplay.user.exception.InvalidResetTokenException;
import com.booknplay.user.exception.UserNotFoundException;
import com.booknplay.user.repository.PasswordResetRepository;
import com.booknplay.user.repository.UserRepository;
import com.booknplay.user.utility.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private EmailService emailService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository, passwordEncoder, passwordResetRepository, emailService);
    }

    @Test
    void registerEncodesPasswordSavesUserAndSendsEmail() {
        User user = user();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.register(user);

        assertUserDto(result);
        assertEquals("encoded-password", user.getPassword());
        verify(userRepository).save(user);
        verify(emailService).sendRegistrationEmail(user.getEmail(), user.getName());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        User user = user();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.register(user));

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder, emailService);
    }

    @Test
    void loginReturnsUserWhenCredentialsMatch() {
        User user = user();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(true);
        user.setPassword("encoded-password");

        UserDto result = userService.login(user.getEmail(), "plain-password");

        assertUserDto(result);
        verify(passwordEncoder).matches("plain-password", "encoded-password");
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login("missing@example.com", "password"));

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void loginRejectsIncorrectPassword() {
        User user = user();
        user.setPassword("encoded-password");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(user.getEmail(), "wrong-password"));
    }

    @Test
    void getProfileReturnsUser() {
        when(userRepository.findById(42L)).thenReturn(Optional.of(user()));

        UserDto result = userService.getProfile(42L);

        assertUserDto(result);
    }

    @Test
    void getProfileThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getProfile(42L));
    }

    @Test
    void updateProfileChangesNameAndPhoneAndSendsEmail() {
        User existingUser = user();
        existingUser.setPassword("encoded-password");
        User updates = User.builder().name("Updated Name").phone("555-0101").build();
        when(userRepository.findById(42L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto result = userService.updateProfile(42L, updates);

        assertEquals("Updated Name", result.getName());
        assertEquals("555-0101", result.getPhone());
        assertEquals("alex@example.com", result.getEmail());
        assertEquals("encoded-password", existingUser.getPassword());
        verify(emailService).sendProfileUpdateEmail("alex@example.com", "Updated Name");
    }

    @Test
    void updateProfileThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateProfile(42L, User.builder().build()));

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(emailService);
    }

    @Test
    void resetPasswordReplacesOldTokenAndSendsEmailWithNewToken() {
        User user = user();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        userService.resetPassword(user.getEmail());

        ArgumentCaptor<PasswordReset> tokenCaptor = ArgumentCaptor.forClass(PasswordReset.class);
        InOrder inOrder = inOrder(passwordResetRepository);
        inOrder.verify(passwordResetRepository).deleteByUserId(42L);
        inOrder.verify(passwordResetRepository).save(tokenCaptor.capture());
        PasswordReset savedToken = tokenCaptor.getValue();
        assertEquals(user, savedToken.getUser());
        assertNotNull(savedToken.getExpiryDate());
        UUID.fromString(savedToken.getToken());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now().plusMinutes(14)));
        assertTrue(savedToken.getExpiryDate().isBefore(LocalDateTime.now().plusMinutes(16)));
        verify(emailService).sendPasswordResetEmail(
                user.getEmail(), user.getName(), savedToken.getToken());
    }

    @Test
    void resetPasswordThrowsWhenEmailIsUnknown() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.resetPassword("missing@example.com"));

        verifyNoInteractions(passwordResetRepository, emailService);
    }

    @Test
    void confirmPasswordResetEncodesNewPasswordAndDeletesToken() {
        User user = user();
        PasswordReset resetToken = PasswordReset.builder()
                .token("valid-token")
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        when(passwordResetRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("new-password")).thenReturn("new-encoded-password");

        userService.confirmPasswordReset("valid-token", "new-password");

        assertEquals("new-encoded-password", user.getPassword());
        verify(userRepository).save(user);
        verify(passwordResetRepository).delete(resetToken);
    }

    @Test
    void confirmPasswordResetRejectsUnknownToken() {
        when(passwordResetRepository.findByToken("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidResetTokenException.class,
                () -> userService.confirmPasswordReset("unknown", "new-password"));

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void confirmPasswordResetDeletesExpiredTokenAndRejectsIt() {
        PasswordReset resetToken = PasswordReset.builder()
                .token("expired-token")
                .user(user())
                .expiryDate(LocalDateTime.now().minusSeconds(1))
                .build();
        when(passwordResetRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(resetToken));

        assertThrows(InvalidResetTokenException.class,
                () -> userService.confirmPasswordReset("expired-token", "new-password"));

        verify(passwordResetRepository).delete(resetToken);
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    private User user() {
        return User.builder()
                .id(42L)
                .name("Alex Example")
                .email("alex@example.com")
                .password("plain-password")
                .phone("555-0100")
                .role(Role.CUSTOMER)
                .build();
    }

    private void assertUserDto(UserDto result) {
        assertEquals(42L, result.getId());
        assertEquals("Alex Example", result.getName());
        assertEquals("alex@example.com", result.getEmail());
        assertEquals("555-0100", result.getPhone());
        assertEquals(Role.CUSTOMER, result.getRole());
    }
}
