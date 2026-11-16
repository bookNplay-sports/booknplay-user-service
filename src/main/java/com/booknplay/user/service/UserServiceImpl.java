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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetRepository passwordResetRepository;
    private final EmailService emailService;
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           PasswordResetRepository passwordResetRepository,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetRepository = passwordResetRepository;
        this.emailService = emailService;
    }
    @Override
    public UserDto register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        emailService.sendRegistrationEmail(user.getEmail(), user.getName());
        return toUserDto(savedUser);
    }
    @Override
    public UserDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return toUserDto(user);
    }
    @Override
    public UserDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
        return toUserDto(user);
    }
    @Override
    public UserDto updateProfile(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
        existingUser.setName(updatedUser.getName());
        existingUser.setPhone(updatedUser.getPhone());
        User savedUser = userRepository.save(existingUser);
        emailService.sendProfileUpdateEmail(savedUser.getEmail(), savedUser.getName());
        return toUserDto(savedUser);
    }
    @Override
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
        passwordResetRepository.deleteByUserId(user.getId());
        PasswordReset resetToken = PasswordReset.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        passwordResetRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetToken.getToken());
    }
    @Override

    public void confirmPasswordReset(String token, String newPassword) {

        PasswordReset resetToken = passwordResetRepository

                .findByToken(token)

                .orElseThrow(() ->

                        new InvalidResetTokenException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            passwordResetRepository.delete(resetToken);

            throw new InvalidResetTokenException("Password reset token has expired");

        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        passwordResetRepository.delete(resetToken);

    }

    private UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}