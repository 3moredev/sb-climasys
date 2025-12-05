package com.climasys.billing.service;

import com.climasys.billing.dto.OPDDailyCollectionDTO;
import com.climasys.billing.repository.OPDDailyCollectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for OPD Daily Collection operations
 * Implements JPA-based queries instead of stored procedures
 */
@Service
public class OPDDailyCollectionService {
    
    private final OPDDailyCollectionRepository repository;
    
    public OPDDailyCollectionService(OPDDailyCollectionRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Get OPD Daily Collection data using JPA queries
     * 
     * @param fromDate Start date for collection period
     * @param toDate End date for collection period
     * @param clinicId Clinic ID filter
     * @param doctorId Doctor ID filter (can be "All" or "0" for all doctors)
     * @param roleId Role ID filter
     * @param languageId Language ID for translations
     * @return List of OPDDailyCollectionDTO objects
     */
    @Transactional(readOnly = true)
    public List<OPDDailyCollectionDTO> getOPDDailyCollection(
            LocalDate fromDate,
            LocalDate toDate,
            String clinicId,
            String doctorId,
            Integer roleId,
            Integer languageId
    ) {
        // Handle "All" or "0" doctor ID
        String actualDoctorId = (doctorId == null || doctorId.equals("All") || doctorId.equals("0")) 
                ? null : doctorId;
        
        // Get visits data
        List<Object[]> visits = repository.findOPDDailyCollectionVisits(
                fromDate, toDate, clinicId, actualDoctorId, roleId, languageId
        );
        
        // Get adhoc payments data
        List<Object[]> adhocPayments = repository.findOPDDailyCollectionAdhocPayments(
                fromDate, toDate, clinicId, actualDoctorId, languageId
        );
        
        // Combine results
        List<Object[]> allResults = new ArrayList<>();
        allResults.addAll(visits);
        allResults.addAll(adhocPayments);
        
        // Map results to DTOs
        return mapToDTOs(allResults);
    }
    
    /**
     * Map query result set to DTOs
     * 
     * @param results Raw result set from JPA queries
     * @return List of OPDDailyCollectionDTO objects
     */
    private List<OPDDailyCollectionDTO> mapToDTOs(List<Object[]> results) {
        List<OPDDailyCollectionDTO> dtos = new ArrayList<>();
        
        for (Object[] row : results) {
            OPDDailyCollectionDTO dto = new OPDDailyCollectionDTO();
            
            int index = 0;
            
            // Map columns based on query result set order
            // visitDate, name, patientId, statusDescription, statusId,
            // feesToCollect, feesCollected, adhocFees, originalBilledAmount, folderNo,
            // comment, difference, dues, originalDiscount, discount, net,
            // inPerson, attendedBy, paymentById, paymentRemark, paymentDescription,
            // partialName, ageYearsIntRound, genderDescription, patientVisitNo,
            // doctorId, doctorName, isFollowUp, baseLocation
            
            if (row.length > index && row[index] != null) dto.setVisitDate(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setName(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setPatientId(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setStatusDescription(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setStatusId(convertToShort(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setFeesToCollect(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setFeesCollected(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setAdhocFees(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setOriginalBilledAmount(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) dto.setFolderNo(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setComment(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setDifference(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setDues(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setOriginalDiscount(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setDiscount(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setNet(convertToBigDecimal(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setInPerson(convertToBoolean(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) dto.setAttendedBy(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setPaymentById(convertToShort(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) dto.setPaymentRemark(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setPaymentDescription(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setPartialName(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setAgeYearsIntRound(convertToInteger(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) dto.setGenderDescription(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) {
                dto.setPatientVisitNo(convertToInteger(row[index]));
            }
            index++;
            
            if (row.length > index && row[index] != null) dto.setDoctorId(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setDoctorName(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setIsFollowUp(row[index].toString());
            index++;
            
            if (row.length > index && row[index] != null) dto.setBaseLocation(row[index].toString());
            
            dtos.add(dto);
        }
        
        return dtos;
    }
    
    /**
     * Helper method to convert Object to BigDecimal
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Helper method to convert Object to Short
     */
    private Short convertToShort(Object value) {
        if (value == null) return null;
        if (value instanceof Short) return (Short) value;
        if (value instanceof Number) return ((Number) value).shortValue();
        try {
            return Short.parseShort(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Helper method to convert Object to Integer
     */
    private Integer convertToInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Helper method to convert Object to Boolean
     */
    private Boolean convertToBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        return Boolean.parseBoolean(value.toString());
    }
}

