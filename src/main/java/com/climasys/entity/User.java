package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "doctor_id")
    private String doctorId;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "login_id")
    private String loginId;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "middle_name")
    private String middleName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "residential_add1")
    private String residentialAdd1;
    
    @Column(name = "residential_add2")
    private String residentialAdd2;
    
    @Column(name = "country_id")
    private String countryId;
    
    @Column(name = "state_id")
    private String stateId;
    
    @Column(name = "city_id")
    private String cityId;
    
    @Column(name = "area_id")
    private Integer areaId;
    
    @Column(name = "pin_code")
    private String pinCode;
    
    @Column(name = "phone_no")
    private String phoneNo;
    
    @Column(name = "mobile_no")
    private String mobileNo;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "language_id")
    private Integer languageId;
    
    @Column(name = "user_photo")
    private String userPhoto;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name")
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name")
    private String modifiedbyName;
    
    @Column(name = "clinic_id")
    private String clinicId;
    
    @Column(name = "base_location")
    private String baseLocation;
    
    @Column(name = "default_doctor")
    private String defaultDoctor;
    
    @Column(name = "default_doctors_department")
    private String defaultDoctorsDepartment;
    
    @Column(name = "show_daily_collection")
    private Boolean showDailyCollection;
    
    @Column(name = "backupdrive")
    private String backupdrive;
}
