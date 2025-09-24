package com.climasys.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusRefId implements Serializable {
    
    private Short id;
    private String clinicId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusRefId that = (StatusRefId) o;
        return id.equals(that.id) && clinicId.equals(that.clinicId);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode() + clinicId.hashCode();
    }
}
