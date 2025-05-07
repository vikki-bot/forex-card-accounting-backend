package com.forexcard.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forexcard.dto.AuditorTransactionDTO;
import com.forexcard.dto.TransactionDTO;
import com.forexcard.model.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

	@Query("SELECT t FROM Transaction t WHERE t.forexCard.id = :cardId AND t.date BETWEEN :startDate AND :endDate")
	List<TransactionDTO> findTransactionsByCardIdAndDateBetween(
	    @Param("cardId") Long cardId,
	    @Param("startDate") LocalDateTime startDateTime,
	    @Param("endDate") LocalDateTime endDateTime
	);

	@Query("SELECT new com.forexcard.dto.AuditorTransactionDTO( " +
		       "t.date, u.name, fc.cardNumber, t.merchant, c.name, " +
		       "t.currentBalance, t.deductAmount, t.amount, t.status) " +
		       "FROM Transaction t " +
		       "JOIN t.forexCard fc " +
		       "JOIN fc.user u " +
		       "JOIN t.currency c")
		List<AuditorTransactionDTO> findAllTransactions();
	
	@Query("SELECT new com.forexcard.dto.AuditorTransactionDTO( " +
		       "t.date, u.name, fc.cardNumber, t.merchant, c.name, " +
		       "t.currentBalance, t.deductAmount, t.amount, t.status) " +
		       "FROM Transaction t " +
		       "JOIN t.forexCard fc " +
		       "JOIN fc.user u " +
		       "JOIN t.currency c " +
		       "WHERE fc.id = :cardId")
	List<AuditorTransactionDTO> findByForexCardId(@Param("cardId") Long cardId);
	
	List<Transaction> findUserByForexCardId(Long forexCardId);

	
	

}