package com.forexcard.test;



import com.forexcard.model.*;
import com.forexcard.repo.*;
import com.forexcard.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private ForexCardRepository forexCardRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private ForexCard card;
    private Currency currency;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        card = new ForexCard();
        card.setId(1L);
        card.setPin("1234");
        card.setBalance(5000.0);
        card.setStatus("ACTIVE");

        currency = new Currency();
        currency.setCode("USD");
        currency.setExchangeRate(1.0); // Assume 1 USD = 1 unit base

        transaction = new Transaction();
        transaction.setForexCard(card);
        transaction.setCurrency(currency);
        transaction.setAmount(100.0);
        transaction.setMerchant("Amazon");
    }

    @Test
    void testProcessTransaction_Success() {
        transaction.getForexCard().setPin("1234");

        when(forexCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(currency));

        ResponseEntity<?> response = transactionService.processTransaction(transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transaction successful!", response.getBody());
        verify(transactionRepository).save(any(Transaction.class));
        verify(forexCardRepository).save(any(ForexCard.class));
    }

    @Test
    void testProcessTransaction_InvalidCardOrCurrency() {
        when(forexCardRepository.findById(1L)).thenReturn(Optional.empty());
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(currency));

        ResponseEntity<?> response = transactionService.processTransaction(transaction);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid card or currency.", response.getBody());
        verify(transactionRepository).save(any(Transaction.class));
    }

  

    @Test
    void testProcessTransaction_CardBlocked() {
        card.setStatus("BLOCKED");

        when(forexCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(currency));

        ResponseEntity<?> response = transactionService.processTransaction(transaction);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Card Blocked.", response.getBody());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testProcessTransaction_InsufficientFunds() {
        card.setBalance(50.0); // not enough after conversion + 100 fee

        when(forexCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(currency));

        ResponseEntity<?> response = transactionService.processTransaction(transaction);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Insufficient balance.", response.getBody());
        verify(transactionRepository).save(any(Transaction.class));
    }
}
