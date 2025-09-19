package com.climasys.auth.service;

import com.climasys.auth.dto.LoginResponse;
import com.climasys.auth.dto.LoginResponse.UserDetails;
import com.climasys.auth.dto.LoginResponse.ShiftTime;
import com.climasys.auth.dto.LoginResponse.AvailableRole;
import com.climasys.auth.dto.LoginResponse.SystemParam;
import com.climasys.auth.entity.*;
import com.climasys.entity.User;
import com.climasys.entity.Clinic;
import com.climasys.auth.repository.*;
import com.climasys.common.crypto.LegacyCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.climasys.audit");

    @Autowired
    private UserMasterRepository userMasterRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private DoctorMasterRepository doctorMasterRepository;
    
    @Autowired
    private ClinicMasterRepository clinicMasterRepository;
    
    @Value("${climasys.encryption.key:PA1ANDE61INI6}")
    private String encryptionKey;

    public LoginResponse authenticateUser(String loginId, String password, String todaysDay, Integer languageId) {
        logger.info("Starting authentication for user: {}", loginId);
        auditLogger.info("LOGIN_ATTEMPT - User: {}", loginId);
        
        try {
            // Set default language ID if not provided
            if (languageId == null) {
                languageId = 1; // Default to English
                logger.debug("Language ID not provided, using default: {}", languageId);
            }

            // Find user by login ID only (password is encrypted in database)
            Optional<User> userOpt = userMasterRepository.findByLoginIdAndIsActive(loginId, true);
            
            LoginResponse response = new LoginResponse();
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Decrypt the stored password and compare with input password
                String decryptedPassword;
                try {
                    decryptedPassword = LegacyCrypto.decryptUnicode(encryptionKey, user.getPassword());
                    logger.debug("Password decryption successful for user: {}", loginId);
                } catch (Exception e) {
                    logger.error("Password decryption failed for user: {} - {}", loginId, e.getMessage());
                    auditLogger.error("LOGIN_FAILED - Password decryption error for user: {}", loginId);
                    response.setLoginStatus(0);
                    response.setErrorMessage("Authentication error");
                    return response;
                }
                
                // Compare decrypted password with input password
                if (!decryptedPassword.equals(password)) {
                    logger.warn("Authentication failed - password mismatch for user: {}", loginId);
                    auditLogger.warn("LOGIN_FAILED - Invalid password for user: {}", loginId);
                    response.setLoginStatus(0);
                    response.setErrorMessage("Invalid login credentials");
                    return response;
                }
                
                logger.info("User found successfully: {}", user.getLoginId());
                
                // Login successful
                response.setLoginStatus(1);
                response.setErrorMessage(null);
                
                // Get user roles
                List<UserRole> userRoles = userRoleRepository.findDefaultRolesByLoginId(loginId);
                logger.debug("User roles found: {} roles", userRoles.size());
                
                if (!userRoles.isEmpty()) {
                    UserRole defaultRole = userRoles.get(0);
                    
                    // Get doctor details using findByDoctorId
                    Optional<DoctorMaster> doctorOpt = doctorMasterRepository.findByDoctorId(user.getDoctorId());
                    logger.debug("Doctor lookup result: {}", doctorOpt.isPresent());
                    
                    // Get clinic details
                    List<Clinic> clinics = clinicMasterRepository.findDefaultClinicsByLoginId(loginId);
                    logger.debug("Clinics found: {} clinics", clinics.size());
                    
                    if (doctorOpt.isPresent() && !clinics.isEmpty()) {
                        DoctorMaster doctor = doctorOpt.get();
                        Clinic clinic = clinics.get(0);
                        
                        // Build user details
                        UserDetails userDetails = new UserDetails();
                        userDetails.setId(user.getId());
                        userDetails.setDoctorId(user.getDoctorId());
                        userDetails.setClinicId(clinic.getClinicId());
                        userDetails.setLoginId(user.getLoginId());
                        userDetails.setFirstName(user.getFirstName());
                        userDetails.setPassword(user.getPassword());
                        userDetails.setRoleName(defaultRole.getRoleMaster().getRoleName());
                        userDetails.setRoleId(defaultRole.getRoleId());
                        userDetails.setDoctorName(buildDoctorName(doctor));
                        userDetails.setClinicName(clinic.getClinicName());
                        userDetails.setLanguageId(user.getLanguageId());
                        userDetails.setIsActive(user.getIsActive());
                        userDetails.setFinancialYear(getFinancialYear());
                        userDetails.setModelId(1); // Default model ID
                        userDetails.setConfigId(1); // Default config ID
                        userDetails.setIsEnabled(true); // Default enabled
                        
                        response.setUserDetails(userDetails);
                        logger.info("User details built successfully for: {}", user.getLoginId());
                    } else {
                        logger.warn("Missing doctor or clinic data for user: {}", user.getLoginId());
                    }
                } else {
                    logger.warn("No user roles found for: {}", user.getLoginId());
                }
                
                // Build shift times (mock data for now)
                List<ShiftTime> shiftTimes = buildShiftTimes(todaysDay);
                response.setShiftTimes(shiftTimes);
                
                // Build available roles (mock data for now)
                List<AvailableRole> availableRoles = buildAvailableRoles();
                response.setAvailableRoles(availableRoles);
                
                // Build system params (mock data for now)
                List<SystemParam> systemParams = buildSystemParams(user.getDoctorId());
                response.setSystemParams(systemParams);
                
                // Set license key (mock data for now)
                response.setLicenseKey("TEST_LICENSE_KEY_12345");
                
                auditLogger.info("LOGIN_SUCCESS - User: {}, Doctor: {}", 
                    user.getLoginId(), user.getDoctorId());
                
            } else {
                // Login failed - invalid credentials
                logger.warn("Authentication failed - invalid credentials for user: {}", loginId);
                auditLogger.warn("LOGIN_FAILED - Invalid credentials for user: {}", loginId);
                response.setLoginStatus(0);
                response.setErrorMessage("Invalid login credentials");
            }
            
            return response;
        } catch (Exception e) {
            // Handle any exceptions during the authentication process
            logger.error("Authentication error for user: {} - {}", loginId, e.getMessage(), e);
            auditLogger.error("LOGIN_ERROR - User: {}, Error: {}", loginId, e.getMessage());
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setLoginStatus(-1);
            errorResponse.setErrorMessage("Authentication error: " + e.getMessage());
            return errorResponse;
        }
    }
    
    private String buildDoctorName(DoctorMaster doctor) {
        StringBuilder name = new StringBuilder();
        if (doctor.getFirstName() != null) name.append(doctor.getFirstName());
        if (doctor.getMiddleName() != null) name.append(" ").append(doctor.getMiddleName());
        if (doctor.getLastName() != null) name.append(" ").append(doctor.getLastName());
        return name.toString().trim();
    }
    
    private Integer getFinancialYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        if (now.getMonthValue() >= 4) {
            return year + 1;
        }
        return year;
    }
    
    private List<ShiftTime> buildShiftTimes(String todaysDay) {
        List<ShiftTime> shiftTimes = new ArrayList<>();
        ShiftTime shiftTime = new ShiftTime();
        shiftTime.setShiftId(1);
        shiftTime.setDescription("Morning Shift - " + todaysDay + " - 09 - 17");
        shiftTimes.add(shiftTime);
        return shiftTimes;
    }
    
    private List<AvailableRole> buildAvailableRoles() {
        List<AvailableRole> availableRoles = new ArrayList<>();
        AvailableRole role = new AvailableRole();
        role.setRoleId(2);
        role.setRoleName("Nurse");
        availableRoles.add(role);
        return availableRoles;
    }
    
    private List<SystemParam> buildSystemParams(String doctorId) {
        List<SystemParam> systemParams = new ArrayList<>();
        SystemParam param = new SystemParam();
        param.setParamName("test_param");
        param.setParamValue("test_value");
        param.setDoctorId(doctorId);
        systemParams.add(param);
        return systemParams;
    }
}
