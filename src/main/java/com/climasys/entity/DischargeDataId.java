package com.climasys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite primary key for DischargeData entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DischargeDataId implements Serializable {
    private String patientId;
    private String doctorId;
    private String clinicId;
    private String ipdRefno;
}
