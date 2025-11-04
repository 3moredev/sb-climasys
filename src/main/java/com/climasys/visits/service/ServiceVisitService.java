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
            
            List<Object[]> rows;
            if (doctorId != null && !doctorId.trim().isEmpty()) {
                // If doctorId is provided, use the filtered query
                rows = serviceVisitRepository.findPreviousServiceVisitDates(patientId, doctorId, clinicId, todaysVisitDate);
            } else {
                // If doctorId is not provided, fetch all visits for the patient and clinic (ignoring doctor)
                rows = serviceVisitRepository.findPreviousServiceVisitDatesWithoutDoctor(patientId, clinicId, todaysVisitDate);
                logger.info("Fetching previous service visit dates without doctor filter for patient: {}, clinic: {}", 
                    patientId, clinicId);
            }
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
        List<Map<String, Object>> items = new ArrayList<>(); // Always initialize
        try {
            logger.info("Fetching previous service visit line-items for patient: {}, doctor: {}, clinic: {}, shiftId: {}, visitNo: {}, visitDate: {}", 
                patientId, doctorId, clinicId, shiftId, visitNo, visitDate);
            
            // Match stored procedure logic: check overwrite table first, then fallback to base table
            // Stored procedure uses: Patient_ID, Clinic_ID, Patient_Visit_No only (Shift_ID, Doctor_ID, Visit_Date are not filtered)
            List<Object[]> rows;
            
            boolean existsInOverwrite = serviceVisitRepository.existsInBillingInfoOverwrite(patientId, clinicId, visitNo);
            
            if (existsInOverwrite) {
                logger.info("Found data in overwrite table, querying from patient_visit_services_billinginfooverwrite");
                rows = serviceVisitRepository.findServiceVisitLineItemsFromOverwrite(patientId, clinicId, visitNo);
                logger.info("Found {} service visit line-item records from overwrite table", rows.size());
            } else {
                logger.info("No data in overwrite table, querying from base table patient_visit_services_billinginfo");
                rows = serviceVisitRepository.findServiceVisitLineItemsFromBase(patientId, clinicId, visitNo);
                logger.info("Found {} service visit line-item records from base table", rows.size());
            }
            
            if (rows.isEmpty()) {
                logger.warn("No service visit line-items found for patient: {}, clinic: {}, visitNo: {}. " +
                    "This could mean: 1) No billing info exists in either table for this visit, " +
                    "2) All records are marked as deleted (delete_flag=true) in overwrite table", 
                    patientId, clinicId, visitNo);
                response.put("success", true);
                response.put("items", items); // Return empty array
                response.put("message", "No service visit line-items found for the specified visit");
                return response;
            }
            
            for (Object[] r : rows) {
                if (r == null || r.length < 6) {
                    logger.warn("Skipping invalid row data - row is null or has insufficient columns (expected 6, got {})", r != null ? r.length : 0);
                    continue;
                }
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
            logger.info("Returning {} service visit line-items (from {})", items.size(), existsInOverwrite ? "overwrite table" : "base table");
        } catch (Exception e) {
            logger.error("Failed to fetch previous service line-items for patient: {}, doctor: {}, clinic: {}, shiftId: {}, visitNo: {}, visitDate: {}: {}", 
                patientId, doctorId, clinicId, shiftId, visitNo, visitDate, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to fetch previous service line-items: " + e.getMessage());
            response.put("items", items); // Always include items, even on error
        }
        return response;
    }
}


