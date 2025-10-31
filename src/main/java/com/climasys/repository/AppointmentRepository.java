package com.climasys.repository;

import com.climasys.entity.PatientVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<PatientVisit, Long> {
    
    // Basic queries
    Optional<PatientVisit> findByPatientVisitNo(Integer patientVisitNo);
    boolean existsByPatientVisitNo(Integer patientVisitNo);
    
    // Find future appointments for a doctor
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.doctorId = :doctorId AND pv.visitDate >= :date")
    List<PatientVisit> findByDoctorIdAndVisitDateAfter(@Param("doctorId") String doctorId, @Param("date") LocalDateTime date);
    
    // Find appointments for a doctor on a specific date
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.doctorId = :doctorId AND pv.visitDate = :date")
    List<PatientVisit> findByDoctorIdAndVisitDate(@Param("doctorId") String doctorId, @Param("date") LocalDateTime date);
    
    // Find appointments for a doctor between two dates
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.doctorId = :doctorId AND pv.visitDate BETWEEN :startDate AND :endDate")
    List<PatientVisit> findByDoctorIdAndVisitDateBetween(@Param("doctorId") String doctorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // USP_Get_FutureAppointments_All_New equivalent
    @Query("SELECT pv FROM PatientVisit pv " +
           "INNER JOIN Patient pm ON pv.patientId = pm.id " +
           "INNER JOIN DoctorMaster dm ON pv.doctorId = dm.doctorId " +
           "INNER JOIN GenderTranslations gt ON CAST(pm.genderId AS short) = gt.genderId " +
           "LEFT JOIN StatusRef sr ON pv.statusId = sr.id AND pv.clinicId = sr.clinicId " +
           "LEFT JOIN FollowUpType fu ON pv.followUpType = fu.id " +
           "WHERE pv.deleteFlag = false " +
           "AND pv.clinicId = :clinicId " +
           "AND pv.visitDate >= :futureDate " +
           "AND pv.statusId NOT IN (CAST(1 AS short), CAST(2 AS short), CAST(4 AS short)) " +
           "AND gt.languageId = :languageId " +
           "ORDER BY pv.visitDate ASC, pv.visitTime ASC")
    List<PatientVisit> getFutureAppointmentsAllNew(@Param("clinicId") String clinicId, 
                                                   @Param("futureDate") LocalDateTime futureDate, 
                                                   @Param("languageId") Integer languageId);
    
    // USP_Get_FutureAppointments_ForGivenDate equivalent
    @Query("SELECT pv FROM PatientVisit pv " +
           "INNER JOIN Patient pm ON pv.patientId = pm.id " +
           "INNER JOIN DoctorMaster dm ON pv.doctorId = dm.doctorId " +
           "INNER JOIN GenderTranslations gt ON CAST(pm.genderId AS short) = gt.genderId " +
           "LEFT JOIN StatusRef sr ON pv.statusId = sr.id AND pv.clinicId = sr.clinicId " +
           "LEFT JOIN FollowUpType fu ON pv.followUpType = fu.id " +
           "WHERE pv.deleteFlag = false " +
           "AND pv.doctorId = :doctorId " +
           "AND pv.clinicId = :clinicId " +
           "AND pv.visitDate = :futureDate " +
           "AND pv.statusId NOT IN (CAST(1 AS short), CAST(2 AS short), CAST(4 AS short)) " +
           "AND gt.languageId = :languageId " +
           "ORDER BY pv.visitTime ASC")
    List<PatientVisit> getFutureAppointmentsForGivenDate(@Param("doctorId") String doctorId,
                                                         @Param("clinicId") String clinicId,
                                                         @Param("futureDate") LocalDateTime futureDate,
                                                         @Param("languageId") Integer languageId);
    
    // USP_Get_TodaysAppointments_ForGivenDate equivalent
    @Query("SELECT pv FROM PatientVisit pv " +
           "INNER JOIN Patient pm ON pv.patientId = pm.id " +
           "INNER JOIN DoctorMaster dm ON pv.doctorId = dm.doctorId " +
           "INNER JOIN GenderTranslations gt ON CAST(pm.genderId AS short) = gt.genderId " +
           "LEFT JOIN StatusRef sr ON pv.statusId = sr.id AND pv.clinicId = sr.clinicId " +
           "LEFT JOIN FollowUpType fu ON pv.followUpType = fu.id " +
           "WHERE pv.deleteFlag = false " +
           "AND pv.doctorId = :doctorId " +
           "AND pv.clinicId = :clinicId " +
           "AND pv.visitDate = :visitDate " +
           "AND pv.statusId NOT IN (CAST(1 AS short), CAST(2 AS short), CAST(4 AS short)) " +
           "AND gt.languageId = :languageId " +
           "ORDER BY pv.visitTime ASC")
    List<PatientVisit> getTodaysAppointmentsForGivenDate(@Param("doctorId") String doctorId,
                                                         @Param("clinicId") String clinicId,
                                                         @Param("visitDate") LocalDateTime visitDate,
                                                         @Param("languageId") Integer languageId);
    
    // Find appointments by patient ID
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.patientId = :patientId AND pv.deleteFlag = false ORDER BY pv.visitDate DESC")
    List<PatientVisit> findByPatientIdAndActive(@Param("patientId") String patientId);
    
    // Find appointments by clinic and date range
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.clinicId = :clinicId AND pv.visitDate BETWEEN :startDate AND :endDate AND pv.deleteFlag = false ORDER BY pv.visitDate ASC, pv.visitTime ASC")
    List<PatientVisit> findByClinicIdAndVisitDateBetween(@Param("clinicId") String clinicId,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
    
    // Check for appointment conflicts - same patient, same doctor, same date/shift, specific statuses
    @Query("SELECT COUNT(pv) FROM PatientVisit pv WHERE pv.doctorId = :doctorId AND pv.visitDate = :visitDate AND pv.patientId = :patientId AND pv.deleteFlag = false AND pv.statusId IN (1, 11)")
    Long countConflictingAppointments(@Param("doctorId") String doctorId,
                                      @Param("visitDate") LocalDateTime visitDate,
                                      @Param("patientId") String patientId);
    
    // Get next patient visit number for a patient
    @Query("SELECT COALESCE(MAX(pv.patientVisitNo), 0) + 1 FROM PatientVisit pv WHERE pv.patientId = :patientId")
    Integer getNextPatientVisitNo(@Param("patientId") String patientId);
    
    // Find appointments by patient, doctor, and date (ignoring time)
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.patientId = :patientId AND pv.doctorId = :doctorId AND CAST(pv.visitDate AS date) = CAST(:visitDate AS date) AND pv.deleteFlag = false")
    List<PatientVisit> findAppointmentsByPatientDoctorAndDate(@Param("patientId") String patientId,
                                                              @Param("doctorId") String doctorId,
                                                              @Param("visitDate") LocalDateTime visitDate);
    
    // Find appointments by patient, doctor, and exact datetime (since DB stores both date and time in UTC)
    @Query("SELECT pv FROM PatientVisit pv WHERE pv.patientId = :patientId AND pv.doctorId = :doctorId AND pv.visitDate = :visitDate AND pv.deleteFlag = false")
    List<PatientVisit> findAppointmentsByPatientDoctorAndExactDateTime(@Param("patientId") String patientId,
                                                                       @Param("doctorId") String doctorId,
                                                                       @Param("visitDate") LocalDateTime visitDate);
    
    // Soft delete appointment by exact datetime match
    @Modifying
    @Transactional
    @Query("UPDATE PatientVisit pv SET pv.deleteFlag = true, pv.modifiedOn = :modifiedOn, pv.modifiedbyName = :modifiedBy WHERE pv.patientId = :patientId AND pv.visitDate = :visitDate AND pv.doctorId = :doctorId AND pv.clinicId = :clinicId")
    int softDeleteAppointment(@Param("patientId") String patientId,
                              @Param("visitDate") LocalDateTime visitDate,
                              @Param("doctorId") String doctorId,
                              @Param("clinicId") String clinicId,
                              @Param("modifiedOn") LocalDateTime modifiedOn,
                              @Param("modifiedBy") String modifiedBy);
    
    // Soft delete appointment by date (ignoring time)
    @Modifying
    @Transactional
    @Query("UPDATE PatientVisit pv SET pv.deleteFlag = true, pv.modifiedOn = :modifiedOn, pv.modifiedbyName = :modifiedBy WHERE pv.patientId = :patientId AND pv.doctorId = :doctorId AND pv.clinicId = :clinicId AND CAST(pv.visitDate AS date) = CAST(:visitDate AS date) AND pv.deleteFlag = false")
    int softDeleteAppointmentByDate(@Param("patientId") String patientId,
                                    @Param("visitDate") LocalDateTime visitDate,
                                    @Param("doctorId") String doctorId,
                                    @Param("clinicId") String clinicId,
                                    @Param("modifiedOn") LocalDateTime modifiedOn,
                                    @Param("modifiedBy") String modifiedBy);
    
    // Soft delete appointment by date and time (matching separate date and time fields)
    @Modifying
    @Transactional
    @Query("UPDATE PatientVisit pv SET pv.deleteFlag = true, pv.modifiedOn = :modifiedOn, pv.modifiedbyName = :modifiedBy WHERE pv.patientId = :patientId AND pv.doctorId = :doctorId AND pv.clinicId = :clinicId AND CAST(pv.visitDate AS date) = :visitDate AND pv.visitTime = :visitTime AND pv.deleteFlag = false")
    int softDeleteAppointmentByDateAndTime(@Param("patientId") String patientId,
                                           @Param("visitDate") java.time.LocalDate visitDate,
                                           @Param("visitTime") java.sql.Time visitTime,
                                           @Param("doctorId") String doctorId,
                                           @Param("clinicId") String clinicId,
                                           @Param("modifiedOn") LocalDateTime modifiedOn,
                                           @Param("modifiedBy") String modifiedBy);
    
    // Update appointment status
    @Modifying
    @Transactional
    @Query("UPDATE PatientVisit pv SET pv.statusId = :statusId, pv.modifiedOn = :modifiedOn, pv.modifiedbyName = :modifiedBy WHERE pv.patientId = :patientId AND pv.visitDate = :visitDate AND pv.doctorId = :doctorId")
    int updateAppointmentStatus(@Param("patientId") String patientId,
                                @Param("visitDate") LocalDateTime visitDate,
                                @Param("doctorId") String doctorId,
                                @Param("statusId") Short statusId,
                                @Param("modifiedOn") LocalDateTime modifiedOn,
                                @Param("modifiedBy") String modifiedBy);
    
    // Update appointment online time, doctor, and status
    @Modifying
    @Transactional
    @Query("UPDATE PatientVisit pv SET pv.onlineAppointmentTime = :onlineAppointmentTime, pv.doctorId = :doctorId, pv.statusId = :statusId, pv.modifiedOn = :modifiedOn, pv.modifiedbyName = :modifiedBy, pv.referId = :referId WHERE pv.patientId = :patientId AND pv.patientVisitNo = :patientVisitNo AND pv.shiftId = :shiftId AND pv.clinicId = :clinicId")
    int updateAppointmentOnlineTimeAndDoctor(@Param("patientId") String patientId,
                                           @Param("patientVisitNo") Integer patientVisitNo,
                                           @Param("shiftId") Short shiftId,
                                           @Param("clinicId") String clinicId,
                                           @Param("onlineAppointmentTime") java.sql.Time onlineAppointmentTime,
                                           @Param("doctorId") String doctorId,
                                           @Param("statusId") Short statusId,
                                           @Param("modifiedOn") LocalDateTime modifiedOn,
                                           @Param("modifiedBy") String modifiedBy,
                                           @Param("referId") String referId);
}
