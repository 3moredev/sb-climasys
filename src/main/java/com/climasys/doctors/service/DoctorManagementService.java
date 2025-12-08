package com.climasys.doctors.service;

import com.climasys.auth.entity.AuthDoctorMaster;
import com.climasys.auth.repository.AuthDoctorMasterRepository;
import com.climasys.auth.service.HttpSessionService;
import com.climasys.auth.repository.UserMasterRepository;
import com.climasys.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for doctor management and operations
 * Uses JPA implementation with logic from stored procedures
 */
@Service
public class DoctorManagementService {

    @Autowired
    private AuthDoctorMasterRepository doctorMasterRepository;
    
    @Autowired
    private UserMasterRepository userMasterRepository;
    
    @Autowired
    private HttpSessionService httpSessionService;

    @Autowired
    private com.climasys.repository.DoctorClinicShiftRepository doctorClinicShiftRepository;

    @Autowired
    private com.climasys.repository.ClinicRepository clinicRepository;

    /**
     * Get all available doctors in the system
     * Based on stored procedure: USP_Get_AllDoctors()
     * 
     * @param languageId Optional language ID filter (if null, returns all active OPD doctors)
     * @param clinicId Optional clinic ID filter (if null, returns all active OPD doctors)
     * @param defaultDoctorId Optional doctor ID to sort first (typically the logged-in user's doctor)
     * @return List of doctors matching the criteria, with default doctor first if specified
     */
    public List<Map<String, Object>> getAllDoctors(Integer languageId, String clinicId, String defaultDoctorId) {
        try {
            List<Map<String, Object>> doctors;
            
            // If both languageId and clinicId are provided, use filtered query (matches Table[1] from stored procedure)
            if (languageId != null && clinicId != null && !clinicId.isEmpty()) {
                List<Object[]> results = doctorMasterRepository.findAllOpdDoctorsByLanguageAndClinic(languageId, clinicId);
                doctors = results != null ? results.stream()
                        .map(this::convertQueryResultToMap)
                        .collect(Collectors.toList()) : List.of();
            } else {
                // Fallback: get all active OPD doctors (simplified version)
                List<Object[]> results = doctorMasterRepository.findAllActiveOpdDoctors();
                doctors = results != null ? results.stream()
                        .map(this::convertQueryResultToMap)
                        .collect(Collectors.toList()) : List.of();
            }
            
            // Always include the default doctor from user_master, even if it doesn't meet filtering criteria
            if (defaultDoctorId != null && !defaultDoctorId.isEmpty()) {
                // Check if default doctor is already in the list
                boolean defaultDoctorExists = doctors.stream()
                        .anyMatch(d -> {
                            String id = String.valueOf(d.get("id") != null ? d.get("id") : d.get("doctor_id"));
                            return defaultDoctorId.equals(id);
                        });
                
                // If default doctor is not in the list, fetch it separately and add it
                if (!defaultDoctorExists) {
                    System.out.println("Default doctor " + defaultDoctorId + " not found in filtered list, fetching separately...");
                    Optional<AuthDoctorMaster> defaultDoctorOpt = doctorMasterRepository.findByDoctorId(defaultDoctorId);
                    if (defaultDoctorOpt.isPresent()) {
                        AuthDoctorMaster defaultDoctor = defaultDoctorOpt.get();
                        // Only add if it's an OPD doctor
                        if (defaultDoctor.getOpdDr() != null && defaultDoctor.getOpdDr()) {
                            Map<String, Object> defaultDoctorMap = convertDoctorToMap(defaultDoctor);
                            doctors.add(0, defaultDoctorMap); // Add at the beginning
                            System.out.println("Added default doctor " + defaultDoctorId + " to the list");
                        } else {
                            System.out.println("Default doctor " + defaultDoctorId + " is not an OPD doctor, skipping");
                        }
                    } else {
                        System.out.println("Default doctor " + defaultDoctorId + " not found in doctor_master table");
                    }
                } else {
                    // Default doctor is already in the list, just sort to put it first
                    doctors = doctors.stream()
                            .sorted((d1, d2) -> {
                                String id1 = String.valueOf(d1.get("id") != null ? d1.get("id") : d1.get("doctor_id"));
                                String id2 = String.valueOf(d2.get("id") != null ? d2.get("id") : d2.get("doctor_id"));
                                boolean isDefault1 = defaultDoctorId.equals(id1);
                                boolean isDefault2 = defaultDoctorId.equals(id2);
                                if (isDefault1 && !isDefault2) return -1;  // Default doctor comes first
                                if (!isDefault1 && isDefault2) return 1;   // Non-default comes after
                                return 0;  // Keep original order for others
                            })
                            .collect(Collectors.toList());
                    System.out.println("Default doctor " + defaultDoctorId + " already in list, sorted to first position");
                }
                
                // Log for debugging
                System.out.println("Final doctors list size: " + doctors.size());
                if (!doctors.isEmpty()) {
                    String firstDoctorId = String.valueOf(doctors.get(0).get("id") != null ? doctors.get(0).get("id") : doctors.get(0).get("doctor_id"));
                    System.out.println("First doctor in list: " + firstDoctorId + " (matches default: " + defaultDoctorId.equals(firstDoctorId) + ")");
                }
            }
            
            return doctors;
        } catch (Exception e) {
            // Log error but return empty list instead of throwing to prevent frontend from hanging
            System.err.println("Error fetching doctors: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Get default doctor from user_master table based on the logged-in user's session
     * 
     * @param session HTTP session containing user information
     * @return Default doctor ID from user_master.default_doctor column, or null if not found
     */
    public String getDefaultDoctorFromUser(HttpSession session) {
        try {
            if (session == null) {
                System.out.println("Session is null, cannot get default doctor");
                return null;
            }
            
            // Get loginId from session
            String loginId = httpSessionService.getLoginId(session);
            if (loginId == null || loginId.isEmpty()) {
                System.out.println("LoginId not found in session, cannot get default doctor");
                return null;
            }
            
            System.out.println("Getting default doctor for loginId: " + loginId);
            
            // Find user by loginId
            Optional<User> userOpt = userMasterRepository.findByLoginIdAndIsActive(loginId, true);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String defaultDoctor = user.getDefaultDoctor();
                System.out.println("Found user. default_doctor value: " + defaultDoctor);
                return (defaultDoctor != null && !defaultDoctor.isEmpty()) ? defaultDoctor : null;
            } else {
                System.out.println("User not found for loginId: " + loginId);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error getting default doctor from user_master: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all available doctors in the system (backward compatibility - no filters)
     * Based on stored procedure: usp_get_all_doctors()
     */
    public List<Map<String, Object>> getAllDoctors() {
        try {
            List<AuthDoctorMaster> doctors = doctorMasterRepository.findAll();

            return doctors.stream()
                    .map(this::convertDoctorToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all doctors: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors available for adhoc appointments
     * Based on stored procedure logic: doctors who are active and available for
     * adhoc
     */
    public List<Map<String, Object>> getDoctorsForAdhocAppointments() {
        try {
            // For now, return all active doctors as adhoc available
            // In a real implementation, this would check availability status
            return getAllDoctors();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors for adhoc appointments: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors assigned to a specific patient
     * Based on stored procedure logic: doctors who have treated this patient
     */
    public List<Map<String, Object>> getDoctorsForPatient(String patientId) {
        try {
            // For now, return all active doctors
            // In a real implementation, this would query patient_visits table
            return getAllDoctors();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors for patient: " + e.getMessage(), e);
        }
    }

    /**
     * Get detailed information about a specific doctor
     * Based on stored procedure logic: get doctor by ID
     */
    public List<Map<String, Object>> getDoctorDetails(String doctorId) {
        try {
            return doctorMasterRepository.findByDoctorId(doctorId)
                    .map(doctor -> List.of(convertDoctorToMap(doctor)))
                    .orElse(List.of());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor details: " + e.getMessage(), e);
        }
    }

    /**
     * Get total count of doctors in the system
     * Based on stored procedure: usp_get_doctor_count()
     */
    public List<Map<String, Object>> getDoctorCount() {
        try {
            List<AuthDoctorMaster> allDoctors = doctorMasterRepository.findAll();
            long totalDoctors = allDoctors.size();
            long activeDoctors = allDoctors.size(); // All doctors are considered active in this schema
            long inactiveDoctors = totalDoctors - activeDoctors;

            Map<String, Object> countData = new HashMap<>();
            countData.put("total_doctors", totalDoctors);
            countData.put("active_doctors", activeDoctors);
            countData.put("inactive_doctors", inactiveDoctors);
            countData.put("doctors_with_clinics", activeDoctors); // Simplified for now

            return List.of(countData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor count: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctors who are ready to submit their work
     * Based on stored procedure logic: doctors with pending work
     */
    public List<Map<String, Object>> getDoctorsReadyForSubmission(String doctorId) {
        try {
            // For now, return empty list
            // In a real implementation, this would check for pending work
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctors ready for submission: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctor status reference data
     * Based on stored procedure logic: get status options
     */
    public List<Map<String, Object>> getDoctorStatusReference() {
        try {
            // Return common doctor status options
            return List.of(
                    Map.of("status_id", 1, "status_name", "Active"),
                    Map.of("status_id", 2, "status_name", "Inactive"),
                    Map.of("status_id", 3, "status_name", "On Leave"),
                    Map.of("status_id", 4, "status_name", "Retired"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor status reference: " + e.getMessage(), e);
        }
    }

    /**
     * Get today's visits for a specific doctor
     * Based on stored procedure logic: get today's visits
     */
    public List<Map<String, Object>> getDoctorTodaysVisits(String doctorId) {
        try {
            // For now, return empty list
            // In a real implementation, this would query patient_visits table
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor today's visits: " + e.getMessage(), e);
        }
    }

    /**
     * Get fees to be collected by a doctor
     * Based on stored procedure logic: get pending fees
     */
    public List<Map<String, Object>> getFeesToCollectByDoctor(String doctorId) {
        try {
            // For now, return empty list
            // In a real implementation, this would query billing tables
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get fees to collect by doctor: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a doctor by ID
     */
    public void deleteDoctor(String doctorId) {
        try {
            doctorMasterRepository.deleteById(doctorId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete doctor: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctor by ID
     */
    public AuthDoctorMaster getDoctorById(String doctorId) {
        try {
            return doctorMasterRepository.findByDoctorId(doctorId).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get doctor by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Create or update a doctor
     */
    public AuthDoctorMaster saveDoctor(AuthDoctorMaster doctor) {
        try {
            if (doctor.getDoctorId() == null || doctor.getDoctorId().isEmpty()) {
                // Generate 10-char unique ID (uppercase alphanumeric)
                doctor.setDoctorId(
                        java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            }
            doctor.setPrefix("Dr.");
            doctor.setCreatedOn(java.time.LocalDateTime.now());
            doctor.setCreatedbyName("Admin");
            doctor.setModifiedOn(java.time.LocalDateTime.now());
            doctor.setModifiedbyName("Admin");
            return doctorMasterRepository.save(doctor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save doctor: " + e.getMessage(), e);
        }
    }

    public AuthDoctorMaster updateDoctor(String doctorId, AuthDoctorMaster doctor) {
        // Ensure the clinicId matches the path variable
        if (!doctorId.equals(doctor.getDoctorId())) {
            throw new IllegalArgumentException("Doctor ID in path does not match request body");
        }

        // Update modified info
        doctor.setModifiedOn(java.time.LocalDateTime.now());
        doctor.setModifiedbyName("Admin");

        return doctorMasterRepository.save(doctor);
    }

    /**
     * Convert query result array to Map (for native query results)
     * Query result format: [prefix, first_name, last_name, doctor_id, speciality, name_with_prefix]
     */
    private Map<String, Object> convertQueryResultToMap(Object[] result) {
        Map<String, Object> doctorMap = new HashMap<>();
        
        String prefix = result[0] != null ? result[0].toString().trim() : "";
        String firstName = result[1] != null ? result[1].toString().trim() : "";
        String lastName = result[2] != null ? result[2].toString().trim() : "";
        String doctorId = result[3] != null ? result[3].toString().trim() : "";
        String speciality = result[4] != null ? result[4].toString().trim() : "";
        String nameWithPrefix = result[5] != null ? result[5].toString().trim() : "";
        
        // Uppercase keys for backward compatibility with climasys2.0
        doctorMap.put("Doctor_ID", doctorId);
        doctorMap.put("Prefix", prefix);
        doctorMap.put("First_Name", firstName);
        doctorMap.put("Speciality", speciality);
        doctorMap.put("NameWithPrefix", nameWithPrefix);
        
        // Lowercase keys for API compatibility
        doctorMap.put("doctor_id", doctorId);
        doctorMap.put("doctorId", doctorId);
        doctorMap.put("id", doctorId);
        doctorMap.put("prefix", prefix);
        doctorMap.put("first_name", firstName);
        doctorMap.put("firstName", firstName);
        doctorMap.put("last_name", lastName);
        doctorMap.put("lastName", lastName);
        doctorMap.put("speciality", speciality);
        doctorMap.put("specialty", speciality);
        doctorMap.put("specialization", speciality);
        doctorMap.put("name_with_prefix", nameWithPrefix);
        
        // Frontend expects these fields
        doctorMap.put("name", nameWithPrefix); // Use NameWithPrefix as the display name
        doctorMap.put("doctorName", nameWithPrefix);
        doctorMap.put("doctor_name", nameWithPrefix);
        
        // OPD doctor flag (all results are OPD doctors from this query)
        doctorMap.put("opd_dr", true);
        doctorMap.put("OPD_DR", true);
        doctorMap.put("opdDoctor", true);
        
        return doctorMap;
    }
    
    /**
     * Helper method to convert AuthDoctorMaster entity to Map
     * Based on stored procedure output format
     * Returns the same format as convertQueryResultToMap for consistency
     */
    private Map<String, Object> convertDoctorToMap(AuthDoctorMaster doctor) {
        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("doctor_id", doctor.getDoctorId());

        // Build full name like in stored procedure
        StringBuilder fullName = new StringBuilder();
        if (doctor.getFirstName() != null)
            fullName.append(doctor.getFirstName());
        if (doctor.getMiddleName() != null) {
            if (fullName.length() > 0)
                fullName.append(" ");
            fullName.append(doctor.getMiddleName());
        }
        if (doctor.getLastName() != null) {
            if (fullName.length() > 0)
                fullName.append(" ");
            fullName.append(doctor.getLastName());
        }
        doctorMap.put("doctor_name", fullName.toString());

        doctorMap.put("qualification", doctor.getDoctorQual());
        doctorMap.put("specialization", doctor.getSpeciality());
        doctorMap.put("phone", doctor.getMobile1());
        doctorMap.put("email", doctor.getEmailid());
        doctorMap.put("is_active", true); // All doctors are considered active in this schema
        doctorMap.put("address", doctor.getResidentialAdd1()); // Use residential address

        // New fields
        doctorMap.put("registration_no", doctor.getRegistrationNo());

        // OPD/IPD logic
        String opdIpd = "";
        if (Boolean.TRUE.equals(doctor.getOpdDr()) && Boolean.TRUE.equals(doctor.getIpdDr())) {
            opdIpd = "OPD/IPD";
        } else if (Boolean.TRUE.equals(doctor.getOpdDr())) {
            opdIpd = "OPD";
        } else if (Boolean.TRUE.equals(doctor.getIpdDr())) {
            opdIpd = "IPD";
        }
        doctorMap.put("opd_ipd", opdIpd);

        // Clinic Names
        try {
            List<com.climasys.entity.DoctorClinicShift> shifts = doctorClinicShiftRepository
                    .findByDoctorId(doctor.getDoctorId());
            String clinicNames = shifts.stream()
                    .map(shift -> shift.getId().getClinicId())
                    .distinct()
                    .map(clinicId -> clinicRepository
                            .findById(new com.climasys.entity.ClinicId(doctor.getDoctorId(), clinicId))
                            .map(com.climasys.entity.Clinic::getClinicName)
                            .orElse(""))
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.joining(", "));
            doctorMap.put("clinic_name", clinicNames);
        } catch (Exception e) {
            doctorMap.put("clinic_name", ""); // Fallback
        }

        // OPD/IPD doctor flags
        doctorMap.put("opd_dr", doctor.getOpdDr() != null ? doctor.getOpdDr() : false);
        doctorMap.put("OPD_DR", doctor.getOpdDr() != null ? doctor.getOpdDr() : false);
        doctorMap.put("opdDoctor", doctor.getOpdDr() != null ? doctor.getOpdDr() : false);
        doctorMap.put("ipd_dr", doctor.getIpdDr() != null ? doctor.getIpdDr() : false);
        return doctorMap;
    }
}
