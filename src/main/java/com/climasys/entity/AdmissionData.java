package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity class for admission_data table
 * Represents patient admission information
 */
@Entity
@Table(name = "admission_data")
@IdClass(AdmissionDataId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionData {
    
    @Id
    @Column(name = "patient_id", length = 32, nullable = false)
    private String patientId;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Id
    @Column(name = "ipd_refno", length = 100, nullable = false)
    private String ipdRefno;
    
    @Column(name = "relativename", columnDefinition = "TEXT")
    private String relativeName;
    
    @Column(name = "relation", columnDefinition = "TEXT")
    private String relation;
    
    @Column(name = "contactno", columnDefinition = "TEXT")
    private String contactNo;
    
    @Column(name = "admission_date")
    private LocalDate admissionDate;
    
    @Column(name = "admission_time")
    private LocalTime admissionTime;
    
    @Column(name = "reasonofadmission", columnDefinition = "TEXT")
    private String reasonOfAdmission;
    
    @Column(name = "shift_id", nullable = false)
    private Short shiftId;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "isinsurance", nullable = false)
    private Boolean isInsurance;
    
    @Column(name = "insurancedetails", columnDefinition = "TEXT")
    private String insuranceDetails;
    
    @Column(name = "treatingdoctor", columnDefinition = "TEXT")
    private String treatingDoctor;
    
    @Column(name = "consultantdoctor", columnDefinition = "TEXT")
    private String consultantDoctor;
    
    @Column(name = "ipdfileno", columnDefinition = "TEXT")
    private String ipdFileNo;
    
    @Column(name = "roomno", columnDefinition = "TEXT")
    private String roomNo;
    
    @Column(name = "packageremarks", columnDefinition = "TEXT")
    private String packageRemarks;
    
    @Column(name = "createdby_name", length = 100)
    private String createdByName;
    
    @Column(name = "modifiedby_name", length = 100)
    private String modifiedByName;
    
    @Column(name = "created_on")
    private LocalDate createdOn;
    
    @Column(name = "modified_on")
    private LocalDate modifiedOn;
    
    @Column(name = "bedno", length = 20)
    private String bedNo;
    
    @Column(name = "referred_doctor", length = 50)
    private String referredDoctor;
    
    @Column(name = "comments_note", length = 1000)
    private String commentsNote;
    
    @Column(name = "insurance_company_id")
    private Integer insuranceCompanyId;
}

