package com.inzira.admin.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.inzira.admin.dtos.AgencyDTO;
import com.inzira.admin.dtos.AgencyUpdateDTO;
import com.inzira.admin.mappers.AgencyMapper;
import com.inzira.agency.models.Agency;
import com.inzira.agency.repositories.AgencyRepository;
import com.inzira.agency.utils.PasswordUtility;
import com.inzira.shared.services.FileStorageService;

@Service
public class AgencyManagementService {

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PasswordUtility passwordUtility;

    @Autowired
    private AgencyMapper agencyMapper;
    
    // Get all agencies as DTOs
    public List<AgencyDTO> getAllAgencies() {
        return agencyRepository.findAll()
                .stream()
                .map(agencyMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get agency by ID as DTO with error handling
    public AgencyDTO getAgencyById(Long id) {
        Agency agency = agencyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agency not found!"));
        return agencyMapper.toDTO(agency);
    }

    // Update agency by ID using DTO + optional MultipartFile
    public AgencyDTO updateAgency(Long id, AgencyUpdateDTO dto, MultipartFile logoFile) {
        return agencyRepository.findById(id)
            .map(existingAgency -> {
                // Use mapper to update entity from DTO
                agencyMapper.updateEntityFromDTO(dto, existingAgency);

                // Handle new logo file upload
                if (logoFile != null && !logoFile.isEmpty()) {
                    try {
                        // Delete old logo file
                        String oldLogoPath = existingAgency.getLogoPath();
                        if (oldLogoPath != null && !oldLogoPath.isBlank()) {
                            Path uploadDir = Paths.get("uploads");
                            Path fullPath = uploadDir.resolve(oldLogoPath);
                            File oldFile = fullPath.toFile();
                            if (oldFile.exists()) oldFile.delete();
                        }

                        // Store new logo file
                        String newFilePath = fileStorageService.storeFile(logoFile, "user-profile");
                        existingAgency.setLogoPath(newFilePath);

                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store logo file during update: " + e.getMessage(), e);
                    }
                }

                Agency savedAgency = agencyRepository.save(existingAgency);
                return agencyMapper.toDTO(savedAgency);
            })
            .orElseThrow(() -> new IllegalArgumentException("Agency not found."));
    }

    // Reset agency password and return new raw password (handle email sending outside)
    public String resetPassword(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new RuntimeException("Agency not found"));

        String newPassword = passwordUtility.generateInitialPassword(agency.getAgencyName(), agency.getPhoneNumber());
        agency.setPassword(passwordUtility.encodePassword(newPassword));

        agencyRepository.save(agency);
        return newPassword;
    }

    // Delete agency and delete logo file
    public void deleteAgency(Long id) {
        Agency agency = agencyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agency not found!"));

        String logoPath = agency.getLogoPath();
        if (logoPath != null && !logoPath.isBlank()) {
            Path uploadDir = Paths.get("uploads");
            Path fullPath = uploadDir.resolve(logoPath);
            File logoFile = fullPath.toFile();

            if (logoFile.exists() && !logoFile.delete()) {
                System.err.println("⚠️ Failed to delete logo file: " + logoFile.getAbsolutePath());
            }
        }

        agencyRepository.deleteById(id);
    }
}
