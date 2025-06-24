package com.inzira.admin.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.inzira.admin.dtos.AgencyDTO;
import com.inzira.admin.dtos.AgencyRegistrationDTO;
import com.inzira.admin.services.AgencyRegistrationService;

@RestController
@RequestMapping("/api/admin/agencies")
public class AgencyRegistrationController {

    @Autowired
    private AgencyRegistrationService agencyRegistrationService;
    // Create new agency (uses Registration DTO)
    @PostMapping
    public ResponseEntity<?> createAgency(
        @ModelAttribute AgencyRegistrationDTO agencyRegistrationDTO,
        @RequestPart("image") MultipartFile image) {

        try {
            // Pass entity to service
            AgencyDTO createdAgency = agencyRegistrationService.createAgency(agencyRegistrationDTO, image);

            // Build full image URL if logoPath is present
            if (createdAgency.getLogoPath() != null) {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                createdAgency.setLogoPath(baseUrl + "/uploads/" + createdAgency.getLogoPath());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(createdAgency);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation Error",
                "message", e.getMessage()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Unexpected Error",
                "message", e.getMessage()
            ));
        }
    }
}
