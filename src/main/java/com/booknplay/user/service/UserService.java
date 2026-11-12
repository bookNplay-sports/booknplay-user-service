package com.booknplay.user.service;

import com.booknplay.user.entity.User;

public interface UserService {

    User register(User user);
    User login(String email, String password);
    User getProfile(Long userId);
    User updateProfile(Long userId, User user);
    void resetPassword(String email);
}
