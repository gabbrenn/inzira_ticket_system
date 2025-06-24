package com.inzira.admin.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.inzira.admin.dtos.AgencyDTO;
import com.inzira.admin.dtos.AgencyUpdateDTO;
import com.inzira.admin.services.AgencyManagementService;

@Controller
@RequestMapping("/api/admin/agencies")
public class AgencyManagementController {

    @Autowired
    private AgencyManagementService agencyManagementService;

    // Get all agencies as DTOs with full image URLs
    @GetMapping
    public ResponseEntity<?> getAllAgencies() {
        List<AgencyDTO> agencies = agencyManagementService.getAllAgencies();

        if (agencies.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "message", "No agencies found",
                "status", HttpStatus.OK.value(),
                "timestamp", LocalDateTime.now()
            ));
        }

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        agencies.forEach(dto -> {
            if (dto.getLogoPath() != null) {
                dto.setLogoPath(baseUrl + "/uploads/" + dto.getLogoPath());
            }
        });

        return ResponseEntity.ok(agencies);
    }

    // Get agency by ID as DTO with full image URL
    @GetMapping("/{id}")
    public ResponseEntity<?> getAgencyById(@PathVariable Long id) {
        try {
            AgencyDTO dto = agencyManagementService.getAgencyById(id);

            if (dto.getLogoPath() != null) {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                dto.setLogoPath(baseUrl + "/uploads/" + dto.getLogoPath());
            }

            return ResponseEntity.ok(dto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Update agency with DTO and optional logo file
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAgency(
        @PathVariable Long id,
        @RequestPart("agency") AgencyUpdateDTO updatedDto,
        @RequestPart(value = "logo", required = false) MultipartFile logoFile) {

        try {
            AgencyDTO updatedAgencyDto = agencyManagementService.updateAgency(id, updatedDto, logoFile);

            if (updatedAgencyDto.getLogoPath() != null) {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                updatedAgencyDto.setLogoPath(baseUrl + "/uploads/" + updatedAgencyDto.getLogoPath());
            }

            return ResponseEntity.ok(updatedAgencyDto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Reset password
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        try {
            String newPassword = agencyManagementService.resetPassword(id);
            return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully",
                "newPassword", newPassword // Remove if not safe to return
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // Delete agency
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAgency(@PathVariable Long id) {
        try {
            agencyManagementService.deleteAgency(id);
            return ResponseEntity.ok(Map.of(
                "message", "Agency deleted successfully",
                "status", HttpStatus.OK.value(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }
}
