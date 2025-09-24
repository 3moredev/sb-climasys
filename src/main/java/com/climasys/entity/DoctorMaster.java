package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "doctor_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorMaster {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Column(name = "prefix", length = 10)
    private String prefix;
    
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Column(name = "speciality", length = 100)
    private String speciality;
    
    @Column(name = "qualification", length = 200)
    private String qualification;
    
    @Column(name = "mobile", length = 15)
    private String mobile;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_on")
    private java.time.LocalDateTime createdOn;
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @Column(name = "modified_on")
    private java.time.LocalDateTime modifiedOn;
    
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;
    
    @Column(name = "delete_flag")
    private Boolean deleteFlag;
}
