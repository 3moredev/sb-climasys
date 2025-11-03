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

    @Query(value = "SELECT igm.group_description, igm.priority_value, gi.instructions_description\n"
            + "FROM instructions_group_master igm\n"
            + "LEFT JOIN group_instructions gi ON igm.doctor_id = gi.doctor_id AND igm.group_description = gi.group_description\n"
            + "WHERE igm.doctor_id = :doctorId AND igm.clinic_id = :clinicId\n"
            + "ORDER BY igm.priority_value, igm.group_description ASC, COALESCE(gi.sequence_no, 999999) ASC, gi.instructions_description ASC", nativeQuery = true)
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

    /**
     * JPA equivalent to USP_Search_PrescriptionForPatientProfile
     * First result set: Filtered by doctor_id and clinic_id
     * Note: The searchStr parameter is expected to already contain % wildcards (matching WebService preprocessing)
     * The stored procedure doesn't use clinic_id, but we add it for multi-clinic support and consistency with other methods
     */
    @Query(value = "SELECT COALESCE(medicine_name,'') || '        |        ' || COALESCE(brand_name,'') || '        |        ' ||\n"
            + "       COALESCE(CAST(morning AS varchar(10)), '') || '-' || COALESCE(CAST(afternoon AS varchar(10)), '') || '-' || COALESCE(CAST(night AS varchar(10)), '') ||\n"
            + "       '        |       ' || COALESCE(CAST(no_of_days AS varchar(10)), '') || '        |        ' || COALESCE(instruction,'') AS search_value\n"
            + "FROM prescription_medicines p\n"
            + "WHERE (\n"
            + "    p.medicine_name LIKE '%' || :searchStr || '%'\n"
            + "    OR p.brand_name LIKE '%' || :searchStr || '%'\n"
            + "    OR (COALESCE(p.brand_name,'') || '        |        ' || COALESCE(p.medicine_name,'')) LIKE '%' || :searchStr || '%'\n"
            + ")\n"
            + "AND p.active = true\n"
            + "AND p.doctor_id = :doctorId\n"
            + "AND p.clinic_id = :clinicId\n"
            + "ORDER BY p.priority_value, p.medicine_name ASC", nativeQuery = true)
    List<String> searchPrescriptionForPatientProfileWithDoctor(@Param("searchStr") String searchStr, @Param("doctorId") String doctorId, @Param("clinicId") String clinicId);

    /**
     * JPA equivalent to USP_Search_PrescriptionForPatientProfile
     * Second result set: All active prescriptions (without doctor filter, but with clinic filter for multi-clinic support)
     * Note: The searchStr parameter is expected to already contain % wildcards (matching WebService preprocessing)
     * Added clinic_id filter for consistency with other methods and proper multi-clinic support
     */
    @Query(value = "SELECT COALESCE(medicine_name,'') || '        |        ' || COALESCE(brand_name,'') || '        |        ' ||\n"
            + "       COALESCE(CAST(morning AS varchar(10)), '') || '-' || COALESCE(CAST(afternoon AS varchar(10)), '') || '-' || COALESCE(CAST(night AS varchar(10)), '') ||\n"
            + "       '        |        ' || COALESCE(CAST(no_of_days AS varchar(10)), '') || '        |        ' || COALESCE(instruction,'') AS search_value\n"
            + "FROM prescription_medicines p\n"
            + "WHERE (\n"
            + "    p.medicine_name LIKE '%' || :searchStr || '%'\n"
            + "    OR p.brand_name LIKE '%' || :searchStr || '%'\n"
            + "    OR (COALESCE(p.brand_name,'') || '        |        ' || COALESCE(p.medicine_name,'')) LIKE '%' || :searchStr || '%'\n"
            + ")\n"
            + "AND p.active = true\n"
            + "AND p.clinic_id = :clinicId\n"
            + "ORDER BY p.priority_value, p.medicine_name ASC", nativeQuery = true)
    List<String> searchPrescriptionForPatientProfileAll(@Param("searchStr") String searchStr, @Param("clinicId") String clinicId);

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


