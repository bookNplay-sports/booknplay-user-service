package com.booknplay.user.repository;

import com.booknplay.user.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository  extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(String token);
    Optional<PasswordReset> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
