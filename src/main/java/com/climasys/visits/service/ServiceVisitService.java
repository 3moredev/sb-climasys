package com.climasys.visits.service;

import com.climasys.repository.ServiceVisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ServiceVisitService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceVisitService.class);

    private final ServiceVisitRepository serviceVisitRepository;

    public ServiceVisitService(ServiceVisitRepository serviceVisitRepository) {
        this.serviceVisitRepository = serviceVisitRepository;
    }

    public Map<String, Object> getPreviousServiceVisitDates(String patientId, String doctorId, String clinicId, LocalDate todaysVisitDate) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Object[]> rows = serviceVisitRepository.findPreviousServiceVisitDates(patientId, doctorId, clinicId, todaysVisitDate);
            List<Map<String, Object>> visits = new ArrayList<>();
            for (Object[] r : rows) {
                Map<String, Object> m = new HashMap<>();
                m.put("visitDate", r[0]);
                m.put("shiftId", r[1]);
                m.put("patientVisitNo", r[2]);
                visits.add(m);
            }
            response.put("success", true);
            response.put("visits", visits);
        } catch (Exception e) {
            logger.error("Failed to fetch previous service visit dates: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to fetch previous service visit dates: " + e.getMessage());
        }
        return response;
    }

    public Map<String, Object> getPreviousServiceVisitLineItems(
            String patientId,
            String doctorId,
            String clinicId,
            Short shiftId,
            Integer visitNo,
            LocalDate visitDate
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Object[]> rows = serviceVisitRepository.findServiceVisitLineItems(patientId, doctorId, clinicId, shiftId, visitNo, visitDate);
            List<Map<String, Object>> items = new ArrayList<>();
            for (Object[] r : rows) {
                Map<String, Object> m = new HashMap<>();
                int i = 0;
                m.put("group", r[i++]);
                m.put("subGroup", r[i++]);
                m.put("details", r[i++]);
                m.put("amount", r[i++]);
                m.put("defaultFees", r[i++]);
                m.put("collectedFees", r[i]);
                items.add(m);
            }
            response.put("success", true);
            response.put("items", items);
        } catch (Exception e) {
            logger.error("Failed to fetch previous service line-items: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to fetch previous service line-items: " + e.getMessage());
        }
        return response;
    }
}


