package com.climasys.medicine.service;

import com.climasys.entity.MedicineMaster;
import com.climasys.entity.DiagnosisMaster;
import com.climasys.repository.MedicineMasterRepository;
import com.climasys.repository.DiagnosisMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Service for medicine master data management using JPA
 */
@Service
public class MedicineMasterDataService {

    @Autowired
    private MedicineMasterRepository medicineMasterRepository;
    
    @Autowired
    private DiagnosisMasterRepository diagnosisMasterRepository;

    /**
     * Get active medicines for a doctor and clinic
     */
    public List<Map<String, Object>> getActiveMedicinesByDoctorAndClinic(String doctorId, String clinicId) {
        try {
            List<MedicineMaster> medicines = medicineMasterRepository.findByDoctorIdAndClinicIdAndActiveOrderByPriorityValueAscShortDescriptionAsc(doctorId, clinicId, true);
            return convertToMapList(medicines);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get active medicines: " + e.getMessage(), e);
        }
    }

    /**
     * Get active medicines for a doctor (backward compatibility)
     */
    public List<Map<String, Object>> getActiveMedicines(String doctorId) {
        try {
            List<MedicineMaster> medicines = medicineMasterRepository.findByClinicIdOrderByPriorityValueAscShortDescriptionAsc("DEFAULT");
            return convertToMapList(medicines);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get active medicines: " + e.getMessage(), e);
        }
    }

    /**
     * Get active prescriptions for a doctor
     */
    public List<Map<String, Object>> getActivePrescriptions(String doctorId) {
        try {
            // For now, return empty list as prescription logic needs to be implemented
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get active prescriptions: " + e.getMessage(), e);
        }
    }

    /**
     * Get BLD (Before Last Date) medicine data
     */
    public List<Map<String, Object>> getBLDMedicineData(String patientId, String visitId) {
        try {
            // For now, return empty list as BLD logic needs to be implemented
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get BLD medicine data: " + e.getMessage(), e);
        }
    }

    /**
     * Get BLD (Before Last Date) prescription data
     */
    public List<Map<String, Object>> getBLDPrescriptionData(String patientId, String visitId) {
        try {
            // For now, return empty list as BLD logic needs to be implemented
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get BLD prescription data: " + e.getMessage(), e);
        }
    }

    /**
     * Get medicine categories for a doctor
     */
    public List<Map<String, Object>> getMedicineCategories(String doctorId) {
        try {
            // For now, return empty list as categories logic needs to be implemented
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get medicine categories: " + e.getMessage(), e);
        }
    }

    /**
     * Get disease master data for doctor and clinic
     */
    public List<Map<String, Object>> getDiseaseMasterDataByDoctorAndClinic(String doctorId, String clinicId) {
        try {
            List<DiagnosisMaster> diagnoses = diagnosisMasterRepository.findByDoctorIdAndClinicIdOrderByPriorityValueAscShortDescriptionAsc(doctorId, clinicId);
            return convertDiagnosisToMapList(diagnoses);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get disease master data: " + e.getMessage(), e);
        }
    }

    /**
     * Get disease master data (backward compatibility)
     */
    public List<Map<String, Object>> getDiseaseMasterData(String doctorId) {
        try {
            List<DiagnosisMaster> diagnoses = diagnosisMasterRepository.findByDoctorIdOrderByPriorityValueAscShortDescriptionAsc(doctorId);
            return convertDiagnosisToMapList(diagnoses);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get disease master data: " + e.getMessage(), e);
        }
    }

    /**
     * Get findings master data
     */
    public List<Map<String, Object>> getFindingsMasterData(String doctorId) {
        try {
            // For now, return empty list as findings logic needs to be implemented
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get findings master data: " + e.getMessage(), e);
        }
    }

    /**
     * Get keyword master data
     */
    public List<Map<String, Object>> getKeywordMasterData(String doctorId) {
        try {
            // For now, return empty list as keywords logic needs to be implemented
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get keyword master data: " + e.getMessage(), e);
        }
    }

    /**
     * Get keyword master data for hospital
     */
    public List<Map<String, Object>> getKeywordMasterDataForHospital(String doctorId) {
        try {
            // For now, return empty list as hospital keywords logic needs to be implemented
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get keyword master data for hospital: " + e.getMessage(), e);
        }
    }

    /**
     * Convert MedicineMaster entities to Map list
     */
    private List<Map<String, Object>> convertToMapList(List<MedicineMaster> medicines) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (MedicineMaster medicine : medicines) {
            Map<String, Object> map = new HashMap<>();
            map.put("short_description", medicine.getShortDescription());
            map.put("doctor_id", medicine.getDoctorId());
            map.put("medicine_description", medicine.getMedicineDescription());
            map.put("clinic_id", medicine.getClinicId());
            map.put("active", medicine.getActive());
            map.put("priority_value", medicine.getPriorityValue());
            map.put("morning", medicine.getMorning());
            map.put("afternoon", medicine.getAfternoon());
            map.put("night", medicine.getNight());
            map.put("no_of_days", medicine.getNoOfDays());
            map.put("instruction", medicine.getInstruction());
            map.put("created_on", medicine.getCreatedOn());
            map.put("modified_on", medicine.getModifiedOn());
            result.add(map);
        }
        return result;
    }

    /**
     * Convert DiagnosisMaster entities to Map list
     */
    private List<Map<String, Object>> convertDiagnosisToMapList(List<DiagnosisMaster> diagnoses) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (DiagnosisMaster diagnosis : diagnoses) {
            Map<String, Object> map = new HashMap<>();
            map.put("short_description", diagnosis.getShortDescription());
            map.put("diagnosis_description", diagnosis.getDiagnosisDescription());
            map.put("doctor_id", diagnosis.getDoctorId());
            map.put("clinic_id", diagnosis.getClinicId());
            map.put("priority_value", diagnosis.getPriorityValue());
            map.put("created_on", diagnosis.getCreatedOn());
            map.put("modified_on", diagnosis.getModifiedOn());
            result.add(map);
        }
        return result;
    }
}