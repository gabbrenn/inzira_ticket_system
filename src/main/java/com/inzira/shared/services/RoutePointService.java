package com.inzira.shared.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inzira.shared.entities.RoutePoint;
import com.inzira.shared.repositories.RoutePointRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

/**
 * Service class for managing Location entities.
 * Handles business logic for creating, retrieving, updating, and deleting locations.
 */
@Service
public class RoutePointService {

    @Autowired
    private RoutePointRepository locationRepository;

    /**
     * Creates a new Location if it doesn't already exist in the same district.
     *
     * @param location the Location to be created
     * @return the saved Location
     * @throws IllegalArgumentException if a location with the same name already exists in the district
     */
    public RoutePoint createLocation(RoutePoint location) {
        Long districtId = location.getDistrict().getId();
        String name = location.getName();

        // Check if a location with the same name already exists in the district
        if (locationRepository.existsByNameIgnoreCaseAndDistrictId(name, districtId)) {
            throw new IllegalArgumentException("Location already exists in the same district.");
        }

        return locationRepository.save(location);
    }

    /**
     * Retrieves all locations from the database.
     *
     * @return a list of all locations
     */
    public List<RoutePoint> getAll() {
        return locationRepository.findAll();
    }

    /**
     * Retrieves a location by its ID.
     *
     * @param id the ID of the location
     * @return the found RoutePoint
     * @throws RuntimeException if no location is found with the given ID
     */
    public RoutePoint getById(Long id) {
        return locationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Location not found!"));
    }

    /**
     * Retrieves all locations that belong to a specific district.
     *
     * @param districtId the ID of the district
     * @return a list of locations in the given district
     */
    public List<RoutePoint> getByDistrict(Long districtId) {
        return locationRepository.findByDistrictId(districtId);
    }

    /**
     * Updates an existing location with new data.
     *
     * @param id the ID of the location to update
     * @param updatedLocation the new data to update the location with
     * @return the updated Location
     * @throws EntityNotFoundException if the location with the given ID does not exist
     * @throws IllegalArgumentException if a duplicate location exists in the same district
     */
    public RoutePoint updateLocation(Long id, RoutePoint updatedLocation) {
        RoutePoint existing = locationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Location not found with ID: " + id));

        Long updatedDistrictId = updatedLocation.getDistrict().getId();
        String updatedName = updatedLocation.getName();

        // Check for duplicate name in the same district excluding the current location
        boolean existsDuplicate = locationRepository.existsByNameIgnoreCaseAndDistrictIdAndIdNot(
            updatedName,
            updatedDistrictId,
            id
        );

        if (existsDuplicate) {
            throw new IllegalArgumentException("Another location with this name already exists in the same district.");
        }

        // Update the existing location with new values
        existing.setName(updatedName);
        existing.setGpsLat(updatedLocation.getGpsLat());
        existing.setGpsLong(updatedLocation.getGpsLong());
        existing.setDistrict(updatedLocation.getDistrict());

        return locationRepository.save(existing);
    }

    /**
     * Deletes a location by its ID.
     *
     * @param id the ID of the location to delete
     * @throws EntityNotFoundException if the location with the given ID does not exist
     */
    public void deleteLocation(Long id) {
        RoutePoint location = locationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Location not found with ID: " + id));
        locationRepository.delete(location);
    }
}
