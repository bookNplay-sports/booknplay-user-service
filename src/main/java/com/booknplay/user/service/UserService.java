package com.booknplay.user.service;

import com.booknplay.user.dto.UserDto;
import com.booknplay.user.entity.User;

public interface UserService {

    UserDto register(User user);
    UserDto login(String email, String password);
    UserDto getProfile(Long userId);
    UserDto updateProfile(Long userId, User user);
    void resetPassword(String email);
    void confirmPasswordReset(String token, String newPassword);
}
