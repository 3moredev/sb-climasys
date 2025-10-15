package com.climasys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite ID class for InstructionsGroupMaster entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructionsGroupMasterId implements Serializable {
    
    private String doctorId;
    private String groupDescription;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstructionsGroupMasterId)) return false;
        InstructionsGroupMasterId that = (InstructionsGroupMasterId) o;
        return doctorId != null && doctorId.equals(that.doctorId) &&
               groupDescription != null && groupDescription.equals(that.groupDescription);
    }
    
    @Override
    public int hashCode() {
        return 31 * (doctorId != null ? doctorId.hashCode() : 0) + 
               (groupDescription != null ? groupDescription.hashCode() : 0);
    }
}

