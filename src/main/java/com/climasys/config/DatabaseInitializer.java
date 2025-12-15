package com.climasys.config;

import com.climasys.auth.entity.*;
import com.climasys.entity.User;
import com.climasys.entity.Clinic;
import com.climasys.entity.StatusRef;
import com.climasys.entity.DoctorClinicShift;
import com.climasys.entity.DoctorClinicShiftId;
import com.climasys.auth.repository.*;
import com.climasys.repository.StatusRefRepository;
import com.climasys.repository.DoctorClinicShiftRepository;
import com.climasys.common.crypto.LegacyCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private UserMasterRepository userMasterRepository;
    
    @Autowired
    private RoleMasterRepository roleMasterRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private AuthDoctorMasterRepository doctorMasterRepository;
    
    @Autowired
    private StatusRefRepository statusRefRepository;
    
    @Autowired
    private DoctorClinicShiftRepository doctorClinicShiftRepository;
    
    @Value("${climasys.encryption.key}")
    private String encryptionKey;
    
    
    @Autowired
    private ClinicMasterRepository clinicMasterRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Create test data if it doesn't exist
            createTestData();
            System.out.println("✅ Test data created successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating test data: " + e.getMessage());
            // Don't fail the application startup if data creation fails
        }
    }
    
    private void createTestData() {
        // Test data creation removed to avoid hardcoded values
        // The application should work with data created through proper APIs
        // or database migrations rather than hardcoded test data
        logger.info("Skipping test data creation to avoid hardcoded values");
    }
}
