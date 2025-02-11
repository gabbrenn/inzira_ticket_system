package com.inzira.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inzira.models.Agency;
import com.inzira.repositories.AgencyRepository;

@Service
public class AgencyService {

    @Autowired
    private AgencyRepository agencyRepository;

    //Create New Agency
    public Agency createAgency(Agency agency){
        if (agencyRepository.existsByEmail(agency.getEmail())) {
            throw new RuntimeException("Agency with email " + agency.getEmail() + " already exists");
        }
        if (agencyRepository.existsByAgencyName(agency.getAgencyName())) {
            throw new RuntimeException("Agency with name " + agency.getAgencyName() + " already exists");
        }
        return agencyRepository.save(agency);
    }

    //Get all Agency
    public List<Agency> getAllAgencies(){
        return agencyRepository.findAll();
    }

    // Get Agency by ID with error handling
    public Agency getAgencyById(Long id) {
        return agencyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agency not found!"));
    }

    // Update agency details
    public Agency updateAgency(Long id, Agency updatedAgency) {
        return agencyRepository.findById(id)
            .map(existingAgency -> {
                existingAgency.setAgencyName(updatedAgency.getAgencyName());
                existingAgency.setEmail(updatedAgency.getEmail());
                existingAgency.setPhoneNumber(updatedAgency.getPhoneNumber());
                existingAgency.setAddress(updatedAgency.getAddress());
                existingAgency.setPassword(updatedAgency.getPassword());
                return agencyRepository.save(existingAgency);
            })
            .orElseThrow(() -> new IllegalArgumentException("Agency not found."));
    }

    //Delete agency by ID
    public void deleteAgency(Long id){
        if (!agencyRepository.existsById(id)) {
            throw new RuntimeException("Agency not found!");
        }
        agencyRepository.deleteById(id);
    }
}
