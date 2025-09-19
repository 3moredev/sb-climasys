package com.climasys.repository;

import com.climasys.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findByMedicineName(String medicineName);
    boolean existsByMedicineName(String medicineName);
}
