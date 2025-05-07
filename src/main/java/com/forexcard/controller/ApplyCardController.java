package com.forexcard.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.forexcard.dto.UserDetailsDTO;
import com.forexcard.model.User;
import com.forexcard.service.ApplyCardService;

@RestController
@RequestMapping("/application")
public class ApplyCardController {

    @Autowired
    private ApplyCardService applyCardService;

    @PutMapping("/card/{id}")
    public ResponseEntity<String> applyForCard(@PathVariable("id") Integer id,
                                               @RequestParam("address") String address,
                                               @RequestParam("state") String state,
                                               @RequestParam("country") String country,
                                               @RequestParam("gender") String gender,
                                               @RequestParam("dob")  LocalDate dob,
                                               @RequestParam("phoneNumber") Long phoneNumber,
                                               @RequestParam("pan") String pan,
                                               @RequestParam("salary") double salary,
                                               @RequestParam("file") MultipartFile file) {
        try {
            applyCardService.applyForCard(id, address, state, country, gender, dob, phoneNumber, pan,salary, file);
            return ResponseEntity.ok("Application submitted successfully with PENDING status.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

   

}
