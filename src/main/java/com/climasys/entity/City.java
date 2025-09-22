package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "city_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    
    @EmbeddedId
    private CityId id;
}

