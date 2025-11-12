package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for DoctorsDepartment entity
 */
public class DoctorsDepartmentId implements Serializable {
    
    private String doctorName;
    private String departmentName;
    
    public DoctorsDepartmentId() {}
    
    public DoctorsDepartmentId(String doctorName, String departmentName) {
        this.doctorName = doctorName;
        this.departmentName = departmentName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoctorsDepartmentId that = (DoctorsDepartmentId) o;
        return Objects.equals(doctorName, that.doctorName) &&
               Objects.equals(departmentName, that.departmentName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(doctorName, departmentName);
    }
}

