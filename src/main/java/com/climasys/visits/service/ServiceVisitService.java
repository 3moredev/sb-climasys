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
            logger.info("Fetching previous service visit dates for patient: {}, doctor: {}, clinic: {}, today: {}", 
                patientId, doctorId, clinicId, todaysVisitDate);
            
            List<Object[]> rows = serviceVisitRepository.findPreviousServiceVisitDates(patientId, doctorId, clinicId, todaysVisitDate);
            logger.info("Found {} previous service visit records for patient: {}", rows.size(), patientId);
            
            List<Map<String, Object>> visits = new ArrayList<>();
            for (Object[] r : rows) {
                Map<String, Object> m = new HashMap<>();
                m.put("visitDate", r[0]);
                m.put("shiftId", r[1]);
                m.put("patientVisitNo", r[2]);
                visits.add(m);
                logger.debug("Added service visit: date={}, shiftId={}, visitNo={}", r[0], r[1], r[2]);
            }
            response.put("success", true);
            response.put("visits", visits);
            logger.info("Returning {} service visits for patient: {}", visits.size(), patientId);
        } catch (Exception e) {
            logger.error("Failed to fetch previous service visit dates for patient: {}, doctor: {}, clinic: {}: {}", 
                patientId, doctorId, clinicId, e.getMessage(), e);
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
            logger.info("Fetching previous service visit line-items for patient: {}, doctor: {}, clinic: {}, shiftId: {}, visitNo: {}, visitDate: {}", 
                patientId, doctorId, clinicId, shiftId, visitNo, visitDate);
            
            List<Object[]> rows = serviceVisitRepository.findServiceVisitLineItems(patientId, doctorId, clinicId, shiftId, visitNo, visitDate);
            logger.info("Found {} service visit line-item records for the given parameters", rows.size());
            
            if (rows.isEmpty()) {
                logger.warn("No service visit line-items found for patient: {}, doctor: {}, clinic: {}, shiftId: {}, visitNo: {}, visitDate: {}. " +
                    "This could mean: 1) No billing info exists in patient_visit_services_billinginfo table for this visit, " +
                    "2) The visit parameters don't match exactly, 3) All records are marked as deleted (delete_flag=true)", 
                    patientId, doctorId, clinicId, shiftId, visitNo, visitDate);
            }
            
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
                logger.debug("Added service line-item: group={}, subGroup={}, details={}, amount={}", 
                    m.get("group"), m.get("subGroup"), m.get("details"), m.get("amount"));
            }
            response.put("success", true);
            response.put("items", items);
            logger.info("Returning {} service visit line-items", items.size());
        } catch (Exception e) {
            logger.error("Failed to fetch previous service line-items for patient: {}, doctor: {}, clinic: {}, shiftId: {}, visitNo: {}, visitDate: {}: {}", 
                patientId, doctorId, clinicId, shiftId, visitNo, visitDate, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to fetch previous service line-items: " + e.getMessage());
        }
        return response;
    }
}


