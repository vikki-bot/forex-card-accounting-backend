package com.forexcard.test;



import com.forexcard.model.User;
import com.forexcard.repo.UserRepository;
import com.forexcard.service.ForgotPasswordService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

class ForgotPasswordServiceTest {

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
    }

    @Test
    void testResetPassword_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("hashedPass");

        forgotPasswordService.resetPassword("test@example.com", "newPass");

        verify(userRepository).save(user);
        verify(passwordEncoder).encode("newPass");
    }

    @Test
    void testResetPassword_UserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Should not throw, just do nothing
        forgotPasswordService.resetPassword("notfound@example.com", "newPass");

        verify(userRepository, never()).save(any());
    }
}

