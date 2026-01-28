package com.climasys.repository;

import com.climasys.entity.ReferralDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferralDoctorRepository extends JpaRepository<ReferralDoctor, Integer> {

    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.languageId = :languageId AND rd.clinicId = :clinicId AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL) ORDER BY rd.doctorName")
    List<ReferralDoctor> findByLanguageIdAndClinicId(@Param("languageId") Integer languageId,
            @Param("clinicId") String clinicId);

    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.languageId = :languageId AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL) ORDER BY rd.doctorName")
    List<ReferralDoctor> findByLanguageId(@Param("languageId") Integer languageId);

    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.doctorMob = :mobile AND rd.clinicId = :clinicId AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL)")
    List<ReferralDoctor> findByDoctorMobAndClinicId(@Param("mobile") String mobile, @Param("clinicId") String clinicId);

    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.doctorMob = :mobile AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL)")
    List<ReferralDoctor> findByDoctorMob(@Param("mobile") String mobile);

    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.doctorName LIKE %:searchStr% AND rd.clinicId = :clinicId AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL) ORDER BY rd.doctorName")
    List<ReferralDoctor> findByDoctorNameContainingAndClinicId(@Param("searchStr") String searchStr,
            @Param("clinicId") String clinicId);

    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.doctorName LIKE %:searchStr% AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL) ORDER BY rd.doctorName")
    List<ReferralDoctor> findByDoctorNameContaining(@Param("searchStr") String searchStr);

    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.rdId = :rdId AND rd.languageId = :languageId AND rd.clinicId = :clinicId AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL)")
    ReferralDoctor findByRdIdAndLanguageIdAndClinicId(@Param("rdId") Integer rdId,
            @Param("languageId") Integer languageId, @Param("clinicId") String clinicId);

    @Query("SELECT rd FROM ReferralDoctor rd WHERE rd.rdId = :rdId AND rd.languageId = :languageId AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL)")
    ReferralDoctor findByRdIdAndLanguageId(@Param("rdId") Integer rdId, @Param("languageId") Integer languageId);

    @Query("SELECT rd FROM ReferralDoctor rd WHERE LOWER(rd.doctorName) = LOWER(:doctorName) AND rd.clinicId = :clinicId AND (rd.deleteFlag = false OR rd.deleteFlag IS NULL)")
    List<ReferralDoctor> findByDoctorNameIgnoreCaseAndClinicId(@Param("doctorName") String doctorName,
            @Param("clinicId") String clinicId);

    /**
     * Reset the sequence to the correct value (max rd_id + 1)
     * This fixes sequence synchronization issues
     */
    @Modifying
    @Query(value = "SELECT setval('referrel_doctors_list_rd_id_seq', (SELECT COALESCE(MAX(rd_id), 0) + 1 FROM referrel_doctors_list))", nativeQuery = true)
    void resetSequence();
}
