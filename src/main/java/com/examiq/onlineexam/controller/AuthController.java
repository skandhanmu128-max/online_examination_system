package com.examiq.onlineexam.controller;

import com.examiq.onlineexam.dto.*;
import com.examiq.onlineexam.model.User;
import com.examiq.onlineexam.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequest request) {
        try {
            UserDto user = authService.register(request);
            return ResponseEntity.ok(ApiResponse.ok("User registered successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        try {
            User user = authService.authenticate(request);
            HttpSession session = servletRequest.getSession(true);
            session.setAttribute("LOGGED_IN_USER", user);
            return ResponseEntity.ok(ApiResponse.ok("Login successful", new UserDto(user)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Active user session", new UserDto(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully", null));
    }
}
