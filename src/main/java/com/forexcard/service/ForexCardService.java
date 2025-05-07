package com.forexcard.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.forexcard.model.ForexCard;
import com.forexcard.model.User;
import com.forexcard.repo.ForexCardRepository;
import com.forexcard.repo.UserRepository;
import com.forexcard.exception.ResourceNotFoundException;
import com.forexcard.exception.OperationNotAllowedException;
import com.forexcard.exception.InvalidPinException;

@Service
public class ForexCardService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ForexCardRepository cardRepo;

    @Autowired
    private EmailService emailService;

    public String activateCard(Integer userId, String pin) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!"APPROVED".equalsIgnoreCase(user.getAdminAction())) {
            throw new OperationNotAllowedException("User is not yet approved by admin.");
        }

        ForexCard card = user.getForexCard();
        if (card == null) {
            throw new ResourceNotFoundException("No Forex Card associated with user.");
        }

        if ("BLOCKED".equalsIgnoreCase(card.getStatus())) {
        	 card.setPin(pin);
             card.setStatus("ACTIVATED");
             cardRepo.save(card);
             emailService.sendCardActivationConfirmation(user.getEmail());
             return "Card Unblocked Successfully.";
           
        }

        card.setPin(pin);
        card.setStatus("ACTIVATED");

        setCardBalanceAndLimits(user, card);

        cardRepo.save(card);
        emailService.sendCardActivationConfirmation(user.getEmail());

        return "Card activated successfully.";
    }

    private void setCardBalanceAndLimits(User user, ForexCard card) {
        double salary = user.getSalary();
        if (salary >= 50000 && salary < 100000) {
            card.setBalance(100000.0);
            card.setMaxLimit(100000.0);
        } else if (salary >= 100000 && salary < 150000) {
            card.setBalance(200000.0);
            card.setMaxLimit(200000.0);
        } else if (salary >= 150000) {
            card.setBalance(300000.0);
            card.setMaxLimit(300000.0);
        }
    }

    public String blockCardByCardId(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        ForexCard forexCard = user.getForexCard();

        if (forexCard == null) {
            throw new ResourceNotFoundException("No Forex Card associated with user.");
        }

        if ("BLOCKED".equalsIgnoreCase(forexCard.getStatus())) {
            throw new OperationNotAllowedException("Card is already blocked.");
        }

        forexCard.setStatus("BLOCKED");
        cardRepo.save(forexCard);
        emailService.sendCardBlockedNotification(user.getEmail());

        return "Forex Card has been successfully blocked.";
    }

    public ResponseEntity<String> activateCard(String cardNumber, String pin) {
        ForexCard card = cardRepo.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with number: " + cardNumber));

        User user = card.getUser();

        if (card.getPin() == null || card.getPin().isEmpty()) {
            throw new OperationNotAllowedException("PIN is not set. Please set your PIN before activation.");
        }

        if (!card.getPin().equals(pin)) {
            throw new InvalidPinException("Invalid PIN. Please try again.");
        }

        card.setStatus("ACTIVATED");
        cardRepo.save(card);
        emailService.sendCardActivationConfirmation(user.getEmail());

        return ResponseEntity.ok("Card activated successfully.");
    }

    public ForexCard getCardByUserId(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        ForexCard card = user.getForexCard();

        if (card == null) {
            throw new ResourceNotFoundException("Forex Card not found for this user.");
        }

        return card;
    }
}
