package com.climasys.advance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for advance collection search results (autocomplete)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceCollectionSearchResultDTO {
    
    @JsonProperty("ipdRefNo")
    private String ipdRefNo;
    
    @JsonProperty("patientId")
    private String patientId;
    
    @JsonProperty("patientName")
    private String patientName;
    
    @JsonProperty("mobile")
    private String mobile;
    
    @JsonProperty("visitDate")
    private String visitDate;
    
    @JsonProperty("searchValue")
    private String searchValue;
}

