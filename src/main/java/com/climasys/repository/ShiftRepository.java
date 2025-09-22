package com.climasys.repository;

import com.climasys.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Short> {
    
    @Query("SELECT s FROM Shift s ORDER BY s.shiftId")
    List<Shift> findAllOrdered();
    
    @Query("SELECT s FROM Shift s WHERE s.shiftDay = :day ORDER BY s.startTime")
    List<Shift> findByDay(@Param("day") String day);
}
