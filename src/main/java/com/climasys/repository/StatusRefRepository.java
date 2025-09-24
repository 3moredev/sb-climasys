package com.climasys.repository;

import com.climasys.entity.StatusRef;
import com.climasys.entity.StatusRefId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRefRepository extends JpaRepository<StatusRef, StatusRefId> {
    
    @Query("SELECT s FROM StatusRef s WHERE s.id = :id AND s.clinicId = :clinicId AND (s.deleteFlag = false OR s.deleteFlag IS NULL)")
    Optional<StatusRef> findByIdAndClinicIdAndActive(@Param("id") Short id, @Param("clinicId") String clinicId);
    
    @Query("SELECT s FROM StatusRef s WHERE s.clinicId = :clinicId AND (s.deleteFlag = false OR s.deleteFlag IS NULL)")
    List<StatusRef> findByClinicIdAndActive(@Param("clinicId") String clinicId);
    
    @Query("SELECT s FROM StatusRef s WHERE s.statusCode = :statusCode AND s.clinicId = :clinicId AND (s.deleteFlag = false OR s.deleteFlag IS NULL)")
    Optional<StatusRef> findByStatusCodeAndClinicIdAndActive(@Param("statusCode") String statusCode, @Param("clinicId") String clinicId);
}
