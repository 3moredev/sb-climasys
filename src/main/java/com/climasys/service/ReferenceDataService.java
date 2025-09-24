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
    
    public List<Gender> getGenders() {
        return genderRepository.findAllOrdered();
    }
    
    public List<BloodGroup> getBloodGroups() {
        return bloodGroupRepository.findAllOrdered();
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
    
    public List<City> getCities(String stateId) {
        if (stateId != null && !stateId.isEmpty()) {
            return cityRepository.findByStateId(stateId);
        }
        return cityRepository.findAllOrdered();
    }
    
    public List<State> getStates(String countryId) {
        if (countryId != null && !countryId.isEmpty()) {
            return stateRepository.findByCountryId(countryId);
        }
        return stateRepository.findAllOrdered();
    }
    
    public List<Country> getCountries() {
        return countryRepository.findAllOrdered();
    }
}
