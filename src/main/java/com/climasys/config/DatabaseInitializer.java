package com.climasys.config;

import com.climasys.auth.entity.*;
import com.climasys.entity.User;
import com.climasys.entity.Clinic;
import com.climasys.entity.ClinicId;
import com.climasys.auth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserMasterRepository userMasterRepository;
    
    @Autowired
    private RoleMasterRepository roleMasterRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private DoctorMasterRepository doctorMasterRepository;
    
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
        // Create roles if they don't exist
        if (roleMasterRepository.count() == 0) {
            RoleMaster doctorRole = new RoleMaster(1, "Doctor");
            doctorRole.setCreatedbyName("System");
            RoleMaster nurseRole = new RoleMaster(2, "Nurse");
            nurseRole.setCreatedbyName("System");
            roleMasterRepository.save(doctorRole);
            roleMasterRepository.save(nurseRole);
        }
        
        // Create doctor if doesn't exist
        if (!doctorMasterRepository.existsById("DOC001")) {
            DoctorMaster doctor = new DoctorMaster("DOC001", "Dr. Test", "User");
            doctor.setSpeciality("General Medicine");
            doctor.setDoctorQual("MBBS, MD");
            doctorMasterRepository.save(doctor);
        }
        
        // Create clinic if doesn't exist
        if (!clinicMasterRepository.existsById(new ClinicId("DOC001", "CLINIC001"))) {
            Clinic clinic = new Clinic();
            clinic.setClinicId("CLINIC001");
            clinic.setClinicName("Test Clinic");
            clinic.setDoctorId("DOC001");
            clinic.setClinicAddress("123 Test Street, Test City");
            clinic.setPhoneNo("+1-234-567-8900");
            clinic.setIsPrint(true);
            clinic.setCreatedOn(LocalDateTime.now());
            clinic.setCreatedbyName("System");
            clinicMasterRepository.save(clinic);
        }
        
        // Create test user if doesn't exist
        if (!userMasterRepository.findByLoginIdAndIsActive("test_user", true).isPresent()) {
            User user = new User();
            user.setLoginId("test_user");
            user.setPassword("test_password");
            user.setFirstName("Test");
            user.setDoctorId("DOC001");
            user.setLanguageId(1);
            user.setIsActive(true);
            user.setCreatedOn(LocalDateTime.now());
            user.setCreatedbyName("System");
            userMasterRepository.save(user);
            
            // Create user role
            UserRole userRole = new UserRole(1, user.getId(), "CLINIC001", "DOC001");
            userRole.setIsDefaultClinic(true);
            userRole.setCreatedOn(LocalDateTime.now());
            userRole.setCreatedbyName("System");
            userRoleRepository.save(userRole);
        }
    }
}
