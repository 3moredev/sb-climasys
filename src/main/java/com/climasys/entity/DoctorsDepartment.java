package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the doctors_department table
 * Maps doctor names to department names
 * 
 * Primary Key: (doctor_name, department_name)
 */
@Entity
@Table(name = "doctors_department")
@IdClass(DoctorsDepartmentId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorsDepartment {
    
    @Id
    @Column(name = "doctor_name", length = 50, nullable = false)
    private String doctorName;
    
    @Id
    @Column(name = "department_name", length = 50, nullable = false)
    private String departmentName;
}

