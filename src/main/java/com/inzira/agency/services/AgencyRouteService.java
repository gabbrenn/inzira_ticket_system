package com.inzira.agency.services;

import com.inzira.agency.models.Agency;
import com.inzira.agency.models.AgencyRoute;
import com.inzira.agency.repositories.AgencyRepository;
import com.inzira.agency.repositories.AgencyRouteRepository;
import com.inzira.shared.models.Route;
import com.inzira.shared.models.RoutePoint;
import com.inzira.shared.repositories.RoutePointRepository;
import com.inzira.shared.repositories.RouteRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgencyRouteService {

    @Autowired
    private AgencyRouteRepository agencyRouteRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    public AgencyRoute createAgencyRoute(Long agencyId, Long routeId, double price, List<Long> pickupPointIds, List<Long> dropPointIds) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new EntityNotFoundException("Agency not found"));

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("Route not found"));

        List<RoutePoint> pickupPoints = routePointRepository.findAllById(pickupPointIds);
        List<RoutePoint> dropPoints = routePointRepository.findAllById(dropPointIds);

        // Optional: Validate pickupPoints belong to route.originDistrict & dropPoints belong to route.destinationDistrict

        AgencyRoute agencyRoute = new AgencyRoute();
        agencyRoute.setAgency(agency);
        agencyRoute.setRoute(route);
        agencyRoute.setPrice(price);
        agencyRoute.setPickupPoints(pickupPoints);
        agencyRoute.setDropPoints(dropPoints);

        return agencyRouteRepository.save(agencyRoute);
    }

    public List<AgencyRoute> getAllAgencyRoutes() {
        return agencyRouteRepository.findAll();
    }

    public AgencyRoute getById(Long id) {
        return agencyRouteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AgencyRoute not found"));
    }

    public List<AgencyRoute> getRoutesByAgencyId(Long agencyId) {
        return agencyRouteRepository.findByAgencyId(agencyId);
    }


    public void delete(Long id) {
        agencyRouteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AgencyRoute not found"));
        agencyRouteRepository.deleteById(id);
    }
}
