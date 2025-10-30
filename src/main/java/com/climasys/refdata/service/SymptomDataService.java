package com.climasys.refdata.service;

import com.climasys.repository.RefDataRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SymptomDataService {

    private final RefDataRepository refDataRepository;

    public SymptomDataService(RefDataRepository refDataRepository) {
        this.refDataRepository = refDataRepository;
    }

    public Map<String, Object> getSymptomData(String doctorId, String clinicId) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Complaints (first result set in SP)
        result.put("complaints", mapRows(refDataRepository.findComplaints(doctorId, clinicId),
                "short_description", "complaint_description", "priority_value"));

        // Billing details (second result set in SP)
        result.put("billingDetails", mapRows(refDataRepository.findBillingDetailsForDoctor(doctorId, clinicId),
                "billing_details", "billing_group_name", "billing_subgroup_name", "default_fees",
                "visit_type", "visit_type_description", "visit_type_id", "isdefault", "sequence_no"));

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


