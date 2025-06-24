package com.inzira.agency.controllers;

import com.inzira.agency.dtos.AgencyRouteDTO;
import com.inzira.agency.mappers.AgencyRouteMapper;
import com.inzira.agency.models.AgencyRoute;
import com.inzira.agency.services.AgencyRouteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agency-routes")
public class AgencyRouteController {

    @Autowired
    private AgencyRouteService agencyRouteService;


    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> request) {
        try {
            Long agencyId = Long.valueOf(request.get("agencyId").toString());
            Long routeId = Long.valueOf(request.get("routeId").toString());
            double price = Double.parseDouble(request.get("price").toString());

            @SuppressWarnings("unchecked")
            List<Integer> pickupPointInts = (List<Integer>) request.get("pickupPointIds");
            List<Long> pickupPointIds = pickupPointInts.stream().map(Long::valueOf).toList();

            @SuppressWarnings("unchecked")
            List<Integer> dropPointInts = (List<Integer>) request.get("dropPointIds");
            List<Long> dropPointIds = dropPointInts.stream().map(Long::valueOf).toList();

            AgencyRoute agencyRoute = agencyRouteService.createAgencyRoute(agencyId, routeId, price, pickupPointIds, dropPointIds);
            return ResponseEntity.ok(agencyRoute);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<AgencyRouteDTO>> getAll() {
        List<AgencyRoute> agencyRoutes = agencyRouteService.getAllAgencyRoutes();
        List<AgencyRouteDTO> dtoList = agencyRoutes.stream()
            .map(AgencyRouteMapper::toDTO)
            .toList();

        return ResponseEntity.ok(dtoList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            AgencyRoute agencyRoute = agencyRouteService.getById(id);
            AgencyRouteDTO dto = AgencyRouteMapper.toDTO(agencyRoute);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/agency/{agencyId}")
    public List<AgencyRouteDTO> getRoutesByAgency(@PathVariable Long agencyId) {
        List<AgencyRoute> routes = agencyRouteService.getRoutesByAgencyId(agencyId);
        return routes.stream().map(AgencyRouteMapper::toDTO).toList();
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            agencyRouteService.delete(id);
            return ResponseEntity.ok(Map.of("message", "AgencyRoute deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }
}
