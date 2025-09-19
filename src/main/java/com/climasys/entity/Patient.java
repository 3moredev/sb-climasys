package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    
    @Id
    @Column(name = "id", length = 32)
    private String id;
    
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Column(name = "folder_no", length = 30)
    private String folderNo;
    
    @Column(name = "first_name", length = 100)
    private String firstName;
    
    @Column(name = "middle_name", length = 100)
    private String middleName;
    
    @Column(name = "last_name", length = 100)
    private String lastName;
    
    @Column(name = "reports_received")
    private Boolean reportsReceived;
    
    @Column(name = "email_id", length = 60)
    private String emailId;
    
    @Column(name = "gender_id", length = 1)
    private String genderId;
    
    @Column(name = "address_1", length = 150)
    private String address1;
    
    @Column(name = "address_2", length = 150)
    private String address2;
    
    @Column(name = "country_id", length = 6)
    private String countryId;
    
    @Column(name = "state_id", length = 6)
    private String stateId;
    
    @Column(name = "city_id", length = 6)
    private String cityId;
    
    @Column(name = "area_id")
    private Integer areaId;
    
    @Column(name = "pincode", length = 20)
    private String pincode;
    
    @Column(name = "date_of_birth")
    private java.sql.Date dateOfBirth;
    
    @Column(name = "age_given")
    private Short ageGiven;
    
    @Column(name = "weight_in_kgs", precision = 8, scale = 2)
    private BigDecimal weightInKgs;
    
    @Column(name = "height_in_cms", precision = 8, scale = 2)
    private BigDecimal heightInCms;
    
    @Column(name = "occupation_id")
    private Integer occupationId;
    
    @Column(name = "bloodgroup_id")
    private Short bloodgroupId;
    
    @Column(name = "residential_no", length = 30)
    private String residentialNo;
    
    @Column(name = "mobile_1", length = 20)
    private String mobile1;
    
    @Column(name = "mobile_2", length = 20)
    private String mobile2;
    
    @Column(name = "emergency_number", length = 30)
    private String emergencyNumber;
    
    @Column(name = "emergency_name", length = 60)
    private String emergencyName;
    
    @Column(name = "patient_last_visit_no")
    private Integer patientLastVisitNo;
    
    @Column(name = "date_of_registration")
    private java.sql.Date dateOfRegistration;
    
    @Column(name = "manual_registration_year")
    private Integer manualRegistrationYear;
    
    @Column(name = "registration_status", length = 1)
    private String registrationStatus;
    
    @Column(name = "folder_path", length = 100)
    private String folderPath;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    @Column(name = "refer_id", length = 1)
    private String referId;
    
    @Column(name = "refer_doctor_details", length = 200)
    private String referDoctorDetails;
    
    @Column(name = "marital_status_id", length = 1)
    private String maritalStatusId;
    
    @Column(name = "doctor_address", length = 150)
    private String doctorAddress;
    
    @Column(name = "doctor_mobile", length = 20)
    private String doctorMobile;
    
    @Column(name = "doctor_email", length = 60)
    private String doctorEmail;
    
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
}
