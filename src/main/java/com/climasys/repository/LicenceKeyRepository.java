package com.climasys.repository;

import com.climasys.entity.LicenceKey;
import com.climasys.entity.ClinicDoctorMasterId;
import com.climasys.entity.ClinicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenceKeyRepository extends JpaRepository<LicenceKey, ClinicDoctorMasterId> {
    List<LicenceKey> findByClinicIdIn(List<String> clinicIds);
}
