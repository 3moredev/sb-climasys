package com.climasys.doctors.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.climasys.entity.LicenceKey;

import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for clinic management and operations
 */
@Service
public class ClinicManagementService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private com.climasys.repository.ClinicRepository clinicRepository;

    @Autowired
    private com.climasys.repository.CityTranslationRepository cityTranslationRepository;

    @Autowired
    private com.climasys.repository.LicenceKeyRepository licenceKeyRepository;

    @Value("${app.api.base-url:http://localhost:8080/api}")
    private String baseUrl;

    /**
     * Get clinic details and information
     */
    public List<Map<String, Object>> getClinicDetails(String clinicId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/clinic-details/" + clinicId;

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get clinic details: " + e.getMessage(), e);
        }
    }

    /**
     * Get clinic shifts and schedules
     */
    public List<Map<String, Object>> getClinicShifts(String clinicId) {
        try {
            String url = baseUrl + "/doctors/stored-procs/clinic-shifts/" + clinicId;

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get clinic shifts: " + e.getMessage(), e);
        }
    }

    /**
     * Get clinic shift timings for a specific day
     */
    public List<Map<String, Object>> getClinicShiftTimings(String clinicId, String shiftDay) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/doctors/stored-procs/clinic-shifts-time")
                    .queryParam("clinicId", clinicId)
                    .queryParam("shiftDay", shiftDay)
                    .toUriString();

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get clinic shift timings: " + e.getMessage(), e);
        }
    }

    /**
     * Get all clinics
     */
    public List<com.climasys.entity.Clinic> getAllClinics() {
        List<com.climasys.entity.Clinic> clinics = clinicRepository.findAll();

        if (clinics != null && !clinics.isEmpty()) {
            List<String> cityIds = clinics.stream()
                    .map(com.climasys.entity.Clinic::getCityId)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());

            // Assuming languageId 1 for English/Default
            List<com.climasys.entity.CityTranslation> cityTranslations = cityTranslationRepository
                    .findByIdCityIdInAndIdLanguageId(cityIds, 1);

            Map<String, String> cityMap = cityTranslations.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            ct -> ct.getId().getCityId(),
                            com.climasys.entity.CityTranslation::getCityName,
                            (existing, replacement) -> existing));

            List<String> clinicIds = clinics.stream()
                    .map(com.climasys.entity.Clinic::getClinicId)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());

            List<com.climasys.entity.LicenceKey> licenceKeys = licenceKeyRepository.findByClinicIdIn(clinicIds);

            Map<String, java.time.LocalDateTime> licenseValidToMap = licenceKeys.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            com.climasys.entity.LicenceKey::getClinicId,
                            com.climasys.entity.LicenceKey::getValidTo,
                            (existing, replacement) -> existing));

            clinics.forEach(clinic -> {
                if (clinic.getCityId() != null) {
                    clinic.setCityName(cityMap.get(clinic.getCityId()));
                }
                if (clinic.getClinicId() != null) {
                    clinic.setLicenseValidTo(licenseValidToMap.get(clinic.getClinicId()));
                }
            });
        }

        return clinics;
    }

    /**
     * Get count of all clinics
     */
    public long getClinicCount() {
        return clinicRepository.countUniqueClinics();
    }

    /**
     * Save a new clinic
     */
    public com.climasys.entity.Clinic saveClinic(com.climasys.entity.Clinic clinic) {
        clinic.setClinicId(java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 30).toUpperCase());
        clinic.setOwnerId(java.util.UUID.randomUUID().toString());
        clinic.setOwnerName(clinic.getClinicName());
        clinic.setCreatedOn(java.time.LocalDateTime.now());
        clinic.setCreatedbyName("Admin");
        clinic.setModifiedOn(java.time.LocalDateTime.now());
        clinic.setModifiedbyName("Admin");
        clinic.setIsPrint(true);

        clinicRepository.save(clinic);

        LicenceKey licenceKey = new LicenceKey();
        licenceKey.setClinicId(clinic.getClinicId());
        licenceKey.setDoctorId("DR-00010");
        licenceKey.setValidFrom(java.time.LocalDateTime.now());
        licenceKey.setValidTo(java.time.LocalDateTime.now().plusYears(1));
        licenceKey.setVersion("1.0");
        licenceKey.setInstallationDate(java.time.LocalDateTime.now());
        licenceKey.setLastRenewalDate(java.time.LocalDateTime.now());
        licenceKey.setLicenseValidity(365);
        licenceKey.setLicenseKey(java.util.UUID.randomUUID().toString());
        licenceKey.setStartVerification(8);

        licenceKeyRepository.save(licenceKey);
        return clinic;
    }

    /**
     * Update an existing clinic
     */
    public com.climasys.entity.Clinic updateClinic(String clinicId, com.climasys.entity.Clinic clinic) {
        // Ensure the clinicId matches the path variable
        if (!clinicId.equals(clinic.getClinicId())) {
            throw new IllegalArgumentException("Clinic ID in path does not match request body");
        }

        // Update modified info
        clinic.setModifiedOn(java.time.LocalDateTime.now());
        clinic.setModifiedbyName("Admin");

        clinicRepository.save(clinic);
        return clinic;
    }

    /**
     * Delete a clinic by ID
     */
    @org.springframework.transaction.annotation.Transactional
    public void deleteClinic(String clinicId) {
        clinicRepository.deleteByClinicId(clinicId);
    }

    /**
     * Get clinic by ID
     */
    public com.climasys.entity.Clinic getClinicById(String clinicId) {
        List<com.climasys.entity.Clinic> clinics = clinicRepository.findByClinicId(clinicId);
        if (clinics != null && !clinics.isEmpty()) {
            // Return the first one found
            return clinics.get(0);
        }
        return null;
    }
}
