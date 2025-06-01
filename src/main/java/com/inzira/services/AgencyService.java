package com.inzira.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.inzira.models.Agency;
import com.inzira.repositories.AgencyRepository;
import com.inzira.utils.PasswordUtility;

@Service
public class AgencyService {

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PasswordUtility passwordUtility;

    //Create New Agency
    public Agency createAgency(Agency agency, MultipartFile file) {
        try {
            // Step 1: Validate email uniqueness
            if (agencyRepository.existsByEmail(agency.getEmail())) {
                throw new IllegalArgumentException("Agency with email " + agency.getEmail() + " already exists");
            }

            // Step 2: Validate name uniqueness
            if (agencyRepository.existsByAgencyName(agency.getAgencyName())) {
                throw new IllegalArgumentException("Agency with name " + agency.getAgencyName() + " already exists");
            }

            // Step 3: Validate the file
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Uploaded file is empty or missing");
            }

            // Step 4: Generate initial password
            String rawPassword = passwordUtility.generateInitialPassword(agency.getAgencyName(), agency.getPhoneNumber());
             // Encode password before saving
            agency.setPassword(passwordUtility.encodePassword(rawPassword));

            // Step 5: Store the file
            String filePath = fileStorageService.storeFile(file, "user-profile");
            agency.setLogoPath(filePath);

            // Step 6: Save agency
            return agencyRepository.save(agency);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store logo file: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while saving agency: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // Let validation-related errors bubble up clearly
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while creating agency: " + e.getMessage(), e);
        }
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

    public Agency updateAgency(Long id, Agency updatedAgency, MultipartFile logoFile) {
    return agencyRepository.findById(id)
        .map(existingAgency -> {
            existingAgency.setAgencyName(updatedAgency.getAgencyName());
            existingAgency.setEmail(updatedAgency.getEmail());
            existingAgency.setPhoneNumber(updatedAgency.getPhoneNumber());
            existingAgency.setStatus(updatedAgency.getStatus());
            existingAgency.setAddress(updatedAgency.getAddress());

            // If a new logo file is uploaded, delete the old one first
            if (logoFile != null && !logoFile.isEmpty()) {
                try {
                    // ✅ Delete old logo file if it exists
                    String oldLogoRelativePath = existingAgency.getLogoPath(); // e.g., "user-profile/logo123.png"
                    if (oldLogoRelativePath != null && !oldLogoRelativePath.isBlank()) {
                        // Construct the actual file path (e.g., using fileStorageService's upload directory)
                        Path uploadDir = Paths.get("uploads"); // or wherever your uploads are stored
                        Path fullPath = uploadDir.resolve(oldLogoRelativePath);

                        File oldFile = fullPath.toFile();
                        if (oldFile.exists()) {
                            oldFile.delete(); // 🧹 delete the file
                        }
                    }   

                    // ✅ Store new logo
                    String newFilePath = fileStorageService.storeFile(logoFile, "user-profile");
                    existingAgency.setLogoPath(newFilePath);

                } catch (IOException e) {
                    throw new RuntimeException("Failed to store logo file during update: " + e.getMessage(), e);
                }
            }

            return agencyRepository.save(existingAgency);
        })
        .orElseThrow(() -> new IllegalArgumentException("Agency not found."));
    }


     public String resetPassword(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new RuntimeException("Agency not found"));

        String newPassword = passwordUtility.generateInitialPassword(agency.getAgencyName(), agency.getPhoneNumber()); // or generateRandomPassword()
        agency.setPassword(passwordUtility.encodePassword(newPassword));

        agencyRepository.save(agency);
        // Optionally: Send email with the new password here
        return newPassword; // Or hide it if not safe to return
    }


    //Delete agency by ID
    public void deleteAgency(Long id) {
        Agency agency = agencyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agency not found!"));

        // ✅ Delete logo file if it exists
        String logoPath = agency.getLogoPath(); // e.g., "user-profile/logo123.png"
        if (logoPath != null && !logoPath.isBlank()) {
            Path uploadDir = Paths.get("uploads"); // Adjust if your root upload dir is different
            Path fullPath = uploadDir.resolve(logoPath);
            File logoFile = fullPath.toFile();

            if (logoFile.exists()) {
                boolean deleted = logoFile.delete();
                if (!deleted) {
                    System.err.println("⚠️ Failed to delete logo file: " + logoFile.getAbsolutePath());
                }
            }
        }

        // ✅ Now delete the agency record
        agencyRepository.deleteById(id);
    }

}
