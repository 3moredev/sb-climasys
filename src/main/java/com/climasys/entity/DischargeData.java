package com.climasys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity class for discharge_data table
 */
@Entity
@Table(name = "discharge_data")
@IdClass(DischargeDataId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DischargeData {

    @Id
    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Id
    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Id
    @Column(name = "clinic_id", nullable = false)
    private String clinicId;

    @Id
    @Column(name = "ipd_refno", nullable = false)
    private String ipdRefno;

    @Column(name = "created_on")
    private LocalDate createdOn;

    @Column(name = "is_printed")
    private Boolean isPrinted;

    @Column(name = "admission_time")
    private LocalTime admissionTime;

    @Column(name = "modified_on")
    private LocalDate modifiedOn;

    @Column(name = "discharge_date")
    private LocalDate dischargeDate;

    @Column(name = "discharge_time")
    private LocalTime dischargeTime;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "shift_id", nullable = false)
    private Short shiftId;

    @Column(name = "operation_start_date")
    private LocalDate operationStartDate;

    @Column(name = "payment_by_cat")
    private Short paymentByCat;

    @Column(name = "operation_end_date")
    private LocalDate operationEndDate;

    @Column(name = "operation_start_time")
    private LocalTime operationStartTime;

    @Column(name = "operation_end_time")
    private LocalTime operationEndTime;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "followup_date")
    private LocalDate followupDate;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "createdby_name")
    private String createdByName;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "operative_notes", columnDefinition = "TEXT")
    private String operativeNotes;

    @Column(name = "remark")
    private String remark;

    @Column(name = "follow_up_comments")
    private String followUpComments;

    @Column(name = "anesthesia")
    private String anesthesia;

    @Column(name = "reasonfordischarge")
    private String reasonForDischarge;

    @Column(name = "footer", columnDefinition = "TEXT")
    private String footer;

    @Column(name = "referred_doctor")
    private String referredDoctor;

    @Column(name = "condition_discharge")
    private String conditionDischarge;

    @Column(name = "printed_on_date")
    private String printedOnDate;

    @Column(name = "printed_on_date_op")
    private String printedOnDateOp;

    @Column(name = "bedno")
    private String bedNo;

    @Column(name = "room", columnDefinition = "TEXT")
    private String room;

    @Column(name = "admitted_days")
    private String admittedDays;

    @Column(name = "ot_hours")
    private String otHours;

    @Column(name = "company", columnDefinition = "TEXT")
    private String company;

    @Column(name = "modifiedby_name")
    private String modifiedByName;

    @Column(name = "treating_doctor")
    private String treatingDoctor;

    @Column(name = "consulting_doctor")
    private String consultingDoctor;

    @Column(name = "ipd_no")
    private String ipdNo;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "complaints", columnDefinition = "TEXT")
    private String complaints;

    @Column(name = "history", columnDefinition = "TEXT")
    private String history;

    @Column(name = "investigations", columnDefinition = "TEXT")
    private String investigations;

    @Column(name = "oe", columnDefinition = "TEXT")
    private String oe;

    @Column(name = "se", columnDefinition = "TEXT")
    private String se;

    @Column(name = "procedure", columnDefinition = "TEXT")
    private String procedure;

    @Column(name = "treatment", columnDefinition = "TEXT")
    private String treatment;

    @Column(name = "discharge", columnDefinition = "TEXT")
    private String discharge;
}
