package com.climasys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite ID class for GroupInstructions entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInstructionsId implements Serializable {
    
    private String doctorId;
    private String groupDescription;
    private String instructionsDescription;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupInstructionsId)) return false;
        GroupInstructionsId that = (GroupInstructionsId) o;
        return doctorId != null && doctorId.equals(that.doctorId) &&
               groupDescription != null && groupDescription.equals(that.groupDescription) &&
               instructionsDescription != null && instructionsDescription.equals(that.instructionsDescription);
    }
    
    @Override
    public int hashCode() {
        int result = doctorId != null ? doctorId.hashCode() : 0;
        result = 31 * result + (groupDescription != null ? groupDescription.hashCode() : 0);
        result = 31 * result + (instructionsDescription != null ? instructionsDescription.hashCode() : 0);
        return result;
    }
}

