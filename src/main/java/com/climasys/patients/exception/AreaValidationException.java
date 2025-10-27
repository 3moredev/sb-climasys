package com.climasys.patients.exception;

/**
 * Exception thrown when area validation fails
 */
public class AreaValidationException extends ValidationException {
    
    private final Integer areaId;
    private final String cityId;
    private final String stateId;
    private final String countryId;
    
    public AreaValidationException(Integer areaId, String cityId, String stateId, String countryId) {
        super("area", 
              String.format("Area combination (area_id=%d, city_id=%s, state_id=%s, country_id=%s) not found in area_master table", 
                           areaId, cityId, stateId, countryId),
              "AREA_NOT_FOUND");
        this.areaId = areaId;
        this.cityId = cityId;
        this.stateId = stateId;
        this.countryId = countryId;
    }
    
    public AreaValidationException(String message) {
        super("area", message, "AREA_VALIDATION_ERROR");
        this.areaId = null;
        this.cityId = null;
        this.stateId = null;
        this.countryId = null;
    }
    
    public Integer getAreaId() {
        return areaId;
    }
    
    public String getCityId() {
        return cityId;
    }
    
    public String getStateId() {
        return stateId;
    }
    
    public String getCountryId() {
        return countryId;
    }
}
