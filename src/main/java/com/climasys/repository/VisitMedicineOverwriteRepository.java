package com.climasys.repository;

import com.climasys.entity.VisitMedicineOverwrite;
import com.climasys.entity.VisitMedicineOverwriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitMedicineOverwriteRepository extends JpaRepository<VisitMedicineOverwrite, VisitMedicineOverwriteId> {
}

