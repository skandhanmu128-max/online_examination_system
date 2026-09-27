package com.examiq.onlineexam.service;

import com.examiq.onlineexam.dto.LoginRequest;
import com.examiq.onlineexam.dto.RegisterRequest;
import com.examiq.onlineexam.dto.UserDto;
import com.examiq.onlineexam.model.User;
import com.examiq.onlineexam.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getEmail(),
                request.getRole()
        );
        userRepository.save(user);
        return new UserDto(user);
    }

    public User authenticate(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!user.isActive()) {
            throw new IllegalStateException("Account is disabled. Please contact administrator.");
        }
        return user;
    }
}
