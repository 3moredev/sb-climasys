package com.climasys.repository;

import com.climasys.entity.ImpressionFinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImpressionFindingRepository extends JpaRepository<ImpressionFinding, Integer> {
    
    @Query("SELECT if FROM ImpressionFinding if WHERE if.doctorId = :doctorId ORDER BY if.sequenceNo, if.group")
    List<ImpressionFinding> findByDoctorId(@Param("doctorId") String doctorId);
    
    @Query("SELECT if FROM ImpressionFinding if ORDER BY if.sequenceNo, if.group")
    List<ImpressionFinding> findAllOrdered();
}
