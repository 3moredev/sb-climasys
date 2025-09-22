package com.climasys.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AreaId implements Serializable {
    
    private Integer id;
    private String cityId;
    private String stateId;
    private String countryId;
}
