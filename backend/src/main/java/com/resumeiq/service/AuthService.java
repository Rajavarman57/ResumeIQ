package com.resumeiq.service;

import com.resumeiq.dto.LoginRequest;
import com.resumeiq.dto.LoginResponse;
import com.resumeiq.model.Recruiter;
import com.resumeiq.repository.RecruiterRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final RecruiterRepository recruiters;
    private final PasswordEncoder encoder;
    private final Map<String, Recruiter> sessions = new ConcurrentHashMap<>();

    public AuthService(RecruiterRepository recruiters, PasswordEncoder encoder) {
        this.recruiters = recruiters;
        this.encoder = encoder;
    }

    public LoginResponse login(LoginRequest request) {
        Recruiter recruiter = recruiters.findByEmail(request.email())
                .filter(user -> encoder.matches(request.password(), user.getPasswordHash()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        String token = UUID.randomUUID().toString();
        sessions.put(token, recruiter);
        return new LoginResponse(token, recruiter.getName(), recruiter.getEmail(), recruiter.getRole());
    }

    public Optional<Recruiter> findByToken(String token) {
        return Optional.ofNullable(sessions.get(token));
    }
}
