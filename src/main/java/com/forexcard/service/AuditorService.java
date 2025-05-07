package com.forexcard.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.forexcard.dto.AuditorTransactionDTO;
import com.forexcard.dto.TransactionDTO;
import com.forexcard.model.ForexCard;
import com.forexcard.model.Transaction;
import com.forexcard.model.User;
import com.forexcard.repo.ForexCardRepository;
import com.forexcard.repo.TransactionRepository;
import com.forexcard.repo.UserRepository;

@Service
public class AuditorService {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ForexCardRepository forexCardRepository;
	

	
	public List<AuditorTransactionDTO> findallTransaction(){
		return transactionRepository.findAllTransactions();
	}

	public List<AuditorTransactionDTO> getTransactionsByUserName(String name) {
        User user = userRepository.findByName(name);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        ForexCard card = forexCardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Forex card not found for user"));

        List<AuditorTransactionDTO> transactions = transactionRepository.findByForexCardId(card.getId());

        return transactions;
		
	}

}
