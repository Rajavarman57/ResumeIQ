package com.resumeiq.service;

import com.resumeiq.dto.LoginRequest;
import com.resumeiq.model.Recruiter;
import com.resumeiq.repository.RecruiterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private final Map<String, Recruiter> store = new HashMap<>();
    private RecruiterRepository recruiters;
    private PasswordEncoder encoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        recruiters = (RecruiterRepository) Proxy.newProxyInstance(
                RecruiterRepository.class.getClassLoader(),
                new Class[]{RecruiterRepository.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("findByEmail".equals(method.getName()) && args != null && args.length == 1) {
                            return Optional.ofNullable(store.get(args[0]));
                        }
                        if (method.getReturnType().equals(Optional.class)) {
                            return Optional.empty();
                        }
                        if (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class)) {
                            return false;
                        }
                        return null;
                    }
                });
        encoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword == null ? null : rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword != null && rawPassword.toString().equals(encodedPassword);
            }
        };
        authService = new AuthService(recruiters, encoder);
    }

    @Test
    void loginReturnsValidSessionToken() {
        String email = "alice@example.com";
        String password = "secret";
        Recruiter recruiter = new Recruiter();
        recruiter.setName("Alice");
        recruiter.setEmail(email);
        recruiter.setPasswordHash("secret");
        recruiter.setRole("RECRUITER");
        store.put(email, recruiter);

        var response = authService.login(new LoginRequest(email, password));

        assertNotNull(response);
        assertNotNull(response.token());
        assertEquals("Alice", response.name());
        assertEquals(email, response.email());
        assertEquals("RECRUITER", response.role());
        assertTrue(authService.findByToken(response.token()).isPresent());
        assertSame(recruiter, authService.findByToken(response.token()).get());
    }

    @Test
    void loginThrowsWhenCredentialsAreInvalid() {
        String email = "bob@example.com";
        String password = "wrong";

        var request = new LoginRequest(email, password);
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }
}
