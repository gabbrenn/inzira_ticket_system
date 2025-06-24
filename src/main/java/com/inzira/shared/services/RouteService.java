package com.inzira.shared.services;

import com.inzira.shared.models.District;
import com.inzira.shared.models.Route;
import com.inzira.shared.repositories.DistrictRepository;
import com.inzira.shared.repositories.RouteRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private DistrictRepository districtRepository;

    public Route createRoute(Route route) {
        if (route.getOrigin() == null || route.getDestination() == null) {
            throw new IllegalArgumentException("Origin and destination must be provided.");
        }

        Long originId = route.getOrigin().getId();
        Long destinationId = route.getDestination().getId();

        if (originId.equals(destinationId)) {
            throw new IllegalArgumentException("Origin and destination districts cannot be the same.");
        }

        if (routeRepository.existsByOriginIdAndDestinationId(originId, destinationId)) {
            throw new IllegalArgumentException("Route already exists.");
        }

        District origin = districtRepository.findById(originId)
            .orElseThrow(() -> new EntityNotFoundException("Origin district not found."));
        District destination = districtRepository.findById(destinationId)
            .orElseThrow(() -> new EntityNotFoundException("Destination district not found."));

        route.setOrigin(origin);
        route.setDestination(destination);

        return routeRepository.save(route);
    }


    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
    }

    public Route updateRoute(Long id, Route updatedRoute) {
        return routeRepository.findById(id).map(route -> {
            boolean sameIds = route.getOrigin().getId().equals(updatedRoute.getOrigin().getId()) &&
                              route.getDestination().getId().equals(updatedRoute.getDestination().getId());

            if (!sameIds) {
                boolean exists = routeRepository.findByOriginIdAndDestinationId(
                        updatedRoute.getOrigin().getId(), updatedRoute.getDestination().getId()
                ).stream().findFirst().isPresent();

                if (exists) {
                    throw new IllegalArgumentException("Another route with same origin and destination already exists.");
                }
            }

            route.setOrigin(updatedRoute.getOrigin());
            route.setDestination(updatedRoute.getDestination());

            return routeRepository.save(route);
        }).orElseThrow(() -> new RuntimeException("Route not found!"));
    }

    public void deleteRoute(Long id) {
        routeRepository.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
        routeRepository.deleteById(id);
    }
}

