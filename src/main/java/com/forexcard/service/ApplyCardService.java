package com.forexcard.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.forexcard.dto.UserDetailsDTO;
import com.forexcard.model.User;
import com.forexcard.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplyCardService {
	private static final String UPLOAD_DIR = "uploads/salary-slips/";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	public void applyForCard(Integer id, String address, String state, String country, String gender,
			LocalDate dob, Long phoneNumber, String pan,double salary, MultipartFile file) throws IOException {
		Optional<User> optionalUser = getUserById(id);
		if (optionalUser.isEmpty()) {
			throw new IllegalArgumentException("User not found.");
		}

		User user = optionalUser.get();

		// Set user fields
		user.setAddress(address);
		user.setState(state);
		user.setCountry(country);
		user.setGender(gender);
		user.setDob(dob);
		user.setPhonenumber(phoneNumber);
		user.setPan(pan);
		user.setSalary(salary);
		user.setAdminAction("PENDING");

		// Handle file upload
		File dir = new File(UPLOAD_DIR);
		if (!dir.exists()) dir.mkdirs();

		String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
		Path filepath = Paths.get(UPLOAD_DIR + filename);
		Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

		String relativePath = "/uploads/salary-slips/" + filename;
		user.setFilePath(relativePath);

		userRepository.save(user); // or userRepository.save(user);
		emailService.sendCardApplicationConfirmation(user.getEmail());
	}

	private Optional<User> getUserById(Integer id) {
		
		return userRepository.findById(id);
	}


}
