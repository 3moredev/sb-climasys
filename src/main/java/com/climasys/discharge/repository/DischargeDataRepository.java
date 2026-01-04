package com.climasys.discharge.repository;

import com.climasys.entity.DischargeData;
import com.climasys.entity.DischargeDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DischargeDataRepository extends JpaRepository<DischargeData, DischargeDataId> {

    Optional<DischargeData> findByPatientIdAndIpdRefno(String patientId, String ipdRefno);
}
