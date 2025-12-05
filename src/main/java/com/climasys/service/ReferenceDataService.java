package com.climasys.service;

import com.climasys.entity.*;
import com.climasys.auth.entity.AuthDoctorMaster;
import com.climasys.repository.*;
import com.climasys.auth.repository.ClinicMasterRepository;
import com.climasys.auth.repository.AuthDoctorMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ReferenceDataService {

    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    private GenderTranslationRepository genderTranslationRepository;

    @Autowired
    private BloodGroupRepository bloodGroupRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private AreaTranslationRepository areaTranslationRepository;

    @Autowired
    private OccupationRepository occupationRepository;

    @Autowired
    private MaritalStatusRepository maritalStatusRepository;

    @Autowired
    private MaritalStatusTranslationRepository maritalStatusTranslationRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityTranslationRepository cityTranslationRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private StateTranslationRepository stateTranslationRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryTranslationRepository countryTranslationRepository;

    @Autowired
    private ImpressionFindingRepository impressionFindingRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private DoctorClinicShiftRepository doctorClinicShiftRepository;

    @Autowired
    private ClinicMasterRepository clinicMasterRepository;

    @Autowired
    private AuthDoctorMasterRepository doctorMasterRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private TitleMasterRepository titleMasterRepository;

    @Autowired
    private FollowupAfterRepository followupAfterRepository;

    @Autowired
    private FollowUpTypeRepository followUpTypeRepository;

    public List<Gender> getGenders() {
        return genderRepository.findAllOrdered();
    }

    public List<BloodGroup> getBloodGroups() {
        return bloodGroupRepository.findAllOrdered();
    }

    public List<ImpressionFinding> getImpressions(String doctorId, String clinicId) {
        // Note: ImpressionFinding entity doesn't have clinic_id field yet
        // For now, we'll ignore clinicId and use the existing logic
        // TODO: Add clinic_id support to ImpressionFinding entity when needed
        if (doctorId != null && !doctorId.isEmpty()) {
            return impressionFindingRepository.findByDoctorId(doctorId);
        }
        return impressionFindingRepository.findAllOrdered();
    }

    public List<ImpressionFinding> getImpressions(String doctorId) {
        if (doctorId != null && !doctorId.isEmpty()) {
            return impressionFindingRepository.findByDoctorId(doctorId);
        }
        return impressionFindingRepository.findAllOrdered();
    }

    public Map<String, Object> getAreaName(Integer areaId) {
        Map<String, Object> result = new HashMap<>();
        Optional<Area> area = areaRepository.findById(new AreaId(areaId, null, null, null));
        if (area.isPresent()) {
            result.put("areaId", areaId);
            result.put("areaName", "Area " + areaId); // You might want to get from translation table
            return result;
        }
        result.put("error", "Area not found");
        return result;
    }

    public List<Area> searchAreas(String query) {
        return areaRepository.searchAreas(query);
    }

    public Map<String, Object> getAreaDetails(String areaName, Integer languageId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Find area translation by area name and language ID
            AreaTranslation areaTranslation = areaTranslationRepository.findByAreaNameAndLanguageId(areaName,
                    languageId);

            if (areaTranslation == null) {
                result.put("error", "Area not found for name: " + areaName + " and language: " + languageId);
                return result;
            }

            // Get area master details
            Area area = areaRepository.findByIdAndActive(areaTranslation.getId().getAreaId());
            if (area == null) {
                result.put("error", "Area master not found for ID: " + areaTranslation.getId().getAreaId());
                return result;
            }

            // Get city translation
            CityTranslation cityTranslation = cityTranslationRepository.findByCityIdAndLanguageId(
                    area.getCityId(), languageId);

            // Get state translation
            StateTranslation stateTranslation = stateTranslationRepository.findByStateIdAndLanguageId(
                    area.getStateId(), languageId);

            // Build result matching stored procedure output
            result.put("id", area.getId());
            result.put("cityId", area.getCityId());
            result.put("stateId", area.getStateId());
            result.put("countryId", area.getCountryId());
            result.put("languageId", languageId);
            result.put("cityName", cityTranslation != null ? cityTranslation.getCityName() : null);
            result.put("stateName", stateTranslation != null ? stateTranslation.getStateName() : null);

        } catch (Exception e) {
            result.put("error", "Error retrieving area details: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> searchAreasAdvanced(String searchStr, Integer languageId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Result Set 1: Basic Area Search
            List<AreaTranslation> areaTranslations = areaTranslationRepository
                    .searchAreasByLanguageAndName(searchStr, languageId);

            List<Map<String, Object>> basicAreaResults = new ArrayList<>();
            for (AreaTranslation at : areaTranslations) {
                Map<String, Object> areaResult = new HashMap<>();
                areaResult.put("areaName", at.getAreaName());
                areaResult.put("areaId", at.getId().getAreaId());
                areaResult.put("cityId", at.getId().getCityId());
                areaResult.put("stateId", at.getId().getStateId());
                basicAreaResults.add(areaResult);
            }
            result.put("basicAreaSearch", basicAreaResults);

            // Result Set 2: Area Search with Patient Count
            List<Map<String, Object>> areaWithPatientCountResults = new ArrayList<>();
            for (AreaTranslation at : areaTranslations) {
                Long patientCount = patientRepository.countByAreaId(at.getId().getAreaId());
                String searchValue = (at.getAreaName() != null ? at.getAreaName() : "") +
                        "   :   " + (patientCount != null ? patientCount.toString() : "0");

                Map<String, Object> areaWithCount = new HashMap<>();
                areaWithCount.put("searchValue", searchValue);
                areaWithCount.put("areaName", at.getAreaName());
                areaWithCount.put("patientCount", patientCount);
                areaWithPatientCountResults.add(areaWithCount);
            }
            result.put("areaWithPatientCount", areaWithPatientCountResults);

            // Result Set 3: Lab Test Search (Note: Lab test entities not available in
            // current codebase)
            // This would require Lab_Test_Master and Patient_Visit_LabTestAsked entities
            List<Map<String, Object>> labTestResults = new ArrayList<>();
            // TODO: Implement when lab test entities are available
            result.put("labTestSearch", labTestResults);

        } catch (Exception e) {
            result.put("error", "Error in advanced area search: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> checkFolderNumber(String folderNo) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = patientRepository.existsByFolderNo(folderNo);
        result.put("exists", exists);
        result.put("folderNo", folderNo);
        return result;
    }

    public List<DoctorClinicShift> getClinicShifts(String clinicId, String doctorId, String day) {
        if (doctorId != null && !doctorId.isEmpty() && day != null && !day.isEmpty()) {
            return doctorClinicShiftRepository.findByClinicIdAndDoctorIdAndDay(clinicId, doctorId, day);
        } else if (doctorId != null && !doctorId.isEmpty()) {
            return doctorClinicShiftRepository.findByClinicIdAndDoctorId(clinicId, doctorId);
        } else {
            return doctorClinicShiftRepository.findByClinicId(clinicId);
        }
    }

    public List<AuthDoctorMaster> getDoctors(String clinicId) {
        if (clinicId != null && !clinicId.isEmpty()) {
            // You might need to create a method in DoctorMasterRepository to find by clinic
            return doctorMasterRepository.findAll();
        }
        return doctorMasterRepository.findAll();
    }

    public List<Clinic> getClinics() {
        return clinicMasterRepository.findAll();
    }

    public List<Occupation> getOccupations() {
        return occupationRepository.findAllOrdered();
    }

    public List<MaritalStatus> getMaritalStatuses() {
        return maritalStatusRepository.findAllOrdered();
    }

    public List<Map<String, Object>> getMaritalStatusesWithTranslations(Integer languageId) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            if (languageId != null) {
                // Get translations directly for the specified language
                List<MaritalStatusTranslation> translations = maritalStatusTranslationRepository
                        .findByLanguageId(languageId);

                for (MaritalStatusTranslation translation : translations) {
                    Map<String, Object> statusMap = new HashMap<>();
                    statusMap.put("id", translation.getId().getMaritalStatusId());
                    statusMap.put("description", translation.getMaritalStatusDescription());
                    result.add(statusMap);
                }
            } else {
                // Fallback to basic marital statuses without translations
                List<MaritalStatus> maritalStatuses = maritalStatusRepository.findAllOrdered();

                for (MaritalStatus maritalStatus : maritalStatuses) {
                    Map<String, Object> statusMap = new HashMap<>();
                    statusMap.put("id", maritalStatus.getId());
                    statusMap.put("description", maritalStatus.getId()); // Use ID as description
                    result.add(statusMap);
                }
            }

        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error getting marital statuses with translations: " + e.getMessage());
        }

        return result;
    }

    public List<CityTranslation> getCities(String stateId) {
        if (stateId != null && !stateId.isEmpty()) {
            return cityTranslationRepository.findByStateId(stateId);
        }
        return cityTranslationRepository.findAllOrdered();
    }

    public List<Map<String, Object>> searchCities(String searchStr, Integer languageId) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // Find city translations by search string and language ID
            List<CityTranslation> cityTranslations = cityTranslationRepository
                    .searchCitiesByLanguageAndName(searchStr, languageId);

            for (CityTranslation ct : cityTranslations) {
                Map<String, Object> cityResult = new HashMap<>();
                cityResult.put("cityName", ct.getCityName());
                cityResult.put("cityId", ct.getId().getCityId());
                cityResult.put("stateId", ct.getId().getStateId());
                result.add(cityResult);
            }

        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "Error searching cities: " + e.getMessage());
            result.add(errorResult);
        }

        return result;
    }

    public List<StateTranslation> getStates(String countryId, Integer languageId) {
        List<StateTranslation> stateTranslations;
        if (countryId != null && !countryId.isEmpty()) {
            stateTranslations = stateTranslationRepository.findByCountryIdAndLanguageId(countryId, languageId);
        } else {
            stateTranslations = stateTranslationRepository.findByLanguageId(languageId);
        }
        return stateTranslations;
    }

    public List<CountryTranslation> getCountries() {
        return countryTranslationRepository.findAll();
    }

    public List<AreaTranslation> getAreas(String cityId, String stateId, Integer languageId) {
        if (languageId != null) {
            return areaTranslationRepository.findByCityIdAndStateIdAndLanguageId(cityId, stateId, languageId);
        } else {
            return areaTranslationRepository.findByCityIdAndStateId(cityId, stateId);
        }
    }

    // =====================================================
    // NEW METHODS FOR USP_Get_BloodGroupDetails REPLACEMENT
    // =====================================================

    /**
     * Get all payment methods
     * 
     * @return List of payment methods
     */
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethodRepository.findAllActiveOrdered();
    }

    /**
     * Get all titles
     * 
     * @return List of titles
     */
    public List<TitleMaster> getTitles() {
        return titleMasterRepository.findAllActiveOrdered();
    }

    /**
     * Get all follow-up types
     * 
     * @return List of follow-up types
     */
    public List<FollowUpType> getFollowUpTypes() {
        return followUpTypeRepository.findAllActive();
    }

    /**
     * Get all follow-up after periods
     * 
     * @return List of follow-up after periods
     */
    public List<FollowupAfter> getFollowupAfterOptions() {
        return followupAfterRepository.findAllActiveOrdered();
    }

    /**
     * Get all reference data in the same format as USP_Get_BloodGroupDetails
     * This method combines all the data that the stored procedure returns
     * 
     * @return Map containing all reference data tables
     */
    public Map<String, Object> getAllReferenceData() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Table[0]: Blood Groups
            result.put("bloodGroups", getBloodGroups());

            // Table[1]: Payment Methods
            result.put("paymentMethods", getPaymentMethods());

            // Table[2]: Titles
            result.put("titles", getTitles());

            // Table[3]: Follow-up Types
            result.put("followUpTypes", getFollowUpTypes());

            // Table[4]: Follow-up After Periods
            result.put("followupAfterOptions", getFollowupAfterOptions());

            result.put("success", true);
            result.put("message", "All reference data retrieved successfully");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to retrieve reference data: " + e.getMessage());
        }

        return result;
    }
}
