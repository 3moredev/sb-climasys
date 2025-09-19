package com.climasys.entity;

import java.io.Serializable;
import java.util.Objects;

public class ClinicId implements Serializable {
    private String doctorId;
    private String clinicId;

    public ClinicId() {}

    public ClinicId(String doctorId, String clinicId) {
        this.doctorId = doctorId;
        this.clinicId = clinicId;
    }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClinicId clinicId1 = (ClinicId) o;
        return Objects.equals(doctorId, clinicId1.doctorId) &&
               Objects.equals(clinicId, clinicId1.clinicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, clinicId);
    }
}
