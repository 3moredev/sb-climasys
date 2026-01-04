package com.climasys.discharge.service;

import com.climasys.discharge.dto.DischargeCardDTO;
import com.climasys.discharge.dto.DischargeCardDetailResponse;
import com.climasys.discharge.dto.UpdateDischargeCardRequest;
import com.climasys.discharge.repository.DischargeCardRepository;
import com.climasys.discharge.repository.DischargeDataRepository;
import com.climasys.admission.repository.AdmissionCardRepository;
import com.climasys.repository.PatientRepository;
import com.climasys.repository.DoctorMasterRepository;
import com.climasys.entity.DischargeData;
import com.climasys.entity.AdmissionData;
import com.climasys.entity.Patient;
import com.climasys.entity.DoctorMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for discharge card operations
 * Replaces USP_Get_Patient_All_Discharge_Cards stored procedure business logic
 * Handles Manage Discharge Card screen functionality
 */
@Service
@Transactional
public class DischargeCardService {

    private static final Logger logger = LoggerFactory.getLogger(DischargeCardService.class);

    @Autowired
    private DischargeCardRepository dischargeCardRepository;

    @Autowired
    private DischargeDataRepository dischargeDataRepository;

    @Autowired
    private AdmissionCardRepository admissionCardRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorMasterRepository doctorMasterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get all admitted patients for "List of Admitted Patient/s" table
     * Matches Table[5] from USP_Get_Patient_All_Discharge_Cards
     * Includes duplicate removal logic by IPD_RefNo (matching old codebase
     * behavior)
     * 
     * @param doctorId Doctor ID (optional - if null, returns all doctors for the
     *                 clinic)
     * @param clinicId Clinic ID
     * @return List of admitted patients
     */
    @Transactional(readOnly = true)
    public List<DischargeCardDTO> getAllAdmittedPatients(String doctorId, String clinicId) {
        logger.info("Getting all admitted patients for doctor: {}, clinic: {}",
                doctorId != null ? doctorId : "ALL", clinicId);

        List<Map<String, Object>> results = dischargeCardRepository
                .findAllAdmittedPatients(doctorId, clinicId);

        // Apply duplicate removal logic by IPD_RefNo (matching Table[5] behavior)
        List<DischargeCardDTO> dischargeCards = removeDuplicatesByIpdRefNo(results);

        logger.info("Retrieved {} admitted patient(s) after duplicate removal", dischargeCards.size());
        return dischargeCards;
    }

    /**
     * Get discharge cards for a specific patient (search results)
     * Matches Table[0] from USP_Get_Patient_All_Discharge_Cards
     * Used when searching for a specific patient
     * 
     * @param patientId Patient ID
     * @param doctorId  Doctor ID (optional - if null, returns all doctors for the
     *                  clinic)
     * @param clinicId  Clinic ID
     * @return List of discharge cards for the patient
     */
    @Transactional(readOnly = true)
    public List<DischargeCardDTO> getDischargeCardsByPatient(String patientId, String doctorId, String clinicId) {
        logger.info("Getting discharge cards for patient: {}, doctor: {}, clinic: {}",
                patientId, doctorId != null ? doctorId : "ALL", clinicId);

        List<Map<String, Object>> results = dischargeCardRepository
                .findDischargeCardsByPatient(patientId, doctorId, clinicId);

        List<DischargeCardDTO> dischargeCards = results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Retrieved {} discharge card(s) for patient: {}", dischargeCards.size(), patientId);
        return dischargeCards;
    }

    /**
     * Search discharge cards by patient ID, name, contact, or IPD number
     * Used for the search functionality on Manage Discharge Card screen
     * 
     * @param searchStr Search string
     * @param doctorId  Doctor ID (optional - if null, searches all doctors for the
     *                  clinic)
     * @param clinicId  Clinic ID
     * @return List of matching discharge cards
     */
    @Transactional(readOnly = true)
    public List<DischargeCardDTO> searchDischargeCards(String searchStr, String doctorId, String clinicId) {
        logger.info("Searching discharge cards for: '{}', doctor: {}, clinic: {}",
                searchStr, doctorId != null ? doctorId : "ALL", clinicId);

        List<Map<String, Object>> results = dischargeCardRepository
                .searchDischargeCards(searchStr, doctorId, clinicId);

        List<DischargeCardDTO> dischargeCards = results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Found {} matching discharge card(s)", dischargeCards.size());
        return dischargeCards;
    }

    /**
     * Remove duplicates by IPD_RefNo (matching old codebase logic for Table[5])
     * The old codebase removes duplicate IPD_RefNo entries from Table[5]
     * Matches the logic: if UniqueRecordsGroup.Contains(dRow["IPD_RefNo"]) then add
     * to DuplicateRecordsGroup
     */
    private List<DischargeCardDTO> removeDuplicatesByIpdRefNo(List<Map<String, Object>> results) {
        Set<String> seenIpdRefNos = new LinkedHashSet<>();
        List<DischargeCardDTO> uniqueCards = new ArrayList<>();

        for (Map<String, Object> result : results) {
            // Try both lowercase and camelCase keys (PostgreSQL typically returns
            // lowercase)
            String ipdRefNo = getStringValue(result, "ipdrefno");
            if (ipdRefNo == null || ipdRefNo.isEmpty()) {
                ipdRefNo = getStringValue(result, "ipdRefNo");
            }

            if (ipdRefNo != null && !ipdRefNo.isEmpty()) {
                if (!seenIpdRefNos.contains(ipdRefNo)) {
                    seenIpdRefNos.add(ipdRefNo);
                    uniqueCards.add(mapToDTO(result));
                }
                // Skip duplicates (matching old codebase behavior)
            } else {
                // Include records without IPD_RefNo
                uniqueCards.add(mapToDTO(result));
            }
        }

        return uniqueCards;
    }

    /**
     * Map database result to DTO
     */
    private DischargeCardDTO mapToDTO(Map<String, Object> result) {
        DischargeCardDTO dto = new DischargeCardDTO();

        dto.setSerialNumber(getIntegerValue(result, "serialnumber"));
        dto.setPatientName(getStringValue(result, "patientname"));
        dto.setIpdNo(getStringValue(result, "ipdno"));
        dto.setIpdFileNo(getStringValue(result, "ipdfileno"));
        dto.setAdmissionDate(getStringValue(result, "admissiondate"));
        dto.setDischargeDate(getStringValue(result, "dischargedate"));
        dto.setKeyword(getStringValue(result, "keyword"));
        dto.setAdvanceRs(getBigDecimalValue(result, "advancers"));
        dto.setPatientId(getStringValue(result, "patientid"));
        dto.setIpdRefNo(getStringValue(result, "ipdrefno"));

        return dto;
    }

    private String getStringValue(Map<String, Object> result, String key) {
        Object value = result.get(key);
        return value != null ? value.toString() : "";
    }

    private Integer getIntegerValue(Map<String, Object> result, String key) {
        Object value = result.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        return 0;
    }

    private BigDecimal getBigDecimalValue(Map<String, Object> result, String key) {
        Object value = result.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get discharge card details for a specific patient and IPD
     * Replaces USP_Get_Patient_DischargeCard_Data stored procedure
     * 
     * @param patientId Patient ID
     * @param shiftId   Shift ID
     * @param clinicId  Clinic ID
     * @param doctorId  Doctor ID
     * @param ipdNo     IPD Number
     * @param invoiceNo Invoice Number (optional)
     * @return Discharge card detail response with all related data
     */
    @Transactional(readOnly = true)
    public DischargeCardDetailResponse getDischargeCardDetails(
            String patientId, String clinicId, String doctorId, String ipdRefNo, String invoiceNo) {

        logger.info("Getting discharge card details for patient: {}, IPD: {}", patientId, ipdRefNo);

        DischargeCardDetailResponse response = new DischargeCardDetailResponse();

        // Get main discharge card data (Table[0])
        // Note: shiftId, clinicId, and invoiceNo are not used in the query (matching
        // stored procedure behavior)

        // 1. Fetch DischargeData
        DischargeData dischargeData = dischargeDataRepository.findByPatientIdAndIpdRefno(patientId, ipdRefNo)
                .orElse(null);

        if (dischargeData != null) {
            AdmissionData admissionData = admissionCardRepository.findById(
                    new com.climasys.entity.AdmissionDataId(patientId, doctorId, clinicId, ipdRefNo))
                    .orElse(null);

            Map<String, Object> patientDetails = fetchPatientDetails(patientId);

            DischargeCardDetailResponse.DischargeCardMainData mainData = mapToDischargeCardMainDataFromEntity(
                    dischargeData, admissionData, patientDetails);
            response.setMainData(mainData);
        }

        // Get investigations
        // List<DischargeCardDetailResponse.DischargeInvestigationDTO> investigations =
        // getDischargeInvestigations(
        // patientId, ipdRefNo, doctorId, clinicId);
        // response.setInvestigations(investigations);

        // Get invoice details
        // List<DischargeCardDetailResponse.DischargeInvoiceDetailDTO> invoiceDetails =
        // getDischargeInvoiceDetails(
        // doctorId, clinicId, patientId, ipdRefNo, invoiceNo, srNo);
        // response.setInvoiceDetails(invoiceDetails);

        // Get invoice header
        // DischargeCardDetailResponse.DischargeInvoiceHeaderDTO invoiceHeader =
        // getDischargeInvoiceHeader(patientId,
        // invoiceNo);
        // response.setInvoiceHeader(invoiceHeader);

        // Get bill details
        // List<DischargeCardDetailResponse.DischargeBillDetailDTO> billDetails =
        // getDischargeBillDetails(patientId,
        // invoiceNo);
        // response.setBillDetails(billDetails);

        // Get bill header
        // DischargeCardDetailResponse.DischargeBillHeaderDTO billHeader =
        // getDischargeBillHeader(patientId, ipdRefNo,
        // invoiceNo);
        // response.setBillHeader(billHeader);

        // Get labour card
        // DischargeCardDetailResponse.LabourCardDTO labourCard =
        // getLabourCard(ipdRefNo);
        // response.setLabourCard(labourCard);

        // Get total advance
        // BigDecimal totalAdvance = getTotalAdvance(ipdRefNo);
        // response.setTotalAdvance(totalAdvance);

        // Get last advance date
        // LocalDate lastAdvanceDate = getLastAdvanceDate(ipdRefNo);
        // response.setLastAdvanceDate(lastAdvanceDate);

        logger.info("Retrieved discharge card details for patient: {}, IPD: {}", patientId, ipdRefNo);
        return response;
    }

    /**
     * Save/Update discharge card details
     * Replaces USP_Insert_DischargeData stored procedure
     * 
     * @param request Update discharge card request
     * @return Map with save status and IPD number
     */
    @Transactional
    public Map<String, Object> saveDischargeCardDetails(UpdateDischargeCardRequest request) {
        logger.info("Saving discharge card details for patient: {}, IPD: {}",
                request.getPatientId(), request.getIpdRefNo());

        Map<String, Object> result = new HashMap<>();

        try {
            // Check for date conflicts (matching SP logic)
            boolean dischargeExists = checkDischargeDateConflict(request);
            if (dischargeExists) {
                result.put("dischargeExists", true);
                result.put("saveStatus", 0);
                result.put("message", "Discharge card already exists for this date range");
                return result;
            }

            // Generate IPD_RefNo if not provided (matching SP logic)
            // UPDATE: As per request, preventing new IPD generation. IPD Ref No is
            // required.
            String ipdRefNo = request.getIpdRefNo();
            if (ipdRefNo == null || ipdRefNo.trim().isEmpty()) {
                throw new RuntimeException("IPD Ref No is required for updating discharge card");
            }

            // MERGE discharge_data (insert or update)
            mergeDischargeData(request);

            // Insert discharge investigations (attachments)
            if (request.getKeywordAttachments() != null && !request.getKeywordAttachments().isEmpty()) {
                insertDischargeInvestigations(request);
            }

            // Update admission_data
            updateAdmissionData(request);

            result.put("saveStatus", 1);
            result.put("ipdNo", ipdRefNo);
            result.put("dischargeExists", false);
            result.put("message", "Discharge card saved successfully");

            logger.info("Successfully saved discharge card details for patient: {}, IPD: {}",
                    request.getPatientId(), ipdRefNo);

        } catch (Exception e) {
            logger.error("Error saving discharge card details for patient: {}, IPD: {}",
                    request.getPatientId(), request.getIpdRefNo(), e);
            result.put("saveStatus", 0);
            result.put("error", e.getMessage());
            throw new RuntimeException("Failed to save discharge card details", e);
        }

        return result;
    }

    private Map<String, Object> fetchPatientDetails(String patientId) {
        String sql = """
                SELECT
                    pm.first_name || ' ' || COALESCE(pm.middle_name || ' ', '') || COALESCE(pm.last_name, '') AS patientName,
                    pm.id AS patientId,
                    gt.gender_description AS gender,
                    CASE
                        WHEN pm.date_of_birth IS NOT NULL THEN
                            CAST(EXTRACT(YEAR FROM AGE(pm.date_of_birth)) AS INTEGER)
                        WHEN pm.age_given IS NOT NULL THEN CAST(pm.age_given AS INTEGER)
                        ELSE NULL
                    END AS age,
                    COALESCE(
                        CASE
                            WHEN pm.address_1 IS NOT NULL AND pm.city_id IS NOT NULL THEN
                                pm.address_1 || ', ' || COALESCE((SELECT ct.city_name FROM city_translations ct WHERE ct.city_id = pm.city_id AND (ct.language_id = 1 OR ct.language_id IS NULL) LIMIT 1), '')
                            WHEN pm.address_1 IS NOT NULL THEN pm.address_1
                            WHEN pm.city_id IS NOT NULL THEN COALESCE((SELECT ct.city_name FROM city_translations ct WHERE ct.city_id = pm.city_id AND (ct.language_id = 1 OR ct.language_id IS NULL) LIMIT 1), '')
                            ELSE ''
                        END, ''
                    ) AS address,
                    COALESCE(pm.mobile_1, '') AS contactNo
                FROM patient_master pm
                LEFT JOIN gender_translations gt ON gt.gender_id = pm.gender_id AND (gt.language_id = 1 OR gt.language_id IS NULL)
                WHERE pm.id = ?
                """;
        try {
            return jdbcTemplate.queryForMap(sql, patientId);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private DischargeCardDetailResponse.DischargeCardMainData mapToDischargeCardMainDataFromEntity(
            DischargeData dd, AdmissionData ad, Map<String, Object> patientMap) {

        DischargeCardDetailResponse.DischargeCardMainData mainData = new DischargeCardDetailResponse.DischargeCardMainData();

        // Patient Information
        mainData.setPatientName(getStringValue(patientMap, "patientname"));
        mainData.setPatientId(getStringValue(patientMap, "patientid"));
        mainData.setGender(getStringValue(patientMap, "gender"));
        Object ageObj = patientMap.get("age");
        if (ageObj != null) {
            if (ageObj instanceof Number) {
                mainData.setAge(((Number) ageObj).intValue());
            }
        }
        mainData.setAddress(getStringValue(patientMap, "address"));
        mainData.setContactNo(getStringValue(patientMap, "contactno"));

        // Discharge Card Information from Entity
        mainData.setIpdRefNo(dd.getIpdRefno());
        mainData.setAdmissionDate(dd.getAdmissionDate());
        mainData.setAdmissionTime(dd.getAdmissionTime());
        mainData.setTreatingDoctor(dd.getTreatingDoctor());
        mainData.setConsultingDoctor(dd.getConsultingDoctor());
        mainData.setDischargeDate(dd.getDischargeDate());
        mainData.setDischargeTime(dd.getDischargeTime());
        mainData.setWeight(dd.getWeight() != null ? BigDecimal.valueOf(dd.getWeight()) : null);
        mainData.setIpdNo(dd.getIpdNo());
        mainData.setDiagnosis(dd.getDiagnosis());
        mainData.setComplaints(dd.getComplaints());
        mainData.setHistory(dd.getHistory());
        mainData.setInvestigations(dd.getInvestigations());
        mainData.setOe(dd.getOe());
        mainData.setSe(dd.getSe());
        mainData.setProcedure(dd.getProcedure());
        mainData.setTreatment(dd.getTreatment());
        mainData.setDischarge(dd.getDischarge());
        mainData.setInstructions(dd.getInstructions());
        mainData.setKeyword(dd.getKeyword());
        mainData.setOperationStartDate(dd.getOperationStartDate());
        mainData.setOperationEndDate(dd.getOperationEndDate());
        mainData.setOperationStartTime(dd.getOperationStartTime());
        mainData.setOperationEndTime(dd.getOperationEndTime());
        mainData.setOperativeNotes(dd.getOperativeNotes());
        mainData.setRemark(dd.getRemark());
        mainData.setFollowUpComments(dd.getFollowUpComments());
        mainData.setAnesthesia(dd.getAnesthesia());
        mainData.setDoctorId(dd.getDoctorId());
        mainData.setReasonForDischarge(dd.getReasonForDischarge());
        // emergencyNumber not in discharge data, usually in doctor master
        // mainData.setEmergencyNumber(getStringValue(map, "emergencynumber"));

        if (ad != null) {
            mainData.setCompany(ad.getInsuranceDetails());
            mainData.setDepartment(ad.getDepartment());
        }

        mainData.setReferredDoctor(dd.getReferredDoctor());
        mainData.setConditionDischarge(dd.getConditionDischarge());
        mainData.setFooter(dd.getFooter());
        mainData.setPrintedOnDate(dd.getPrintedOnDate());
        mainData.setPrintedOnDateOp(dd.getPrintedOnDateOp());
        mainData.setRoom(dd.getRoom());
        mainData.setBedNo(dd.getBedNo());
        mainData.setAdmittedDays(dd.getAdmittedDays());
        mainData.setOtHours(dd.getOtHours());

        mainData.setFollowUpDate(dd.getFollowupDate());

        return mainData;
    }

    private List<DischargeCardDetailResponse.DischargeInvestigationDTO> getDischargeInvestigations(
            String patientId, String ipdNo, String doctorId, String clinicId) {
        String sql = """
                SELECT ipd_refno, attachment_path, id
                FROM discharge_investigations
                WHERE patient_id = ? AND ipd_refno = ?
                AND doctor_id = ? AND clinic_id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DischargeCardDetailResponse.DischargeInvestigationDTO dto = new DischargeCardDetailResponse.DischargeInvestigationDTO();
            dto.setIpdRefNo(rs.getString("ipd_refno"));
            dto.setAttachmentPath(rs.getString("attachment_path"));
            dto.setId(rs.getInt("id"));
            return dto;
        }, patientId, ipdNo);
    }

    private List<DischargeCardDetailResponse.DischargeInvoiceDetailDTO> getDischargeInvoiceDetails(
            String doctorId, String clinicId, String patientId, String ipdRefNo, String invoiceNo, Integer srNo) {
        if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT did.ipd_refno, did.invoice_no, did.description, did.unit_price,
                       did.quantity, did.doctor_id,
                       did.description || '*' || did.unit_price || '*' || did.quantity AS id
                FROM discharge_invoice_details did
                INNER JOIN ipd_invoice_medicinemaster inm ON inm.description = did.description
                WHERE did.patient_id = ? AND did.invoice_no = ?
                  AND COALESCE(did.delete_flag, false) = false
                ORDER BY inm.sortorder
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DischargeCardDetailResponse.DischargeInvoiceDetailDTO dto = new DischargeCardDetailResponse.DischargeInvoiceDetailDTO();
            dto.setIpdRefNo(rs.getString("ipd_refno"));
            dto.setInvoiceNo(rs.getString("invoice_no"));
            dto.setDescription(rs.getString("description"));
            dto.setUnitPrice(rs.getBigDecimal("unit_price"));
            dto.setQuantity(rs.getBigDecimal("quantity"));
            dto.setId(rs.getString("id"));
            dto.setDoctorId(rs.getString("doctor_id"));
            return dto;
        }, patientId, invoiceNo);
    }

    private DischargeCardDetailResponse.DischargeInvoiceHeaderDTO getDischargeInvoiceHeader(
            String patientId, String invoiceNo) {
        if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
            return null;
        }

        String sql = """
                SELECT ipd_refno, invoice_no, invoice_date, total_amount, collected_amount,
                       discount, balance, net_amount, comments, doctor_id
                FROM discharge_invoice_hdr
                WHERE patient_id = ? AND invoice_no = ?
                LIMIT 1
                """;

        List<DischargeCardDetailResponse.DischargeInvoiceHeaderDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            DischargeCardDetailResponse.DischargeInvoiceHeaderDTO dto = new DischargeCardDetailResponse.DischargeInvoiceHeaderDTO();
            dto.setIpdRefNo(rs.getString("ipd_refno"));
            dto.setInvoiceNo(rs.getString("invoice_no"));
            dto.setInvoiceDate(rs.getDate("invoice_date") != null ? rs.getDate("invoice_date").toLocalDate() : null);
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            dto.setCollectedAmount(rs.getBigDecimal("collected_amount"));
            dto.setDiscount(rs.getBigDecimal("discount"));
            dto.setBalance(rs.getBigDecimal("balance"));
            dto.setNetAmount(rs.getBigDecimal("net_amount"));
            dto.setComments(rs.getString("comments"));
            dto.setDoctorId(rs.getString("doctor_id"));
            return dto;
        }, patientId, invoiceNo);

        return results.isEmpty() ? null : results.get(0);
    }

    private List<DischargeCardDetailResponse.DischargeBillDetailDTO> getDischargeBillDetails(
            String patientId, String invoiceNo) {
        if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT '' AS id_charges, dbd.date_of_service AS hospital_bill_date,
                       dbd.date_of_service AS hdnlbl_hstpl_add_date,
                       dbd.ipd_refno, dbd.bill_no, dbd.charges_category, dbd.charges_subcategory,
                       dbd.comments, dbd.amount, dbd.doctor_id, dbd.total_amount,
                       dbd.no_of_units AS units, dbd.calculation_type,
                       dbd.charges_category || '*' || dbd.charges_subcategory || '*' || dbd.amount AS id
                FROM discharge_bill_details dbd
                INNER JOIN ipd_billdata_chargesmaster ibc
                    ON dbd.charges_category = ibc.charges_category
                    AND dbd.charges_subcategory = ibc.charges_subcategory
                WHERE dbd.patient_id = ? AND dbd.bill_no = ?
                  AND COALESCE(dbd.delete_flag, false) = false
                ORDER BY dbd.date_of_service ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DischargeCardDetailResponse.DischargeBillDetailDTO dto = new DischargeCardDetailResponse.DischargeBillDetailDTO();
            dto.setIdCharges(rs.getString("id_charges"));
            dto.setHospitalBillDate(
                    rs.getDate("hospital_bill_date") != null ? rs.getDate("hospital_bill_date").toLocalDate() : null);
            dto.setHdnlblHstplAddDate(
                    rs.getDate("hdnlbl_hstpl_add_date") != null ? rs.getDate("hdnlbl_hstpl_add_date").toLocalDate()
                            : null);
            dto.setIpdRefNo(rs.getString("ipd_refno"));
            dto.setBillNo(rs.getString("bill_no"));
            dto.setChargesCategory(rs.getString("charges_category"));
            dto.setChargesSubCategory(rs.getString("charges_subcategory"));
            dto.setComments(rs.getString("comments"));
            dto.setAmount(rs.getBigDecimal("amount"));
            dto.setId(rs.getString("id"));
            dto.setDoctorId(rs.getString("doctor_id"));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            dto.setUnits(rs.getBigDecimal("units"));
            dto.setCalculationType(rs.getString("calculation_type"));
            return dto;
        }, patientId, invoiceNo);
    }

    private DischargeCardDetailResponse.DischargeBillHeaderDTO getDischargeBillHeader(
            String patientId, String ipdNo, String invoiceNo) {
        if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
            return null;
        }

        String sql = """
                SELECT dbh.ipd_refno, dbh.bill_no, dbh.bill_date, dbh.adjust_advance,
                       dbh.total_amount, dbh.collected_amount, dbh.discount, dbh.balance,
                       dbh.net_amount, dbh.comments, dd.ipd_no,
                       COALESCE(dd.treating_doctor, '') AS treating_doctor,
                       COALESCE(dd.consulting_doctor, '') AS consulting_doctor,
                       dbh.doctor_id, ad.insurancedetails, dbh.tds
                FROM discharge_bill_hdr dbh
                INNER JOIN discharge_data dd ON dd.ipd_refno = dbh.ipd_refno
                LEFT JOIN admission_data ad ON ad.ipd_refno = dbh.ipd_refno
                WHERE dbh.patient_id = ? AND dbh.ipd_refno = ? AND dbh.bill_no = ?
                LIMIT 1
                """;

        List<DischargeCardDetailResponse.DischargeBillHeaderDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            DischargeCardDetailResponse.DischargeBillHeaderDTO dto = new DischargeCardDetailResponse.DischargeBillHeaderDTO();
            dto.setIpdRefNo(rs.getString("ipd_refno"));
            dto.setBillNo(rs.getString("bill_no"));
            dto.setBillDate(rs.getDate("bill_date") != null ? rs.getDate("bill_date").toLocalDate() : null);
            dto.setAdjustAdvance(rs.getBigDecimal("adjust_advance"));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            dto.setCollectedAmount(rs.getBigDecimal("collected_amount"));
            dto.setDiscount(rs.getBigDecimal("discount"));
            dto.setBalance(rs.getBigDecimal("balance"));
            dto.setNetAmount(rs.getBigDecimal("net_amount"));
            dto.setComments(rs.getString("comments"));
            dto.setIpdNo(rs.getString("ipd_no"));
            dto.setTreatingDoctor(rs.getString("treating_doctor"));
            dto.setConsultingDoctor(rs.getString("consulting_doctor"));
            dto.setDoctorId(rs.getString("doctor_id"));
            dto.setInsuranceDetails(rs.getString("insurancedetails"));
            dto.setTds(rs.getBigDecimal("tds"));
            return dto;
        }, patientId, ipdNo, invoiceNo);

        return results.isEmpty() ? null : results.get(0);
    }

    private DischargeCardDetailResponse.LabourCardDTO getLabourCard(String ipdNo) {
        String sql = """
                SELECT obstetric_history, date_of_delivery, time_of_delivery, labour_name,
                       operative_interference, indication, puerperium, child_sex,
                       wt_at_birth, wt_at_discharge, clinic_id, doctor_id, shift_id,
                       patient_id, ipd_ref_id,
                       date_of_delivery1, time_of_delivery1, child_sex1,
                       wt_at_birth1, wt_at_discharge1,
                       date_of_delivery2, time_of_delivery2, child_sex2,
                       wt_at_birth2, wt_at_discharge2, remark
                FROM ipd_labour_card
                WHERE ipd_ref_id = ?
                LIMIT 1
                """;

        List<DischargeCardDetailResponse.LabourCardDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            DischargeCardDetailResponse.LabourCardDTO dto = new DischargeCardDetailResponse.LabourCardDTO();
            dto.setObstetricHistory(rs.getString("obstetric_history"));
            dto.setDateOfDelivery(
                    rs.getDate("date_of_delivery") != null ? rs.getDate("date_of_delivery").toLocalDate() : null);
            dto.setTimeOfDelivery(
                    rs.getTime("time_of_delivery") != null ? rs.getTime("time_of_delivery").toLocalTime() : null);
            dto.setLabourName(rs.getString("labour_name"));
            dto.setOperativeInterference(rs.getString("operative_interference"));
            dto.setIndication(rs.getString("indication"));
            dto.setPuerperium(rs.getString("puerperium"));
            dto.setChildSex(rs.getString("child_sex"));
            dto.setWtAtBirth(rs.getBigDecimal("wt_at_birth"));
            dto.setWtAtDischarge(rs.getBigDecimal("wt_at_discharge"));
            dto.setClinicId(rs.getString("clinic_id"));
            dto.setDoctorId(rs.getString("doctor_id"));
            dto.setShiftId(rs.getInt("shift_id"));
            dto.setPatientId(rs.getString("patient_id"));
            dto.setIpdRefId(rs.getString("ipd_ref_id"));
            dto.setDateOfDelivery1(
                    rs.getDate("date_of_delivery1") != null ? rs.getDate("date_of_delivery1").toLocalDate() : null);
            dto.setTimeOfDelivery1(
                    rs.getTime("time_of_delivery1") != null ? rs.getTime("time_of_delivery1").toLocalTime() : null);
            dto.setChildSex1(rs.getString("child_sex1"));
            dto.setWtAtBirth1(rs.getBigDecimal("wt_at_birth1"));
            dto.setWtAtDischarge1(rs.getBigDecimal("wt_at_discharge1"));
            dto.setDateOfDelivery2(
                    rs.getDate("date_of_delivery2") != null ? rs.getDate("date_of_delivery2").toLocalDate() : null);
            dto.setTimeOfDelivery2(
                    rs.getTime("time_of_delivery2") != null ? rs.getTime("time_of_delivery2").toLocalTime() : null);
            dto.setChildSex2(rs.getString("child_sex2"));
            dto.setWtAtBirth2(rs.getBigDecimal("wt_at_birth2"));
            dto.setWtAtDischarge2(rs.getBigDecimal("wt_at_discharge2"));
            dto.setRemark(rs.getString("remark"));
            return dto;
        }, ipdNo);

        return results.isEmpty() ? null : results.get(0);
    }

    private BigDecimal getTotalAdvance(String ipdNo) {
        String sql = """
                SELECT COALESCE(SUM(amount_received), 0) AS amount_received
                FROM advance_collection_details
                WHERE ipd_refno = ?
                """;

        BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class, ipdNo);
        return result != null ? result : BigDecimal.ZERO;
    }

    private LocalDate getLastAdvanceDate(String ipdNo) {
        String sql = """
                SELECT MAX(advance_date) AS last_advance_date
                FROM advance_collection_details
                WHERE ipd_refno = ?
                """;

        try {
            Timestamp timestamp = jdbcTemplate.queryForObject(sql, Timestamp.class, ipdNo);
            return timestamp != null ? timestamp.toLocalDateTime().toLocalDate() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // Helper methods for saveDischargeCardDetails
    private boolean checkDischargeDateConflict(UpdateDischargeCardRequest request) {
        String sql = """
                SELECT COUNT(*) > 0
                FROM discharge_data
                WHERE (
                    (admission_date BETWEEN ? AND ? OR discharge_date BETWEEN ? AND ?)
                    OR (? BETWEEN admission_date AND discharge_date OR ? BETWEEN admission_date AND discharge_date)
                )
                AND patient_id = ? AND doctor_id = ? AND ipd_refno != ?
                """;

        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class,
                request.getAdmissionDate(), request.getDischargeDate(),
                request.getAdmissionDate(), request.getDischargeDate(),
                request.getAdmissionDate(), request.getDischargeDate(),
                request.getPatientId(), request.getDoctorId(),
                request.getIpdRefNo() != null ? request.getIpdRefNo() : "");

        return result != null && result;
    }

    private void mergeDischargeData(UpdateDischargeCardRequest request) {
        // Check if record exists
        String checkSql = """
                SELECT COUNT(*) > 0
                FROM discharge_data
                WHERE doctor_id = ? AND patient_id = ? AND ipd_refno = ?
                """;

        Boolean exists = jdbcTemplate.queryForObject(checkSql, Boolean.class,
                request.getDoctorId(), request.getPatientId(), request.getIpdRefNo());

        UpdateDischargeCardRequest.DischargeDetailDTO detail = request.getDischargeDetails() != null
                && !request.getDischargeDetails().isEmpty()
                        ? request.getDischargeDetails().get(0)
                        : null;

        if (exists != null && exists.equals(Boolean.TRUE)) {
            // UPDATE
            String updateSql = """
                    UPDATE discharge_data SET
                        admission_date = ?, admission_time = ?,
                        treating_doctor = ?, consulting_doctor = ?,
                        discharge_date = ?, discharge_time = ?,
                        weight = ?, ipd_no = ?, keyword = ?,
                        diagnosis = ?, complaints = ?, history = ?,
                        investigations = ?, oe = ?, se = ?,
                        procedure = ?, treatment = ?, discharge = ?,
                        instructions = ?, modified_on = CURRENT_TIMESTAMP,
                        modifiedby_name = ?,
                        operation_start_date = ?, operation_end_date = ?,
                        operation_start_time = ?, operation_end_time = ?,
                        operative_notes = ?, remark = ?,
                        follow_up_comments = ?, anesthesia = ?,
                        reasonfordischarge = ?, referred_doctor = ?,
                        condition_discharge = ?, footer = ?,
                        printed_on_date = ?, bedno = ?, room = ?,
                        admitted_days = ?, ot_hours = ?, company = ?,
                        followup_date = ?
                    WHERE doctor_id = ? AND patient_id = ? AND ipd_refno = ?
                    """;

            jdbcTemplate.update(updateSql,
                    request.getAdmissionDate(), request.getAdmissionTime(),
                    request.getTreatingDoctor(), request.getConsultingDoctor(),
                    request.getDischargeDate(), request.getDischargeTime(),
                    request.getWeight(), request.getIpdNo(), request.getKeyword(),
                    detail != null ? detail.getDiagnosis() : null,
                    detail != null ? detail.getComplaints() : null,
                    detail != null ? detail.getHistory() : null,
                    detail != null ? detail.getInvestigation() : null,
                    detail != null ? detail.getOe() : null,
                    detail != null ? detail.getSe() : null,
                    detail != null ? detail.getProcedure() : null,
                    detail != null ? detail.getTreatment() : null,
                    detail != null ? detail.getDischarge() : null,
                    detail != null ? detail.getInstruction() : null,
                    request.getUserId(),
                    request.getOperationStartDate(), request.getOperationEndDate(),
                    request.getOperationStartTime(), request.getOperationEndTime(),
                    request.getOperativeNotes(), request.getRemark(),
                    request.getFollowUpComments(), request.getAnesthesia(),
                    request.getReasonForDischarge(), request.getReferredDoctor(),
                    request.getConditionOnDischarge(), request.getFooter(),
                    request.getDefaultDate(), request.getWard(), request.getRoom(),
                    request.getAdmittedDays(), request.getOtHours(), request.getCompany(),
                    request.getFollowUpDate(),
                    request.getDoctorId(), request.getPatientId(), request.getIpdRefNo());
        } else {
            throw new RuntimeException("Discharge card not found for update. PatientId: " + request.getPatientId()
                    + ", IPD: " + request.getIpdRefNo());
        }

        // Update is_printed flag
        String updatePrintedSql = """
                UPDATE discharge_data
                SET is_printed = false, modified_on = CURRENT_TIMESTAMP, modifiedby_name = ?
                WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND ipd_refno = ?
                """;
        jdbcTemplate.update(updatePrintedSql, request.getUserId(), request.getPatientId(), request.getDoctorId(),
                request.getClinicId(), request.getIpdRefNo());
    }

    private void insertDischargeInvestigations(UpdateDischargeCardRequest request) {
        String sql = """
                INSERT INTO discharge_investigations (
                    doctor_id, clinic_id, patient_id, ipd_refno, attachment_path,
                    created_on, createdby_name, modified_on, modifiedby_name
                ) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)
                """;

        for (String attachmentPath : request.getKeywordAttachments()) {
            jdbcTemplate.update(sql,
                    request.getDoctorId(), request.getClinicId(),
                    request.getPatientId(), request.getIpdRefNo(), attachmentPath,
                    request.getUserId(), request.getUserId());
        }
    }

    private void updateAdmissionData(UpdateDischargeCardRequest request) {
        String sql = """
                UPDATE admission_data SET
                    treatingdoctor = ?, consultantdoctor = ?, ipdfileno = ?,
                    referred_doctor = ?, insurancedetails = ?,
                    modified_on = CURRENT_TIMESTAMP, modifiedby_name = ?
                WHERE patient_id = ? AND doctor_id = ? AND clinic_id = ? AND ipd_refno = ?
                """;

        jdbcTemplate.update(sql,
                request.getTreatingDoctor(), request.getConsultingDoctor(),
                request.getIpdNo(), request.getReferredDoctor(), request.getCompany(),
                request.getUserId(),
                request.getPatientId(), request.getDoctorId(),
                request.getClinicId(), request.getIpdRefNo());
    }

    // Additional helper methods
    private LocalDate getLocalDateValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof LocalDate)
            return (LocalDate) value;
        if (value instanceof java.sql.Date)
            return ((java.sql.Date) value).toLocalDate();
        if (value instanceof Timestamp)
            return ((Timestamp) value).toLocalDateTime().toLocalDate();
        return null;
    }

    private LocalTime getLocalTimeValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof LocalTime)
            return (LocalTime) value;
        if (value instanceof java.sql.Time)
            return ((java.sql.Time) value).toLocalTime();
        return null;
    }
}
