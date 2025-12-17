package com.climasys.billing.service;

import com.climasys.billing.dto.OPDDailyCollectionDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for OPD Daily Collection operations
 * Returns dummy data instead of database queries
 */
@Service
public class OPDDailyCollectionService {
    
    // Dummy doctors data
    private static final List<String[]> DUMMY_DOCTORS = Arrays.asList(
        new String[]{"DOC-001", "Dr. Tongaonkar"},
        new String[]{"DOC-002", "Dr. Sharma"},
        new String[]{"DOC-003", "Dr. Patel"},
        new String[]{"DOC-004", "Dr. Kumar"},
        new String[]{"DOC-005", "Dr. Singh"}
    );
    
    // Dummy patients data
    private static final List<String[]> DUMMY_PATIENTS = Arrays.asList(
        new String[]{"AMIT SOMRA JAIN", "F-001", "P-001", "Male", "35"},
        new String[]{"RUPINA J SHAHJI", "F-002", "P-002", "Female", "42"},
        new String[]{"RAJESH KUMAR", "F-003", "P-003", "Male", "28"},
        new String[]{"PRIYA SHARMA", "F-004", "P-004", "Female", "31"},
        new String[]{"VIJAY PATEL", "F-005", "P-005", "Male", "45"},
        new String[]{"ANITA DESAI", "F-006", "P-006", "Female", "38"},
        new String[]{"SURESH MEHTA", "F-007", "P-007", "Male", "52"},
        new String[]{"KAVITA RAO", "F-008", "P-008", "Female", "29"},
        new String[]{"MOHAN LAL", "F-009", "P-009", "Male", "41"},
        new String[]{"GEETA VERMA", "F-010", "P-010", "Female", "36"}
    );
    
    // Visit times
    private static final List<int[]> VISIT_TIMES = Arrays.asList(
        new int[]{10, 0, 21},
        new int[]{10, 55, 58},
        new int[]{11, 30, 15},
        new int[]{12, 15, 42},
        new int[]{13, 0, 5},
        new int[]{14, 20, 33},
        new int[]{15, 10, 18},
        new int[]{16, 5, 50},
        new int[]{16, 45, 12},
        new int[]{17, 30, 7}
    );
    
    private static final List<String> PAYMENT_METHODS = Arrays.asList("Cash", "Card", "UPI", "Cheque", "");
    private static final List<String> FOLLOW_UP_TYPES = Arrays.asList("New", "Follow up");
    private static final List<String> REASONS = Arrays.asList("Regular Checkup", "Follow-up", "Consultation", "Review", "--");
    private static final List<Integer> ORIGINAL_AMOUNTS = Arrays.asList(500, 600, 750, 400, 800, 550, 650, 700, 450, 900);
    
    /**
     * Get OPD Daily Collection data using dummy data
     * 
     * @param fromDate Start date for collection period
     * @param toDate End date for collection period
     * @param clinicId Clinic ID filter
     * @param doctorId Doctor ID filter (can be "All" or "0" for all doctors)
     * @param roleId Role ID filter
     * @param languageId Language ID for translations
     * @return List of OPDDailyCollectionDTO objects
     */
    public List<OPDDailyCollectionDTO> getOPDDailyCollection(
            LocalDate fromDate,
            LocalDate toDate,
            String clinicId,
            String doctorId,
            Integer roleId,
            Integer languageId
    ) {
        // Generate dummy data
        List<OPDDailyCollectionDTO> dtos = new ArrayList<>();
        
        // Filter doctors based on doctorId
        List<String[]> selectedDoctors = (doctorId == null || doctorId.equals("All") || doctorId.equals("0"))
                ? DUMMY_DOCTORS
                : DUMMY_DOCTORS.stream()
                    .filter(d -> d[0].equals(doctorId))
                    .collect(Collectors.toList());
        
        if (selectedDoctors.isEmpty()) {
            selectedDoctors = DUMMY_DOCTORS;
        }
        
        // Generate records for each patient
        for (int i = 0; i < DUMMY_PATIENTS.size(); i++) {
            String[] patient = DUMMY_PATIENTS.get(i);
            int[] visitTime = VISIT_TIMES.get(i % VISIT_TIMES.size());
            String[] doctor = selectedDoctors.get(i % selectedDoctors.size());
            
            // Calculate amounts
            int originalAmount = ORIGINAL_AMOUNTS.get(i % ORIGINAL_AMOUNTS.size());
            int discount = (i % 3 == 1) ? 50 : ((i % 3 == 2) ? 100 : 0);
            int originalDiscount = (i % 4 == 1) ? 25 : ((i % 4 == 2) ? 75 : 0);
            int billed = originalAmount - originalDiscount;
            int net = billed - discount;
            double collected = (i % 2 == 0) ? net : ((i % 3 == 1) ? net * 0.5 : ((i % 3 == 2) ? net * 0.8 : 0));
            double dues = net - collected;
            int adhoc = (i % 5 == 0) ? ((i % 3 == 1) ? 100 : ((i % 3 == 2) ? 200 : 0)) : 0;
            int difference = originalAmount - billed;
            
            // Format visit time
            String visitTimeStr = String.format("%02d:%02d:%02d", visitTime[0], visitTime[1], visitTime[2]);
            
            OPDDailyCollectionDTO dto = new OPDDailyCollectionDTO();
            dto.setVisitDate(visitTimeStr);
            dto.setName(patient[0]);
            dto.setPatientId(patient[2]);
            dto.setStatusDescription("Completed");
            dto.setStatusId((short) 1);
            dto.setFeesToCollect(BigDecimal.valueOf(billed));
            dto.setFeesCollected(BigDecimal.valueOf(collected));
            dto.setAdhocFees(BigDecimal.valueOf(adhoc));
            dto.setOriginalBilledAmount(BigDecimal.valueOf(originalAmount));
            dto.setFolderNo(patient[1]);
            dto.setComment(REASONS.get(i % REASONS.size()));
            dto.setDifference(BigDecimal.valueOf(difference));
            dto.setDues(BigDecimal.valueOf(dues));
            dto.setOriginalDiscount(BigDecimal.valueOf(originalDiscount));
            dto.setDiscount(BigDecimal.valueOf(discount));
            dto.setNet(BigDecimal.valueOf(net));
            dto.setInPerson(true);
            dto.setAttendedBy(doctor[1]);
            dto.setPaymentById((i % 2 == 0) ? (short) 1 : null);
            dto.setPaymentRemark(null);
            dto.setPaymentDescription(PAYMENT_METHODS.get(i % PAYMENT_METHODS.size()).isEmpty() ? null : PAYMENT_METHODS.get(i % PAYMENT_METHODS.size()));
            dto.setPartialName(patient[0].split(" ")[0]);
            dto.setAgeYearsIntRound(Integer.parseInt(patient[4]));
            dto.setGenderDescription(patient[3]);
            dto.setPatientVisitNo(i + 1);
            dto.setDoctorId(doctor[0]);
            dto.setDoctorName(doctor[1]);
            dto.setIsFollowUp(FOLLOW_UP_TYPES.get(i % FOLLOW_UP_TYPES.size()));
            dto.setBaseLocation(null);
            
            dtos.add(dto);
        }
        
        return dtos;
    }
}

