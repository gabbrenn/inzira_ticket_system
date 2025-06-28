package com.inzira.admin.controllers;

import com.inzira.admin.services.RouteManagementService;
import com.inzira.shared.entities.Route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/routes")
public class RouteManagementController {

    @Autowired
    private RouteManagementService routeService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Route route) {
        try {
            return ResponseEntity.ok(routeService.createRoute(route));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
            "message", e.getMessage(),
            "timestamp", LocalDateTime.now()
            ));
        }
    }


    @GetMapping
    public List<Route> getAll() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(routeService.getRouteById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Update Route
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Route route) {
        try {
            Route updated = routeService.updateRoute(id, route);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", e.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value(),
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            routeService.deleteRoute(id);
            return ResponseEntity.ok(Map.of("message", "Route deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }
}