package com.forexcard.test;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.forexcard.exception.InvalidPinException;
import com.forexcard.exception.OperationNotAllowedException;
import com.forexcard.exception.ResourceNotFoundException;
import com.forexcard.model.ForexCard;
import com.forexcard.model.User;
import com.forexcard.repo.ForexCardRepository;
import com.forexcard.repo.UserRepository;
import com.forexcard.service.EmailService;
import com.forexcard.service.ForexCardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class ForexCardServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private ForexCardRepository cardRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ForexCardService forexCardService;

    private User user;
    private ForexCard card;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setAdminAction("APPROVED");
        user.setEmail("user@example.com");
        user.setSalary(120000.0);

        card = new ForexCard();
        card.setCardNumber("123456");
        card.setStatus("PENDING");
        user.setForexCard(card);
        card.setUser(user);
    }

    @Test
    public void testActivateCard_UserApproved_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(cardRepo.save(any(ForexCard.class))).thenReturn(card);

        String result = forexCardService.activateCard(1, "1234");

        assertEquals("Card activated successfully.", result);
        assertEquals("ACTIVATED", card.getStatus());
        verify(emailService).sendCardActivationConfirmation("user@example.com");
    }

    @Test
    public void testActivateCard_UserNotApproved_ThrowsException() {
        user.setAdminAction("PENDING");
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        assertThrows(OperationNotAllowedException.class, () -> {
            forexCardService.activateCard(1, "1234");
        });
    }

    @Test
    public void testActivateBlockedCard_Success() {
        card.setStatus("BLOCKED");
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        String result = forexCardService.activateCard(1, "5678");

        assertEquals("Card Unblocked Successfully.", result);
        assertEquals("ACTIVATED", card.getStatus());
        verify(cardRepo).save(card);
        verify(emailService).sendCardActivationConfirmation("user@example.com");
    }

    @Test
    public void testBlockCard_Success() {
        card.setStatus("ACTIVATED");
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(cardRepo.save(any())).thenReturn(card);

        String result = forexCardService.blockCardByCardId(1);

        assertEquals("Forex Card has been successfully blocked.", result);
        assertEquals("BLOCKED", card.getStatus());
    }

    @Test
    public void testBlockCard_AlreadyBlocked_ThrowsException() {
        card.setStatus("BLOCKED");
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        assertThrows(OperationNotAllowedException.class, () -> {
            forexCardService.blockCardByCardId(1);
        });
    }

    @Test
    public void testActivateCardByCardNumber_ValidPin_Success() {
        card.setPin("1234");
        when(cardRepo.findByCardNumber("123456")).thenReturn(Optional.of(card));

        ResponseEntity<String> response = forexCardService.activateCard("123456", "1234");

        assertEquals("Card activated successfully.", response.getBody());
        assertEquals("ACTIVATED", card.getStatus());
        verify(emailService).sendCardActivationConfirmation("user@example.com");
    }

    @Test
    public void testActivateCardByCardNumber_InvalidPin_ThrowsException() {
        card.setPin("1234");
        when(cardRepo.findByCardNumber("123456")).thenReturn(Optional.of(card));

        assertThrows(InvalidPinException.class, () -> {
            forexCardService.activateCard("123456", "0000");
        });
    }

    @Test
    public void testActivateCardByCardNumber_PinNotSet_ThrowsException() {
        card.setPin(null);
        when(cardRepo.findByCardNumber("123456")).thenReturn(Optional.of(card));

        assertThrows(OperationNotAllowedException.class, () -> {
            forexCardService.activateCard("123456", "1234");
        });
    }

    @Test
    public void testGetCardByUserId_Success() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        ForexCard result = forexCardService.getCardByUserId(1);

        assertEquals(card, result);
    }

    @Test
    public void testGetCardByUserId_NoCard_ThrowsException() {
        user.setForexCard(null);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        assertThrows(ResourceNotFoundException.class, () -> {
            forexCardService.getCardByUserId(1);
        });
    }
}

