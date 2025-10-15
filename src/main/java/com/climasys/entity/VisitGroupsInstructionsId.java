package com.climasys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Composite ID class for VisitGroupsInstructions entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitGroupsInstructionsId implements Serializable {
    
    private String doctorId;
    private String clinicId;
    private Short shiftId;
    private String patientId;
    private Integer patientVisitNo;
    private LocalDateTime visitDate;
    private String groupDescription;
    private String instructionsDescription;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitGroupsInstructionsId)) return false;
        VisitGroupsInstructionsId that = (VisitGroupsInstructionsId) o;
        return doctorId != null && doctorId.equals(that.doctorId) &&
               clinicId != null && clinicId.equals(that.clinicId) &&
               shiftId != null && shiftId.equals(that.shiftId) &&
               patientId != null && patientId.equals(that.patientId) &&
               patientVisitNo != null && patientVisitNo.equals(that.patientVisitNo) &&
               visitDate != null && visitDate.equals(that.visitDate) &&
               groupDescription != null && groupDescription.equals(that.groupDescription) &&
               instructionsDescription != null && instructionsDescription.equals(that.instructionsDescription);
    }
    
    @Override
    public int hashCode() {
        int result = doctorId != null ? doctorId.hashCode() : 0;
        result = 31 * result + (clinicId != null ? clinicId.hashCode() : 0);
        result = 31 * result + (shiftId != null ? shiftId.hashCode() : 0);
        result = 31 * result + (patientId != null ? patientId.hashCode() : 0);
        result = 31 * result + (patientVisitNo != null ? patientVisitNo.hashCode() : 0);
        result = 31 * result + (visitDate != null ? visitDate.hashCode() : 0);
        result = 31 * result + (groupDescription != null ? groupDescription.hashCode() : 0);
        result = 31 * result + (instructionsDescription != null ? instructionsDescription.hashCode() : 0);
        return result;
    }
}

