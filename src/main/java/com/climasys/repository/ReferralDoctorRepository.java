package com.climasys.repository;

import com.climasys.entity.ReferralDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferralDoctorRepository extends JpaRepository<ReferralDoctor, Integer> {
    
    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.languageId = :languageId ORDER BY rd.doctorName")
    List<ReferralDoctor> findByLanguageId(@Param("languageId") Integer languageId);
    
    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.doctorMob = :mobile")
    List<ReferralDoctor> findByDoctorMob(@Param("mobile") String mobile);
    
    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.doctorName LIKE %:searchStr% ORDER BY rd.doctorName")
    List<ReferralDoctor> findByDoctorNameContaining(@Param("searchStr") String searchStr);
    
    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.rdId = :rdId AND rd.languageId = :languageId")
    ReferralDoctor findByRdIdAndLanguageId(@Param("rdId") Integer rdId, @Param("languageId") Integer languageId);
}
