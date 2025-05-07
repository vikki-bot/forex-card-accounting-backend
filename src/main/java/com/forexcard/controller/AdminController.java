package com.forexcard.controller;

import com.forexcard.dto.PendingUserDTO;
import com.forexcard.model.User;
import com.forexcard.repo.UserRepository;
import com.forexcard.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepo;

    @PutMapping("/approve/{userId}")
    public ResponseEntity<String> approveUser(@PathVariable("userId") Integer userId) {
        String result = adminService.approveUser(userId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/reject/{userId}")
    public ResponseEntity<String> denyUser(@PathVariable("userId") Integer userId) {
        String result = adminService.denyUser(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PendingUserDTO>> getPendingUsers() {
        List<PendingUserDTO> pendingUserDTOs = adminService.getPendingUsers();
        return ResponseEntity.ok(pendingUserDTOs);
    }

    @GetMapping("/document/{userId}")
    public ResponseEntity<byte[]> viewUploadedDocument(@PathVariable("userId") Integer userId) {
        try {
            Optional<User> optionalUser = userRepo.findById(userId);

            if (optionalUser.isEmpty() || optionalUser.get().getFilePath() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String filePath = optionalUser.get().getFilePath(); // e.g., /uploads/salary-slips/xyz.pdf
            Path path = Paths.get("." + filePath);

            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] fileContent = adminService.getUploadedDocument(userId);

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.inline().filename(path.getFileName().toString()).build());

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
