package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinic_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ClinicId.class)
public class Clinic {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Column(name = "clinic_name", length = 100)
    private String clinicName;
    
    @Column(name = "clinic_address", length = 200)
    private String clinicAddress;
    
    @Column(name = "country_id", length = 6, nullable = false)
    private String countryId;
    
    @Column(name = "state_id", length = 6, nullable = false)
    private String stateId;
    
    @Column(name = "city_id", length = 6, nullable = false)
    private String cityId;
    
    @Column(name = "area_id", nullable = false)
    private Integer areaId;
    
    @Column(name = "pincode", length = 15)
    private String pincode;
    
    @Column(name = "tips", length = 4000)
    private String tips;
    
    @Column(name = "news", length = 2000)
    private String news;
    
    @Column(name = "phone_no", length = 50)
    private String phoneNo;
    
    @Column(name = "fax_no", length = 30)
    private String faxNo;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    @Column(name = "is_print")
    private Boolean isPrint;
}
