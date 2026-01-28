package com.climasys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for Patient_Documents_Treatment table
 * Stores treatment-related documents for patient visits
 */
@Entity
@Table(name = "patient_documents_treatment", schema = "public")
public class PatientDocumentTreatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "patient_id", length = 50)
    private String patientId;

    @Column(name = "doctor_id", length = 50)
    private String doctorId;

    @Column(name = "clinic_id", length = 50)
    private String clinicId;

    @Column(name = "document_name", length = 200)
    private String documentName;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "createdby_name", length = 50)
    private String createdbyName;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "modified_name", length = 50)
    private String modifiedName;

    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    @Column(name = "patient_visit_no")
    private Integer patientVisitNo;

    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @Column(name = "file_size")
    private Long fileSize;

    // Constructors
    public PatientDocumentTreatment() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedbyName() {
        return createdbyName;
    }

    public void setCreatedbyName(String createdbyName) {
        this.createdbyName = createdbyName;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedName() {
        return modifiedName;
    }

    public void setModifiedName(String modifiedName) {
        this.modifiedName = modifiedName;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Integer getPatientVisitNo() {
        return patientVisitNo;
    }

    public void setPatientVisitNo(Integer patientVisitNo) {
        this.patientVisitNo = patientVisitNo;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "PatientDocumentTreatment{" +
                "id=" + id +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", clinicId='" + clinicId + '\'' +
                ", documentName='" + documentName + '\'' +
                ", patientVisitNo=" + patientVisitNo +
                ", visitDate=" + visitDate +
                ", deleteFlag=" + deleteFlag +
                '}';
    }
}
