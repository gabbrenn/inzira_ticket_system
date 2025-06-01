package com.inzira.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.inzira.models.Agency;
import com.inzira.services.AgencyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/api/agencies")
@CrossOrigin(origins = "http://localhost:5175")
public class AgencyController {

    @Autowired
    private AgencyService agencyService;

    //Create new Agency
    @PostMapping
    public ResponseEntity<?> createAgency(
        @ModelAttribute Agency agency,
        @RequestParam("image") MultipartFile image) {

        try {
            Agency savedAgency = agencyService.createAgency(agency, image);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedAgency);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Validation Error",
                        "message", e.getMessage()
                ));

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Unexpected Error",
                        "message", e.getMessage()
                ));
        }
    }


    // Get all agencies
    @GetMapping
    public ResponseEntity<?> getAllAgencies() {
        List<Agency> agencies = agencyService.getAllAgencies();

        if (agencies.isEmpty()) {
            return ResponseEntity.ok(Map.of(
            "message", "No agencies found",
            "status", HttpStatus.OK.value(),
            "timestamp", LocalDateTime.now()
            ));
        }

        // Build base URL for images
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        // Set full image path for each agency
        agencies.forEach(agency -> {
            if (agency.getLogoPath() != null) {
                agency.setLogoPath(baseUrl + "/uploads/" + agency.getLogoPath());
            }
        });

        return ResponseEntity.ok(agencies);
    }

    
    // Get agency by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAgencyById(@PathVariable Long id) {
        try {
            Agency agency = agencyService.getAgencyById(id);

            // Append full logo URL if exists
            if (agency.getLogoPath() != null) {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                agency.setLogoPath(baseUrl + "/uploads/" + agency.getLogoPath());
            }

            return ResponseEntity.ok(agency);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "message", e.getMessage(),
            "status", HttpStatus.NOT_FOUND.value(),
            "timestamp", LocalDateTime.now()
            ));
        }
    }


    // Update agency
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAgency(
    @PathVariable Long id,
    @RequestPart("agency") Agency updatedAgency,
    @RequestPart(value = "logo", required = false) MultipartFile logoFile) {
    
        try {
            Agency agency = agencyService.updateAgency(id, updatedAgency, logoFile);
            return ResponseEntity.ok(agency);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = Map.of(
            "message", e.getMessage(),
            "status", HttpStatus.NOT_FOUND.value(),
            "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Reset password
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        try {
            String newPassword = agencyService.resetPassword(id);
            return ResponseEntity.ok().body(Map.of(
                "message", "Password reset successfully",
                "newPassword", newPassword // Return only if safe
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // Delete agency
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAgency(@PathVariable Long id) {
        try {
            agencyService.deleteAgency(id);
            Map<String, Object> response = Map.of(
            "message", "Agency deleted successfully",
            "status", HttpStatus.OK.value(),
            "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = Map.of(
            "message", e.getMessage(),
            "status", HttpStatus.NOT_FOUND.value(),
            "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
}
