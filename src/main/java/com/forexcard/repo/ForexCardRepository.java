package com.forexcard.repo;




import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.forexcard.model.ForexCard;

@Repository
public interface ForexCardRepository extends JpaRepository<ForexCard, Long> {

	

	 Optional<ForexCard> findByUserId(Integer userId);

	Optional<ForexCard> findByCardNumber(String cardNumber);  
	
	@Query("SELECT f.id FROM ForexCard f WHERE f.user.id = :userId")
	Optional<Long> findCardIdByUserId(@Param("userId") Integer userId);


    
}

