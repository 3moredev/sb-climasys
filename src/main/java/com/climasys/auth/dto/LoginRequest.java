package com.climasys.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Login request containing user credentials and preferences")
public class LoginRequest {
    
    @Schema(description = "User's login ID", example = "doctor123", required = true)
    @NotBlank(message = "Login ID is required")
    private String loginId;
    
    @Schema(description = "User's password", example = "password123", required = true)
    @NotBlank(message = "Password is required")
    private String password;
    
    @Schema(description = "Current day for shift management", example = "Monday", required = true)
    @NotBlank(message = "Today's day is required")
    private String todaysDay;
    
    @Schema(description = "Language preference ID (1=English, 2=Hindi, etc.)", example = "1", required = true)
    @NotNull(message = "Language ID is required")
    private Integer languageId;
    
    // Constructors
    public LoginRequest() {}
    
    public LoginRequest(String loginId, String password, String todaysDay, Integer languageId) {
        this.loginId = loginId;
        this.password = password;
        this.todaysDay = todaysDay;
        this.languageId = languageId;
    }
    
    // Getters and Setters
    public String getLoginId() {
        return loginId;
    }
    
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getTodaysDay() {
        return todaysDay;
    }
    
    public void setTodaysDay(String todaysDay) {
        this.todaysDay = todaysDay;
    }
    
    public Integer getLanguageId() {
        return languageId;
    }
    
    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }
}
