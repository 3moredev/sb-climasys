package com.climasys.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Login response containing authentication status and user information")
public class LoginResponse {

    @Schema(description = "Login status: 1=success, 0=failed, -1=error", example = "1")
    private Integer loginStatus;

    @Schema(description = "User details if login successful")
    private UserDetails userDetails;

    @Schema(description = "Available shift times")
    private List<ShiftTime> shiftTimes;

    @Schema(description = "Available user roles")
    private List<AvailableRole> availableRoles;

    @Schema(description = "System parameters")
    private List<SystemParam> systemParams;

    @Schema(description = "License key for the application", example = "TEST_LICENSE_KEY_12345")
    private String licenseKey;

    @Schema(description = "Additional user master details")
    private UserMasterDetails userMasterDetails;

    @Schema(description = "Error message if login failed", example = "Invalid credentials")
    private String errorMessage;

    // Constructors
    public LoginResponse() {
    }

    public LoginResponse(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }

    // Inner classes for nested data
    @Schema(description = "User details information")
    public static class UserDetails {
        private Long id;
        private String doctorId;
        private String clinicId;
        private String loginId;
        private String firstName;
        private String password;
        private String roleName;
        private Integer roleId;
        private String doctorName;
        private String clinicName;
        private Integer languageId;
        private Boolean isActive;
        private Integer financialYear;
        private Integer modelId;
        private Integer configId;
        private Boolean isEnabled;

        public UserDetails() {
        }

        // Getters and Setters with Jackson annotations
        @com.fasterxml.jackson.annotation.JsonProperty("user_id")
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("doctor_id")
        public String getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(String doctorId) {
            this.doctorId = doctorId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("clinic_id")
        public String getClinicId() {
            return clinicId;
        }

        public void setClinicId(String clinicId) {
            this.clinicId = clinicId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("login_id")
        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("first_name")
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("password")
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("role_name")
        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("role_id")
        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("doctor_name")
        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("clinic_name")
        public String getClinicName() {
            return clinicName;
        }

        public void setClinicName(String clinicName) {
            this.clinicName = clinicName;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("language_id")
        public Integer getLanguageId() {
            return languageId;
        }

        public void setLanguageId(Integer languageId) {
            this.languageId = languageId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("is_active")
        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("financial_year")
        public Integer getFinancialYear() {
            return financialYear;
        }

        public void setFinancialYear(Integer financialYear) {
            this.financialYear = financialYear;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("model_id")
        public Integer getModelId() {
            return modelId;
        }

        public void setModelId(Integer modelId) {
            this.modelId = modelId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("config_id")
        public Integer getConfigId() {
            return configId;
        }

        public void setConfigId(Integer configId) {
            this.configId = configId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("is_enabled")
        public Boolean getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }

    public static class ShiftTime {
        private Integer shiftId;
        private String description;

        public ShiftTime() {
        }

        public ShiftTime(Integer shiftId, String description) {
            this.shiftId = shiftId;
            this.description = description;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("shift_id")
        public Integer getShiftId() {
            return shiftId;
        }

        public void setShiftId(Integer shiftId) {
            this.shiftId = shiftId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("description")
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class AvailableRole {
        private Integer roleId;
        private String roleName;

        public AvailableRole() {
        }

        public AvailableRole(Integer roleId, String roleName) {
            this.roleId = roleId;
            this.roleName = roleName;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("role_id")
        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("role_name")
        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }

    public static class SystemParam {
        private String paramName;
        private String paramValue;
        private String doctorId;

        public SystemParam() {
        }

        public SystemParam(String paramName, String paramValue, String doctorId) {
            this.paramName = paramName;
            this.paramValue = paramValue;
            this.doctorId = doctorId;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("param_name")
        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("param_value")
        public String getParamValue() {
            return paramValue;
        }

        public void setParamValue(String paramValue) {
            this.paramValue = paramValue;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("doctor_id")
        public String getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(String doctorId) {
            this.doctorId = doctorId;
        }
    }

    public static class UserMasterDetails {
        private Long id;
        private String loginId;
        private String firstName;
        private String doctorId;

        public UserMasterDetails() {
        }

        public UserMasterDetails(Long id, String loginId, String firstName, String doctorId) {
            this.id = id;
            this.loginId = loginId;
            this.firstName = firstName;
            this.doctorId = doctorId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(String doctorId) {
            this.doctorId = doctorId;
        }
    }

    // Main class getters and setters
    public Integer getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public List<ShiftTime> getShiftTimes() {
        return shiftTimes;
    }

    public void setShiftTimes(List<ShiftTime> shiftTimes) {
        this.shiftTimes = shiftTimes;
    }

    public List<AvailableRole> getAvailableRoles() {
        return availableRoles;
    }

    public void setAvailableRoles(List<AvailableRole> availableRoles) {
        this.availableRoles = availableRoles;
    }

    public List<SystemParam> getSystemParams() {
        return systemParams;
    }

    public void setSystemParams(List<SystemParam> systemParams) {
        this.systemParams = systemParams;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public UserMasterDetails getUserMasterDetails() {
        return userMasterDetails;
    }

    public void setUserMasterDetails(UserMasterDetails userMasterDetails) {
        this.userMasterDetails = userMasterDetails;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
