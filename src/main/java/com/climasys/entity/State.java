package com.climasys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "state_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {
    
    @EmbeddedId
    private StateId id;
}

