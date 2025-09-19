package com.climasys.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_master")
public class DoctorMaster {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Column(name = "prefix", length = 10)
    private String prefix;
    
    @Column(name = "first_name", length = 100)
    private String firstName;
    
    @Column(name = "middle_name", length = 100)
    private String middleName;
    
    @Column(name = "last_name", length = 100)
    private String lastName;
    
    @Column(name = "doctor_photo")
    private byte[] doctorPhoto;
    
    @Column(name = "registration_no", length = 100)
    private String registrationNo;
    
    @Column(name = "speciality", length = 50)
    private String speciality;
    
    @Column(name = "residential_no", length = 30)
    private String residentialNo;
    
    @Column(name = "practising_year")
    private Integer practisingYear;
    
    @Column(name = "mobile_1", length = 20)
    private String mobile1;
    
    @Column(name = "mobile_2", length = 20)
    private String mobile2;
    
    @Column(name = "emergency_number", length = 30)
    private String emergencyNumber;
    
    @Column(name = "wapp_no", length = 20)
    private String wappNo;
    
    @Column(name = "emailid", length = 60)
    private String emailid;
    
    @Column(name = "doctor_qual", length = 100)
    private String doctorQual;
    
    @Column(name = "residential_add1", length = 200)
    private String residentialAdd1;
    
    @Column(name = "residential_add2", length = 200)
    private String residentialAdd2;
    
    @Column(name = "country_id", length = 6, nullable = false)
    private String countryId;
    
    @Column(name = "state_id", length = 6, nullable = false)
    private String stateId;
    
    @Column(name = "city_id", length = 6, nullable = false)
    private String cityId;
    
    @Column(name = "area_id", nullable = false)
    private Integer areaId;
    
    @Column(name = "profile_image", length = 300)
    private String profileImage;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    @Column(name = "base_location", length = 255)
    private String baseLocation;
    
    @Column(name = "ipd_dr")
    private Boolean ipdDr;
    
    @Column(name = "opd_dr")
    private Boolean opdDr;
    
    // Constructors
    public DoctorMaster() {}
    
    public DoctorMaster(String doctorId, String firstName, String lastName) {
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdOn = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public byte[] getDoctorPhoto() { return doctorPhoto; }
    public void setDoctorPhoto(byte[] doctorPhoto) { this.doctorPhoto = doctorPhoto; }
    
    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    
    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }
    
    public String getResidentialNo() { return residentialNo; }
    public void setResidentialNo(String residentialNo) { this.residentialNo = residentialNo; }
    
    public Integer getPractisingYear() { return practisingYear; }
    public void setPractisingYear(Integer practisingYear) { this.practisingYear = practisingYear; }
    
    public String getMobile1() { return mobile1; }
    public void setMobile1(String mobile1) { this.mobile1 = mobile1; }
    
    public String getMobile2() { return mobile2; }
    public void setMobile2(String mobile2) { this.mobile2 = mobile2; }
    
    public String getEmergencyNumber() { return emergencyNumber; }
    public void setEmergencyNumber(String emergencyNumber) { this.emergencyNumber = emergencyNumber; }
    
    public String getWappNo() { return wappNo; }
    public void setWappNo(String wappNo) { this.wappNo = wappNo; }
    
    public String getEmailid() { return emailid; }
    public void setEmailid(String emailid) { this.emailid = emailid; }
    
    public String getDoctorQual() { return doctorQual; }
    public void setDoctorQual(String doctorQual) { this.doctorQual = doctorQual; }
    
    public String getResidentialAdd1() { return residentialAdd1; }
    public void setResidentialAdd1(String residentialAdd1) { this.residentialAdd1 = residentialAdd1; }
    
    public String getResidentialAdd2() { return residentialAdd2; }
    public void setResidentialAdd2(String residentialAdd2) { this.residentialAdd2 = residentialAdd2; }
    
    public String getCountryId() { return countryId; }
    public void setCountryId(String countryId) { this.countryId = countryId; }
    
    public String getStateId() { return stateId; }
    public void setStateId(String stateId) { this.stateId = stateId; }
    
    public String getCityId() { return cityId; }
    public void setCityId(String cityId) { this.cityId = cityId; }
    
    public Integer getAreaId() { return areaId; }
    public void setAreaId(Integer areaId) { this.areaId = areaId; }
    
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    
    public String getCreatedbyName() { return createdbyName; }
    public void setCreatedbyName(String createdbyName) { this.createdbyName = createdbyName; }
    
    public LocalDateTime getModifiedOn() { return modifiedOn; }
    public void setModifiedOn(LocalDateTime modifiedOn) { this.modifiedOn = modifiedOn; }
    
    public String getModifiedbyName() { return modifiedbyName; }
    public void setModifiedbyName(String modifiedbyName) { this.modifiedbyName = modifiedbyName; }
    
    public String getBaseLocation() { return baseLocation; }
    public void setBaseLocation(String baseLocation) { this.baseLocation = baseLocation; }
    
    public Boolean getIpdDr() { return ipdDr; }
    public void setIpdDr(Boolean ipdDr) { this.ipdDr = ipdDr; }
    
    public Boolean getOpdDr() { return opdDr; }
    public void setOpdDr(Boolean opdDr) { this.opdDr = opdDr; }
}
