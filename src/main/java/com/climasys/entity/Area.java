package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "area_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AreaId.class)
public class Area {

    @Id
    @Column(name = "id")
    private Integer id;

    @Id
    @Column(name = "city_id", length = 6, nullable = false)
    private String cityId;

    @Id
    @Column(name = "state_id", length = 6, nullable = false)
    private String stateId;

    @Id
    @Column(name = "country_id", length = 6, nullable = false)
    private String countryId;

    @Column(name = "is_activate")
    private Boolean isActivate = true;

    // Fetch area name from translations for default language (1)
    // using @Formula strictly for reading.
    @org.hibernate.annotations.Formula("(select t.area_name from area_translations t where t.area_id = id and t.city_id = city_id and t.state_id = state_id and t.country_id = country_id and t.language_id = 1)")
    private String name;
}
