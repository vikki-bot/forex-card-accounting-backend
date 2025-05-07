package com.forexcard.test;



import com.forexcard.dto.UserLoginDTO;
import com.forexcard.exception.AuthenticationFailedException;
import com.forexcard.model.User;
import com.forexcard.repo.UserRepository;
import com.forexcard.service.AuthService;
import com.forexcard.util.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserLoginDTO userLoginDTO;

    @BeforeEach
    void setUp() {
        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("test@example.com");
        userLoginDTO.setPassword("hashedPassword");
    }

    @Test
    void login_successful() {
        when(userRepository.findLoginDataByEmail("test@example.com"))
                .thenReturn(Optional.of(userLoginDTO));
        when(passwordEncoder.matches("rawPassword", "hashedPassword"))
                .thenReturn(true);

        UserLoginDTO result = authService.login("test@example.com", "rawPassword");

        assertThat(result).isEqualTo(userLoginDTO);
    }

    @Test
    void login_invalidPassword_throwsException() {
        when(userRepository.findLoginDataByEmail("test@example.com"))
                .thenReturn(Optional.of(userLoginDTO));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword"))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.login("test@example.com", "wrongPassword"))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Invalid email or password.");
    }

    @Test
    void login_nonExistingUser_throwsException() {
        when(userRepository.findLoginDataByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("nonexistent@example.com", "password"))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Invalid email or password.");
    }

    @Test
    void getUserByEmail_returnsUserIfExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = authService.getUserByEmail("test@example.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUserByEmail_returnsNullIfNotExists() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        User result = authService.getUserByEmail("unknown@example.com");

        assertThat(result).isNull();
    }

    @Test
    void getTokenForUser_returnsJwtToken() {
        when(jwtUtil.generateToken("test@example.com")).thenReturn("mocked-jwt-token");

        String token = authService.getTokenForUser(userLoginDTO);

        assertThat(token).isEqualTo("mocked-jwt-token");
    }
}
