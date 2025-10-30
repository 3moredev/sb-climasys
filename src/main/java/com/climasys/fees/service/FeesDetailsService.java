package com.climasys.fees.service;

import com.climasys.repository.FeeDetailsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FeesDetailsService {

    private final FeeDetailsRepository feeDetailsRepository;

    public FeesDetailsService(FeeDetailsRepository feeDetailsRepository) {
        this.feeDetailsRepository = feeDetailsRepository;
    }

    public Map<String, Object> getPatientFeesDetails(String patientId) {
        List<Object[]> rows = feeDetailsRepository.findFeesDetailsByPatientId(patientId);
        List<Map<String, Object>> data = new ArrayList<>();

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        for (Object[] r : rows) {
            int i = 0;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("Patient_ID", r[i++]);
            m.put("Full_Name", r[i++]);
            m.put("Patient_Visit_No", r[i++]);
            Object visitDate = r[i++];
            m.put("Visit_Date", visitDate);
            m.put("Bill", r[i++]);
            m.put("Collected", r[i++]);
            m.put("Folder_No", r[i++]);
            m.put("Balance", r[i++]);
            m.put("Discount", r[i++]);
            m.put("Dues", r[i++]);
            String visitTimeText = Objects.toString(r[i++], "");
            String shiftInitial = Objects.toString(r[i++], "");
            m.put("Status_Description", r[i++]);
            m.put("ISadhoc", r[i++]);
            String receiptNumber = Objects.toString(r[i++], "");
            String receiptType = Objects.toString(r[i++], "");
            m.put("DoctorName", r[i++]);

            String lastVisitDate;
            if (visitDate instanceof java.sql.Timestamp ts) {
                LocalDate d = ts.toLocalDateTime().toLocalDate();
                lastVisitDate = dateFmt.format(d) + " - " + visitTimeText + " - " + shiftInitial;
            } else if (visitDate instanceof java.sql.Date d) {
                LocalDate ld = d.toLocalDate();
                lastVisitDate = dateFmt.format(ld) + " - " + visitTimeText + " - " + shiftInitial;
            } else if (visitDate instanceof LocalDateTime ldt) {
                lastVisitDate = dateFmt.format(ldt.toLocalDate()) + " - " + visitTimeText + " - " + shiftInitial;
            } else {
                lastVisitDate = Objects.toString(visitDate, "") + " - " + visitTimeText + " - " + shiftInitial;
            }
            m.put("LAST_VISIT_DATE", lastVisitDate);
            m.put("Receipt_Number", (receiptType + " " + receiptNumber).trim());

            data.add(m);
        }

        Map<String, Object> header = new HashMap<>();
        List<Object[]> folderName = feeDetailsRepository.findFolderAndName(patientId);
        if (!folderName.isEmpty()) {
            header.put("Folder_No", folderName.get(0)[0]);
            header.put("Full_Name", folderName.get(0)[1]);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("patientId", patientId);
        response.put("header", header);
        response.put("rows", data);
        return response;
    }
}


