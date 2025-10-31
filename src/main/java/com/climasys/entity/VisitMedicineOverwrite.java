package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_medicine_overwrite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VisitMedicineOverwriteId.class)
public class VisitMedicineOverwrite {
    
    @Id
    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;
    
    @Id
    @Column(name = "patient_visit_no", nullable = false)
    private Integer patientVisitNo;
    
    @Id
    @Column(name = "shift_id", nullable = false)
    private Short shiftId;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "patient_id", length = 32, nullable = false)
    private String patientId;
    
    @Id
    @Column(name = "short_description", length = 300, nullable = false)
    private String shortDescription;
    
    @Column(name = "medicine_description", length = 4000)
    private String medicineDescription;
    
    @Column(name = "morning")
    private Double morning;
    
    @Column(name = "afternoon")
    private Double afternoon;
    
    @Column(name = "night")
    private Double night;
    
    @Column(name = "no_of_days")
    private Integer noOfDays;
    
    @Column(name = "instruction", length = 4000)
    private String instruction;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    @Column(name = "delete_indicator")
    private Boolean deleteIndicator;
    
    @Column(name = "delete_flag")
    private Boolean deleteFlag;
}

