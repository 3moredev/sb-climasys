package com.climasys.refdata.service;

import com.climasys.repository.RefDataRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatientProfileRefDataService {

    private final RefDataRepository refDataRepository;

    public PatientProfileRefDataService(RefDataRepository refDataRepository) {
        this.refDataRepository = refDataRepository;
    }

    public Map<String, Object> getRefData(String doctorId, String clinicId) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("medicineMaster", mapRows(refDataRepository.findMedicineMaster(doctorId, clinicId),
                "short_description", "medicine_description", "priority_value"));

        result.put("complaints", mapRows(refDataRepository.findComplaints(doctorId, clinicId),
                "short_description", "complaint_description", "priority_value"));

        result.put("dressings", mapRows(refDataRepository.findDressings(doctorId, clinicId),
                "short_description", "dressing_description", "priority_value"));

        result.put("diagnosis", mapRows(refDataRepository.findDiagnosis(doctorId, clinicId),
                "short_description", "diagnosis_description", "priority_value"));

        result.put("prescriptionMedicines", mapRows(refDataRepository.findPrescriptionMedicines(doctorId, clinicId),
                "medicine_name", "brand_name", "cat_short_name", "catsub_description", "priority_value"));

        result.put("procedures", mapRows(refDataRepository.findProcedures(doctorId, clinicId),
                "procedure_description", "priority_value"));

        result.put("instructionGroups", mapInstructionGroups(refDataRepository.findInstructionGroups(doctorId, clinicId)));

        result.put("operatorComplaints", mapRows(refDataRepository.findOperatorComplaints(doctorId, clinicId),
                "short_description", "complaint_description", "priority_value"));

        result.put("prescriptionSearch", refDataRepository.buildPrescriptionSearch(doctorId, clinicId));

        result.put("success", true);
        result.put("doctorId", doctorId);
        result.put("clinicId", clinicId);
        return result;
    }

    /**
     * JPA equivalent to USP_Search_PrescriptionForPatientProfile
     * Matches the stored procedure logic exactly:
     * - Processes search string (adds % wildcards around words, matching original WebService behavior)
     * - Returns two result sets: filtered by doctor/clinic and all active prescriptions for clinic
     * - Added clinic_id filter for multi-clinic support (stored procedure doesn't use it, but table has it)
     */
    public Map<String, Object> searchPrescriptionForPatientProfile(String prefixText, String doctorId, String clinicId) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        // Process search string: match original WebService logic exactly
        // Original code: if (prefixText.Split(' ').Length > 0) - always processes (array always has at least 1 element)
        String searchStr = prefixText;
        if (prefixText != null && !prefixText.trim().isEmpty()) {
            String[] words = prefixText.trim().split("\\s+");
            StringBuilder filter = new StringBuilder();
            for (String word : words) {
                filter.append("%").append(word).append("%");
            }
            searchStr = filter.toString();
        }
        
        // Get first result set: filtered by doctor_id and clinic_id
        List<String> resultSet1 = refDataRepository.searchPrescriptionForPatientProfileWithDoctor(searchStr, doctorId, clinicId);
        
        // Get second result set: all active prescriptions for clinic (no doctor filter)
        List<String> resultSet2 = refDataRepository.searchPrescriptionForPatientProfileAll(searchStr, clinicId);
        
        // Match the stored procedure response structure
        // The original returns two result sets, but for API we'll combine them
        result.put("resultSet1", resultSet1);
        result.put("resultSet2", resultSet2);
        result.put("success", true);
        
        return result;
    }

    /**
     * Map instruction groups with their instructions
     * Groups multiple instructions under each group_description
     */
    private List<Map<String, Object>> mapInstructionGroups(List<Object[]> rows) {
        // Group by group_description to combine multiple instructions
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        
        for (Object[] r : rows) {
            String groupDescription = (String) r[0];
            Integer priorityValue = (Integer) r[1];
            String instructionsDescription = (String) r[2];
            
            // Get or create the group map
            Map<String, Object> groupMap = grouped.computeIfAbsent(groupDescription, k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("group_description", groupDescription);
                m.put("priority_value", priorityValue);
                m.put("instructions_description", new ArrayList<String>());
                return m;
            });
            
            // Add instruction to the list if it exists
            if (instructionsDescription != null) {
                @SuppressWarnings("unchecked")
                List<String> instructions = (List<String>) groupMap.get("instructions_description");
                instructions.add(instructionsDescription);
            }
        }
        
        return new ArrayList<>(grouped.values());
    }

    private List<Map<String, Object>> mapRows(List<Object[]> rows, String... keys) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new LinkedHashMap<>();
            for (int i = 0; i < keys.length && i < r.length; i++) {
                m.put(keys[i], r[i]);
            }
            list.add(m);
        }
        return list;
    }
}


