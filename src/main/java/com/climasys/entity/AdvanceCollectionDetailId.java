package com.climasys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Composite primary key for AdvanceCollectionDetail entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceCollectionDetailId implements Serializable {
    private String patientId;
    private String doctorId;
    private String clinicId;
    private String ipdRefno;
    private LocalDateTime date;
}

