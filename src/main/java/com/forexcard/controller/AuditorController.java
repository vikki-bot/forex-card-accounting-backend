package com.forexcard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forexcard.dto.AuditorTransactionDTO;
import com.forexcard.dto.TransactionDTO;
import com.forexcard.model.ForexCard;
import com.forexcard.model.Transaction;
import com.forexcard.model.User;
import com.forexcard.repo.ForexCardRepository;
import com.forexcard.repo.UserRepository;
import com.forexcard.service.AuditorService;

@RestController
@RequestMapping("/auditor")
public class AuditorController {
	
	@Autowired
	private AuditorService auditorService;
	
	
	
	
	@GetMapping("/alltransaction")
	public ResponseEntity<List<AuditorTransactionDTO>> findallTransaction(){
		List<AuditorTransactionDTO> allTransaction = auditorService.findallTransaction();
		
		return allTransaction.isEmpty()
				?ResponseEntity.status(HttpStatus.NOT_FOUND).body(allTransaction)
				:ResponseEntity.ok(allTransaction);
	}
	
    @GetMapping("/finduserbyname")
    public List<AuditorTransactionDTO> findUserByName(@RequestParam("name") String name) {
        return auditorService.getTransactionsByUserName(name);
    }

}
