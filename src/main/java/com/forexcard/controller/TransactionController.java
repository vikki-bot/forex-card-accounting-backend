package com.forexcard.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forexcard.dto.TransactionDTO;
import com.forexcard.model.Transaction;
import com.forexcard.service.TransactionService;
import com.forexcard.service.UserService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserService userService;
    
    // Endpoint to process a transaction (deduct amount)
    @PostMapping("/process")
    public ResponseEntity<?> makeTransaction(@RequestBody Transaction request) {
        return transactionService.processTransaction(request); // Delegate to the service layer
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUserId(@PathVariable("userId") Integer userId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }
  
  @GetMapping("/transactionsByDate")
  public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
          @RequestParam("userId") Integer userId,
          @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
          @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

      List<TransactionDTO> transactions = transactionService.getTransactionsByDateRange(userId, startDate, endDate);
      return ResponseEntity.ok(transactions);
  }
}
