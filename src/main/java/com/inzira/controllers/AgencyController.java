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

import com.inzira.models.Agency;
import com.inzira.services.AgencyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/api/agencies")
// @CrossOrigin(origins = "http://localhost:5175")
public class AgencyController {

    @Autowired
    private AgencyService agencyService;

    //Create new Agency
    @PostMapping
    public ResponseEntity<?> createAgency(@RequestBody Agency agency){
        try {
            Agency savedAgency = agencyService.createAgency(agency);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAgency);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", e.getMessage()));
        }
    }

    //Get all agencies
    @GetMapping
    public ResponseEntity<?> getAllAgencies(){
        List<Agency> agencies = agencyService.getAllAgencies();
        if (agencies.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No agencies found"));
        }
        return ResponseEntity.ok(agencyService.getAllAgencies());
    }
    
    //Get agency by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getAgencyById(@PathVariable Long id) {
        try {
            Agency agency = agencyService.getAgencyById(id);
            return ResponseEntity.ok(agency);
        } catch (RuntimeException e) {
            // Return JSON response with error message in a Map
            Map<String, Object> errorResponse = Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Update agency
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAgency(@PathVariable Long id, @RequestBody Agency updatedAgency) {
        try {
            Agency agency = agencyService.updateAgency(id, updatedAgency);
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
