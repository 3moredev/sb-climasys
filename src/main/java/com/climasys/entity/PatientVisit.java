package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PatientVisitId.class)
public class PatientVisit {
    
    @Id
    @Column(name = "doctor_id", length = 30, nullable = false)
    private String doctorId;
    
    @Id
    @Column(name = "clinic_id", length = 10, nullable = false)
    private String clinicId;
    
    @Id
    @Column(name = "shift_id", nullable = false)
    private Short shiftId;
    
    @Id
    @Column(name = "patient_id", length = 32, nullable = false)
    private String patientId;
    
    @Id
    @Column(name = "patient_visit_no", nullable = false)
    private Integer patientVisitNo;
    
    @Id
    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;
    
    @Column(name = "folder_no", length = 30)
    private String folderNo;
    
    @Column(name = "visit_time")
    private java.sql.Time visitTime;
    
    @Column(name = "financial_year")
    private Integer financialYear;
    
    @Column(name = "status_id", nullable = false)
    private Short statusId;
    
    @Column(name = "weight_in_kgs", precision = 8, scale = 2)
    private BigDecimal weightInKgs;
    
    @Column(name = "reports_asked")
    private Boolean reportsAsked;
    
    @Column(name = "reports_received")
    private Boolean reportsReceived;
    
    @Column(name = "asthama")
    private Boolean asthama;
    
    @Column(name = "tuberculosis")
    private Boolean tuberculosis;
    
    @Column(name = "hypertension")
    private Boolean hypertension;
    
    @Column(name = "diabetes")
    private Boolean diabetes;
    
    @Column(name = "cholestrol")
    private Boolean cholestrol;
    
    @Column(name = "allergy_dtls", length = 500)
    private String allergyDtls;
    
    @Column(name = "habits_comments", length = 1000)
    private String habitsComments;
    
    @Column(name = "tobaco")
    private Boolean tobaco;
    
    @Column(name = "smoking")
    private Boolean smoking;
    
    @Column(name = "alchohol")
    private Boolean alchohol;
    
    @Column(name = "ihd")
    private Boolean ihd;
    
    @Column(name = "th")
    private Boolean th;
    
    @Column(name = "height_in_cms", precision = 8, scale = 2)
    private BigDecimal heightInCms;
    
    @Column(name = "pulse")
    private Integer pulse;
    
    @Column(name = "blood_pressure", length = 10)
    private String bloodPressure;
    
    @Column(name = "fees_to_collect", precision = 10, scale = 2)
    private BigDecimal feesToCollect;
    
    @Column(name = "fees_collected", precision = 10, scale = 2)
    private BigDecimal feesCollected;
    
    @Column(name = "instructions", length = 1000)
    private String instructions;
    
    @Column(name = "appointment_sr_no")
    private Short appointmentSrNo;
    
    @Column(name = "patient_last_visit_no")
    private Integer patientLastVisitNo;
    
    @Column(name = "on_call_status")
    private Boolean onCallStatus;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
    
    @Column(name = "delete_flag")
    private Boolean deleteFlag;
    
    @Column(name = "comment", length = 500)
    private String comment;
    
    @Column(name = "observation", length = 1000)
    private String observation;
    
    @Column(name = "in_person")
    private Boolean inPerson;
    
    @Column(name = "original_billed_amount", precision = 10, scale = 2)
    private BigDecimal originalBilledAmount;
    
    @Column(name = "sugar", length = 25)
    private String sugar;
    
    @Column(name = "thtext", length = 1000)
    private String thtext;
    
    @Column(name = "offline_reason", length = 500)
    private String offlineReason;
    
    @Column(name = "offline_flag")
    private Boolean offlineFlag;
    
    @Column(name = "symptom_comment", length = 1000)
    private String symptomComment;
    
    @Column(name = "impression", length = 1000)
    private String impression;
    
    @Column(name = "payment_by_id")
    private Short paymentById;
    
    @Column(name = "attended_by", length = 30)
    private String attendedBy;
    
    @Column(name = "payment_remark", length = 1000)
    private String paymentRemark;
    
    @Column(name = "attended_by_id")
    private Integer attendedById;
    
    @Column(name = "follow_up", length = 100)
    private String followUp;
    
    @Column(name = "receipt_number", length = 10)
    private String receiptNumber;
    
    @Column(name = "is_follow_up")
    private Boolean isFollowUp;
    
    @Column(name = "current_medicines", length = 1000)
    private String currentMedicines;
    
    @Column(name = "visit_comments", length = 1000)
    private String visitComments;
    
    @Column(name = "current_complaints", length = 1000)
    private String currentComplaints;
    
    @Column(name = "is_submit_patient_visit_details")
    private Boolean isSubmitPatientVisitDetails;
    
    @Column(name = "is_submit_patient_labtest")
    private Boolean isSubmitPatientLabtest;
    
    @Column(name = "receipt_type", length = 1)
    private String receiptType;
    
    @Column(name = "tpr", length = 10)
    private String tpr;
    
    @Column(name = "important_findings", length = 1000)
    private String importantFindings;
    
    @Column(name = "additional_comments", length = 1000)
    private String additionalComments;
    
    @Column(name = "systemic", length = 30)
    private String systemic;
    
    @Column(name = "odeama", length = 10)
    private String odeama;
    
    @Column(name = "pallor", length = 10)
    private String pallor;
    
    @Column(name = "is_submit_gynec_details")
    private Boolean isSubmitGynecDetails;
    
    @Column(name = "gc", length = 20)
    private String gc;
    
    @Column(name = "fmp", length = 1000)
    private String fmp;
    
    @Column(name = "prmc", length = 1000)
    private String prmc;
    
    @Column(name = "pamc", length = 1000)
    private String pamc;
    
    @Column(name = "lmp", length = 1000)
    private String lmp;
    
    @Column(name = "obstetrics_history", length = 1000)
    private String obstetricsHistory;
    
    @Column(name = "surgical_history_past_history", length = 1000)
    private String surgicalHistoryPastHistory;
    
    @Column(name = "gynec_additional_comments", length = 1000)
    private String gynecAdditionalComments;
    
    @Column(name = "pregnant")
    private Boolean pregnant;
    
    @Column(name = "edd")
    private LocalDateTime edd;
    
    @Column(name = "follow_up_type")
    private Short followUpType;
    
    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;
    
    @Column(name = "addendum", length = 1000)
    private String addendum;
    
    @Column(name = "from_time")
    private java.sql.Time fromTime;
    
    @Column(name = "follow_up_comment", length = 1000)
    private String followUpComment;
    
    @Column(name = "treatment_comment", length = 1000)
    private String treatmentComment;
    
    @Column(name = "treatment_plan", length = 1000)
    private String treatmentPlan;
    
    @Column(name = "doctor_notes", length = 100)
    private String doctorNotes;
    
    @Column(name = "plan", length = 1000)
    private String plan;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "followup_after")
    private Short followupAfter;
    
    @Column(name = "schedule")
    private Short schedule;
    
    @Column(name = "additional_instructions", length = 1000)
    private String additionalInstructions;
    
    @Column(name = "impression_finding", length = 1000)
    private String impressionFinding;
    
    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount;
    
    @Column(name = "original_discount", precision = 10, scale = 2)
    private BigDecimal originalDiscount;
    
    @Column(name = "complaints_by_patient_per_visit", length = 400)
    private String complaintsByPatientPerVisit;
    
    @Column(name = "cat_id")
    private Integer catId;
    
    @Column(name = "online_appointment_time")
    private java.sql.Time onlineAppointmentTime;
    
    @Column(name = "refer_id", length = 1)
    private String referId;
    
    @Column(name = "refer_doctor_details", length = 200)
    private String referDoctorDetails;
    
    @Column(name = "doctor_address", length = 150)
    private String doctorAddress;
    
    @Column(name = "doctor_mobile", length = 20)
    private String doctorMobile;
    
    @Column(name = "doctor_email", length = 60)
    private String doctorEmail;
}
