package com.inzira.admin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inzira.admin.services.DistrictService;
import com.inzira.shared.entities.District;
import com.inzira.shared.entities.RoutePoint;
import com.inzira.shared.services.RoutePointService;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/districts")
public class DistrictManagementController {

    @Autowired
    private DistrictService districtService;

    @Autowired
    private RoutePointService locationService;

    // Create District
    @PostMapping
    public ResponseEntity<?> create(@RequestBody District district) {
        try {
            return ResponseEntity.ok(districtService.createDistrict(district));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get All Districts
    @GetMapping
    public ResponseEntity<?> getAllDistricts() {
        List<District> districts = districtService.getAll();
        if (districts.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "message", "No District found",
                "status", HttpStatus.OK.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
        return ResponseEntity.ok(districts);
    }

    // Get District by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            District district = districtService.getById(id);
            return ResponseEntity.ok(district);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Update District
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDistrict(@PathVariable Long id, @RequestBody District district) {
        try {
            District updated = districtService.updateDistrict(id, district);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Delete District
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDistrict(@PathVariable Long id) {
        try {
            districtService.deleteDistrict(id);
            return ResponseEntity.ok(Map.of(
                "message", "District deleted successfully",
                "status", HttpStatus.OK.value(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Add RoutePoint to District
    @PostMapping("/{districtId}/points")
    public ResponseEntity<?> addLocation(@PathVariable Long districtId, @RequestBody RoutePoint location) {
        try {
            District district = districtService.getById(districtId);
            location.setDistrict(district);
            RoutePoint created = locationService.createLocation(location);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", "District not found",
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Update RoutePoints inside District
    @PutMapping("/{districtId}/points/{pointId}")
    public ResponseEntity<?> updateLocation(
        @PathVariable Long districtId,
        @PathVariable Long locationId,
        @RequestBody RoutePoint updatedLocation
    ) {
        try {
            District district = districtService.getById(districtId);
            RoutePoint location = locationService.getById(locationId);

            if (!location.getDistrict().getId().equals(district.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Point does not belong to the specified district",
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "timestamp", LocalDateTime.now()
                ));
            }

            updatedLocation.setDistrict(district);
            RoutePoint updated = locationService.updateLocation(locationId, updatedLocation);
            return ResponseEntity.ok(updated);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Delete Location inside District
    @DeleteMapping("/{districtId}/points/{pointId}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long districtId, @PathVariable Long locationId) {
        try {
            RoutePoint location = locationService.getById(locationId);
            if (!location.getDistrict().getId().equals(districtId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Point does not belong to the specified district",
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "timestamp", LocalDateTime.now()
                ));
            }
            locationService.deleteLocation(locationId);
            return ResponseEntity.ok(Map.of(
                "message", "RoutPoint deleted successfully",
                "status", HttpStatus.OK.value(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // List Locations by District
    @GetMapping("/{districtId}/points")
    public ResponseEntity<?> getByDistrict(@PathVariable Long districtId) {
        try {
            districtService.getById(districtId); // validate existence
            List<RoutePoint> locations = locationService.getByDistrict(districtId);
            if (locations.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "No RoutePoint found in this district",
                    "status", HttpStatus.OK.value(),
                    "timestamp", LocalDateTime.now()
                ));
            }
            return ResponseEntity.ok(locations);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", "District not found",
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }
}
