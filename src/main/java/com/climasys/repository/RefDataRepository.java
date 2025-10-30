package com.climasys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefDataRepository extends JpaRepository<com.climasys.entity.Patient, String> {

    @Query(value = "SELECT short_description, medicine_description, priority_value\n"
            + "FROM medicine_master\n"
            + "WHERE active = true AND doctor_id = :doctorId AND clinic_id = :clinicId\n"
            + "ORDER BY priority_value, short_description ASC", nativeQuery = true)
    List<Object[]> findMedicineMaster(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    @Query(value = "SELECT short_description, complaint_description, priority_value\n"
            + "FROM complaint_master\n"
            + "WHERE doctor_id = :doctorId AND clinic_id = :clinicId\n"
            + "ORDER BY priority_value, short_description ASC", nativeQuery = true)
    List<Object[]> findComplaints(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    @Query(value = "SELECT short_description, dressing_description, priority_value\n"
            + "FROM dressing_master\n"
            + "WHERE doctor_id = :doctorId AND clinic_id = :clinicId\n"
            + "ORDER BY priority_value, short_description ASC", nativeQuery = true)
    List<Object[]> findDressings(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    @Query(value = "SELECT short_description, diagnosis_description, priority_value\n"
            + "FROM diagnosis_master\n"
            + "WHERE doctor_id = :doctorId AND clinic_id = :clinicId\n"
            + "ORDER BY priority_value, short_description ASC", nativeQuery = true)
    List<Object[]> findDiagnosis(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    @Query(value = "SELECT medicine_name, brand_name, cat_short_name, catsub_description, priority_value\n"
            + "FROM prescription_medicines\n"
            + "WHERE active = true AND doctor_id = :doctorId AND clinic_id = :clinicId\n"
            + "ORDER BY priority_value, medicine_name ASC", nativeQuery = true)
    List<Object[]> findPrescriptionMedicines(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    @Query(value = "SELECT DISTINCT dpm.procedure_description, dpm.priority_value\n"
            + "FROM doctor_procedure_master dpm\n"
            + "JOIN doctor_procedure_findings dpf ON dpm.procedure_description = dpf.procedure_description\n"
            + "WHERE dpm.doctor_id = :doctorId AND dpf.doctor_id = :doctorId AND dpm.clinic_id = :clinicId\n"
            + "ORDER BY dpm.priority_value, dpm.procedure_description ASC", nativeQuery = true)
    List<Object[]> findProcedures(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    @Query(value = "SELECT group_description, priority_value\n"
            + "FROM instructions_group_master\n"
            + "WHERE doctor_id = :doctorId AND clinic_id = :clinicId\n"
            + "ORDER BY priority_value, group_description ASC", nativeQuery = true)
    List<Object[]> findInstructionGroups(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    @Query(value = "SELECT short_description, complaint_description, priority_value\n"
            + "FROM complaint_master\n"
            + "WHERE doctor_id = :doctorId AND COALESCE(display_to_operator, 0) = 1 AND clinic_id = :clinicId\n"
            + "ORDER BY priority_value, short_description ASC", nativeQuery = true)
    List<Object[]> findOperatorComplaints(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    @Query(value = "SELECT COALESCE(brand_name,'') || '   :   ' || COALESCE(medicine_name,'') || '   :   ' ||\n"
            + "       COALESCE(CAST(morning AS varchar), '') || '-' || COALESCE(CAST(afternoon AS varchar), '') || '-' || COALESCE(CAST(night AS varchar), '') ||\n"
            + "       '   :   ' || COALESCE(CAST(no_of_days AS varchar), '') || '   :   ' || COALESCE(instruction,'') AS search_value\n"
            + "FROM prescription_medicines\n"
            + "WHERE active = true AND doctor_id = :doctorId AND clinic_id = :clinicId\n"
            + "ORDER BY priority_value, medicine_name ASC", nativeQuery = true)
    List<String> buildPrescriptionSearch(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    // For USP_Get_SymptomData billing details
    @Query(value = "SELECT bdm.billing_details, bdm.billing_group_name, bdm.billing_subgroup_name, bdm.default_fees,\n"
            + "       bdm.visit_type, bvt.billing_visittype_description, bvt.billing_visittype_id,\n"
            + "       COALESCE(bdm.isdefault, false) AS isdefault, COALESCE(bdm.sequence_no, 0) AS sequence_no\n"
            + "FROM billing_details_master bdm\n"
            + "JOIN billing_visittype_translations bvt ON bdm.visit_type = bvt.billing_visittype_id\n"
            + "WHERE bdm.doctor_id = :doctorId AND bdm.clinic_id = :clinicId\n"
            + "ORDER BY bdm.sequence_no ASC", nativeQuery = true)
    List<Object[]> findBillingDetailsForDoctor(@Param("doctorId") String doctorId, @Param("clinicId") String clinicId);
}


