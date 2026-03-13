package com.example.demo.controller;

import com.example.demo.dto.Request.RegisterRequest;
import com.example.demo.dto.Request.LoginRequest;
import com.example.demo.dto.Request.PasswordChangeRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.security.AuthUserPrincipal;
import com.example.demo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return userService.login(request)
                .map(user -> {
                    var principal = new AuthUserPrincipal(user.getId(), user.getEmail(), user.getUsername());
                    var auth = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(auth);
                    SecurityContextHolder.setContext(context);
                    HttpSession session = httpRequest.getSession(true);
                    session.setAttribute("SPRING_SECURITY_CONTEXT", context);
                    log.info("Session created for user: {}", user.getEmail());
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(a -> a.isAuthenticated() && !"anonymousUser".equals(a.getPrincipal()))
                .map(a -> a.getPrincipal())
                .filter(p -> p instanceof AuthUserPrincipal)
                .map(p -> (AuthUserPrincipal) p)
                .map(AuthUserPrincipal::getEmail)
                .flatMap(userService::findByEmail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateMe(@RequestBody UserResponse body) {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(a -> a.isAuthenticated() && !"anonymousUser".equals(a.getPrincipal()))
                .map(a -> a.getPrincipal())
                .filter(p -> p instanceof AuthUserPrincipal)
                .map(p -> (AuthUserPrincipal) p)
                .flatMap(principal -> userService.updateProfile(principal.getId(), body.getEmail(), body.getUsername()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(a -> a.isAuthenticated() && !"anonymousUser".equals(a.getPrincipal()))
                .map(a -> a.getPrincipal())
                .filter(p -> p instanceof AuthUserPrincipal)
                .map(p -> (AuthUserPrincipal) p)
                .map(principal -> {
                    boolean changed = userService.changePassword(principal.getId(), request);
                    return changed ? ResponseEntity.ok().<Void>build() : ResponseEntity.badRequest().build();
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
