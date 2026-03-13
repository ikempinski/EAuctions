package com.example.demo.services;

import com.example.demo.dto.Request.RegisterRequest;
import com.example.demo.dto.Request.LoginRequest;
import com.example.demo.dto.Request.PasswordChangeRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        return toResponse(user);
    }

    /**
     * Validates credentials and returns user response if valid (for session creation).
     * Returns empty if email not found or password does not match.
     */
    public Optional<UserResponse> login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            log.info("Login failed: no user found for email={}", request.getEmail());
            return Optional.empty();
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.info("Login failed: password mismatch for email={}", request.getEmail());
            return Optional.empty();
        }
        return Optional.of(toResponse(user));
    }

    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserService::toResponse);
    }

    @Transactional
    public Optional<UserResponse> updateProfile(Long userId, String email, String username) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setEmail(email);
                    user.setUsername(username);
                    return toResponse(user);
                });
    }

    @Transactional
    public boolean changePassword(Long userId, PasswordChangeRequest request) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
                        return false;
                    }
                    user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
                    return true;
                })
                .orElse(false);
    }

    private static UserResponse toResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setEmail(user.getEmail());
        r.setUsername(user.getUsername());
        r.setPasswordHash(null);
        return r;
    }
}
