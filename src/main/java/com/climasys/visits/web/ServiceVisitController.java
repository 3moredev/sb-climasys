package com.climasys.visits.web;

import com.climasys.visits.service.ServiceVisitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceVisitController {

    private final ServiceVisitService serviceVisitService;

    public ServiceVisitController(ServiceVisitService serviceVisitService) {
        this.serviceVisitService = serviceVisitService;
    }

    @GetMapping("/previous-visit-dates")
    public ResponseEntity<?> getPreviousServiceVisitDates(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String clinicId,
            @RequestParam(required = false) String todaysVisitDate
    ) {
        LocalDate today = (todaysVisitDate == null || todaysVisitDate.isBlank())
                ? LocalDate.now() : LocalDate.parse(todaysVisitDate);
        Map<String, Object> result = serviceVisitService.getPreviousServiceVisitDates(patientId, doctorId, clinicId, today);
        boolean ok = Boolean.TRUE.equals(result.get("success"));
        return ok ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/previous-visit-items")
    public ResponseEntity<?> getPreviousServiceVisitItems(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String clinicId,
            @RequestParam Short shiftId,
            @RequestParam Integer visitNo,
            @RequestParam String visitDate
    ) {
        LocalDate vDate = LocalDate.parse(visitDate);
        Map<String, Object> result = serviceVisitService.getPreviousServiceVisitLineItems(
                patientId, doctorId, clinicId, shiftId, visitNo, vDate);
        boolean ok = Boolean.TRUE.equals(result.get("success"));
        return ok ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
}


