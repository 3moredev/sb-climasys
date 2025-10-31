package com.climasys.repository;

import com.climasys.entity.Medicine;
import com.climasys.entity.MedicineId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitPrescriptionOverwriteRepository extends JpaRepository<Medicine, MedicineId> {
    
    @Query(value = """
        SELECT COALESCE(MAX(sequence_id), 0) + 1 
        FROM visit_prescription_overwrite
        """, nativeQuery = true)
    Optional<Integer> getNextSequenceId();
}

