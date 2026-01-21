package com.climasys.service;

import com.climasys.entity.*;
import com.climasys.auth.entity.AuthDoctorMaster;
import com.climasys.repository.*;
import com.climasys.auth.repository.ClinicMasterRepository;
import com.climasys.auth.repository.AuthDoctorMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private JdbcTemplate jdbcTemplate;

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
            List<AreaTranslation> areaTranslations = areaTranslationRepository.findByAreaNameAndLanguageId(areaName,
                    languageId);

            if (areaTranslations == null || areaTranslations.isEmpty()) {
                result.put("error", "Area not found for name: " + areaName + " and language: " + languageId);
                return result;
            }

            // If multiple found, we just take the first one for now as this endpoint
            // doesn't support disambiguation
            AreaTranslation areaTranslation = areaTranslations.get(0);

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

    public Map<String, Object> getAreaDetailsById(Integer areaId, Integer languageId) {
        Map<String, Object> result = new HashMap<>();

        try {
            AreaTranslation at = areaTranslationRepository.findByAreaIdAndLanguageId(areaId, languageId);
            if (at == null) {
                result.put("error", "Area not found for id: " + areaId + " and language: " + languageId);
                return result;
            }

            Area area = areaRepository.findById(new AreaId(
                    at.getId().getAreaId(),
                    at.getId().getCityId(),
                    at.getId().getStateId(),
                    at.getId().getCountryId())).orElse(null);

            if (area == null) {
                result.put("error", "Area master not found for ID: " + areaId);
                return result;
            }

            result.put("areaId", area.getId());
            result.put("areaName", at.getAreaName());
            result.put("cityId", area.getCityId());
            result.put("stateId", area.getStateId());
            result.put("countryId", area.getCountryId());
            result.put("languageId", languageId);
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

    public List<City> getCities(String stateId) {
        if (stateId != null && !stateId.isEmpty()) {
            return cityRepository.findByStateId(stateId);
        }
        return cityRepository.findAllOrdered();
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

    public List<State> getStates(String countryId) {
        if (countryId != null && !countryId.isEmpty()) {
            return stateRepository.findByCountryId(countryId);
        }
        return stateRepository.findAllOrdered();
    }

    public List<Map<String, Object>> getStatesWithTranslations(String countryId, Integer languageId) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // Get all states
            List<State> states = getStates(countryId);

            // For each state, get its translation
            for (State state : states) {
                Map<String, Object> stateResult = new HashMap<>();
                stateResult.put("id", state.getId());

                // Get state translation
                StateTranslation stateTranslation = stateTranslationRepository.findByStateIdAndLanguageId(
                        state.getId().getId(), languageId);

                if (stateTranslation != null) {
                    stateResult.put("stateName", stateTranslation.getStateName());
                } else {
                    stateResult.put("stateName", null);
                }

                result.add(stateResult);
            }
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "Error getting states with translations: " + e.getMessage());
            result.add(errorResult);
        }

        return result;
    }

    public List<Country> getCountries() {
        return countryRepository.findAllOrdered();
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

    @Transactional
    public Map<String, Object> createArea(String areaName, String cityId, String stateId, String countryId,
            Integer languageId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Ensure referenced country exists; if not, create a minimal one to satisfy
            // foreign key constraints
            try {
                Integer countryExists = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM country_master WHERE id = ?",
                        Integer.class, countryId);

                if (countryExists == null || countryExists == 0) {
                    // Insert country_master row (minimal)
                    jdbcTemplate.update(
                            "INSERT INTO country_master (id) VALUES (?) " +
                                    "ON CONFLICT (id) DO NOTHING",
                            countryId);

                    // Insert translation with a fallback name = countryId
                    jdbcTemplate.update(
                            "INSERT INTO country_translations (country_id, language_id, country_name) VALUES (?, ?, ?) "
                                    +
                                    "ON CONFLICT (country_id, language_id) DO NOTHING",
                            countryId, languageId, countryId);
                }
            } catch (Exception e) {
                System.err.println("Error checking/creating country: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // Ensure referenced state exists; if not, create a minimal one to satisfy
            // foreign key constraints
            try {
                Integer stateExists = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM state_master WHERE id = ? AND country_id = ?",
                        Integer.class, stateId, countryId);

                if (stateExists == null || stateExists == 0) {
                    // Insert state_master row (minimal)
                    jdbcTemplate.update(
                            "INSERT INTO state_master (id, country_id) VALUES (?, ?) " +
                                    "ON CONFLICT (id, country_id) DO NOTHING",
                            stateId, countryId);

                    // Insert translation with a fallback name = stateId
                    jdbcTemplate.update(
                            "INSERT INTO state_translations (state_id, country_id, language_id, state_name) VALUES (?, ?, ?, ?) "
                                    +
                                    "ON CONFLICT (state_id, country_id, language_id) DO NOTHING",
                            stateId, countryId, languageId, stateId);
                }
            } catch (Exception e) {
                System.err.println("Error checking/creating state: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // Ensure referenced city exists; if not, create a minimal one so area insert
            // won't fail.
            try {
                Integer cityExists = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM city_master WHERE id = ? AND state_id = ? AND country_id = ?",
                        Integer.class, cityId, stateId, countryId);

                if (cityExists == null || cityExists == 0) {
                    // Insert city_master row (minimal). Postgres requires conflict target.
                    jdbcTemplate.update(
                            "INSERT INTO city_master (id, state_id, country_id) VALUES (?, ?, ?) " +
                                    "ON CONFLICT (id, state_id, country_id) DO NOTHING",
                            cityId, stateId, countryId);

                    // Insert translation with a fallback name = cityId (can be updated later via
                    // UI/Admin)
                    jdbcTemplate.update(
                            "INSERT INTO city_translations (city_id, state_id, country_id, language_id, city_name) VALUES (?, ?, ?, ?, ?) "
                                    +
                                    "ON CONFLICT (city_id, state_id, country_id, language_id) DO NOTHING",
                            cityId, stateId, countryId, languageId, cityId);
                }
            } catch (Exception e) {
                System.err.println("Error checking/creating city: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // Check if area already exists with the same name, city, state, country
            List<AreaTranslation> existingTranslations = areaTranslationRepository.findByAreaNameAndLanguageId(areaName,
                    languageId);

            if (existingTranslations != null && !existingTranslations.isEmpty()) {
                for (AreaTranslation existingTranslation : existingTranslations) {
                    // Check if it matches the same city/state/country
                    Area existingArea = areaRepository.findById(new AreaId(
                            existingTranslation.getId().getAreaId(),
                            existingTranslation.getId().getCityId(),
                            existingTranslation.getId().getStateId(),
                            existingTranslation.getId().getCountryId())).orElse(null);

                    if (existingArea != null &&
                            existingArea.getCityId().equals(cityId) &&
                            existingArea.getStateId().equals(stateId) &&
                            existingArea.getCountryId().equals(countryId)) {
                        // Area already exists, return it
                        result.put("success", true);
                        result.put("areaId", existingArea.getId());
                        result.put("message", "Area already exists");
                        return result;
                    }
                }
            }

            // Get next area ID - find max ID for the given city/state/country combination
            String maxIdSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM area_master WHERE city_id = ? AND state_id = ? AND country_id = ?";
            Integer nextAreaId = jdbcTemplate.queryForObject(maxIdSql, Integer.class, cityId, stateId, countryId);

            if (nextAreaId == null) {
                nextAreaId = 1; // Fallback to 1 if no areas exist
            }

            // Create Area entity
            Area newArea = new Area();
            newArea.setId(nextAreaId);
            newArea.setCityId(cityId);
            newArea.setStateId(stateId);
            newArea.setCountryId(countryId);
            newArea.setIsActivate(true);

            // Save Area
            areaRepository.save(newArea);

            // Create AreaTranslation entity
            AreaTranslationId translationId = new AreaTranslationId();
            translationId.setAreaId(nextAreaId);
            translationId.setCityId(cityId);
            translationId.setStateId(stateId);
            translationId.setCountryId(countryId);
            translationId.setLanguageId(languageId);

            AreaTranslation newTranslation = new AreaTranslation();
            newTranslation.setId(translationId);
            newTranslation.setAreaName(areaName);

            // Save AreaTranslation
            areaTranslationRepository.save(newTranslation);

            result.put("success", true);
            result.put("areaId", nextAreaId);
            result.put("areaName", areaName);
            result.put("cityId", cityId);
            result.put("stateId", stateId);
            result.put("countryId", countryId);
            result.put("message", "Area created successfully");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to create area: " + e.getMessage());
            System.err.println("=== ERROR CREATING AREA ===");
            System.err.println("Area Name: " + areaName);
            System.err.println("City ID: " + cityId);
            System.err.println("State ID: " + stateId);
            System.err.println("Country ID: " + countryId);
            System.err.println("Language ID: " + languageId);
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Error Type: " + e.getClass().getName());
            e.printStackTrace();
        }

        return result;
    }
}
