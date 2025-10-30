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

        result.put("instructionGroups", mapRows(refDataRepository.findInstructionGroups(doctorId, clinicId),
                "group_description", "priority_value"));

        result.put("operatorComplaints", mapRows(refDataRepository.findOperatorComplaints(doctorId, clinicId),
                "short_description", "complaint_description", "priority_value"));

        result.put("prescriptionSearch", refDataRepository.buildPrescriptionSearch(doctorId, clinicId));

        result.put("success", true);
        result.put("doctorId", doctorId);
        result.put("clinicId", clinicId);
        return result;
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


