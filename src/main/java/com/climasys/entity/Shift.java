package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "shift_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    
    @Id
    @Column(name = "shift_id")
    private Short shiftId;
    
    @Column(name = "description", length = 30, nullable = false)
    private String description;
    
    @Column(name = "shift_day", length = 15, nullable = false)
    private String shiftDay;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    
    @Column(name = "createdby_name", length = 90)
    private String createdbyName;
    
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    
    @Column(name = "modifiedby_name", length = 90)
    private String modifiedbyName;
}
