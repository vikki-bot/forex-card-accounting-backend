package com.forexcard.test;



import com.forexcard.dto.PendingUserDTO;
import com.forexcard.exception.ResourceNotFoundException;
import com.forexcard.model.ForexCard;
import com.forexcard.model.User;
import com.forexcard.repo.ForexCardRepository;
import com.forexcard.repo.UserRepository;
import com.forexcard.service.AdminService;
import com.forexcard.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private ForexCardRepository cardRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdminService adminService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setAdminAction("PENDING");
    }

    @Test
    void approveUser_shouldApproveAndCreateCard() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(cardRepo.save(any(ForexCard.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepo.save(any(User.class))).thenReturn(user);

        String result = adminService.approveUser(1);

        assertThat(result).isEqualTo("User approved and card created.");
        assertThat(user.getAdminAction()).isEqualTo("APPROVED");
        assertThat(user.getForexCard()).isNotNull();
        assertThat(user.getForexCard().getStatus()).isEqualTo("INACTIVE");
        verify(emailService).sendCardApprovalConfirmation("user@example.com");
    }

    @Test
    void approveUser_shouldThrowIfUserNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.approveUser(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with ID");
    }

    @Test
    void denyUser_shouldSetRejectedAndSendEmail() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        String result = adminService.denyUser(1);

        assertThat(result).isEqualTo("User application has been denied.");
        assertThat(user.getAdminAction()).isEqualTo("REJECTED");
        verify(emailService).sendCardRejectionEmail("user@example.com");
    }

    @Test
    void getPendingUsers_shouldReturnPendingList() {
        List<PendingUserDTO> mockList = List.of(new PendingUserDTO());
        when(userRepo.findPendingUsers("PENDING")).thenReturn(mockList);

        List<PendingUserDTO> result = adminService.getPendingUsers();

        assertThat(result).hasSize(1);
    }

    @Test
    void getUploadedDocument_shouldReturnFileBytes() throws IOException {
        String dummyPath = "/uploads/test.pdf";
        user.setFilePath(dummyPath);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        Path realPath = Paths.get("." + dummyPath);
        byte[] mockBytes = "test content".getBytes();

        // Mocking file read
        Files.createDirectories(realPath.getParent());
        Files.write(realPath, mockBytes);

        byte[] result = adminService.getUploadedDocument(1);

        assertThat(result).isEqualTo(mockBytes);

        // Cleanup
        Files.deleteIfExists(realPath);
    }

    @Test
    void getUploadedDocument_shouldThrowIfFileMissing() {
        user.setFilePath("/uploads/missing.pdf");
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> adminService.getUploadedDocument(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File not found");
    }

    @Test
    void getUploadedDocument_shouldThrowIfFilePathNull() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user)); // no filePath set

        assertThatThrownBy(() -> adminService.getUploadedDocument(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No file found");
    }
}
