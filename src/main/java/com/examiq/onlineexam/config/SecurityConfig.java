package com.examiq.onlineexam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Allow H2 console
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/h2-console/**", "/", "/index.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                .requestMatchers("/api/admin/**").permitAll() // Custom session check in controllers
                .requestMatchers("/api/student/**").permitAll() // Custom session check in controllers
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
