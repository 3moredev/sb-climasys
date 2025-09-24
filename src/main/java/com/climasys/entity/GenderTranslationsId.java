package com.climasys.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenderTranslationsId implements Serializable {
    
    private Short genderId;
    private Integer languageId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenderTranslationsId that = (GenderTranslationsId) o;
        return genderId.equals(that.genderId) && languageId.equals(that.languageId);
    }
    
    @Override
    public int hashCode() {
        return genderId.hashCode() + languageId.hashCode();
    }
}
