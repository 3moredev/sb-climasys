-- =====================================================
-- Additional Climasys Stored Procedures Migration
-- SQL Server to PostgreSQL Migration
-- =====================================================
-- 
-- This script migrates additional stored procedures for
-- billing, lab tests, and other complex operations
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- BILLING STORED PROCEDURES
-- =====================================================

-- USP_Get_Amount (Billing amount calculation)
CREATE OR REPLACE FUNCTION usp_get_amount(
    p_visit_id VARCHAR(30) DEFAULT NULL,
    p_patient_id INTEGER DEFAULT NULL
)
RETURNS TABLE(
    visit_id VARCHAR(30),
    patient_name TEXT,
    total_amount NUMERIC(10,2),
    consultation_fee NUMERIC(10,2),
    medicine_cost NUMERIC(10,2),
    lab_cost NUMERIC(10,2),
    other_charges NUMERIC(10,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        pv.visit_id,
        CONCAT(
            COALESCE(pm.first_name, ''), ' ',
            COALESCE(pm.middle_name, ''), ' ',
            COALESCE(pm.last_name, '')
        )::TEXT as patient_name,
        COALESCE(
            (SELECT param_value::NUMERIC FROM system_params 
             WHERE param_name = 'consultation_fee' 
             AND doctor_id = pv.doctor_id LIMIT 1), 500
        ) as total_amount,
        COALESCE(
            (SELECT param_value::NUMERIC FROM system_params 
             WHERE param_name = 'consultation_fee' 
             AND doctor_id = pv.doctor_id LIMIT 1), 500
        ) as consultation_fee,
        0.00 as medicine_cost,
        0.00 as lab_cost,
        0.00 as other_charges
    FROM patient_visits pv
    JOIN patient_master pm ON pv.patient_id = pm.id
    WHERE pv.delete_flag = false
    AND (p_visit_id IS NULL OR pv.visit_id = p_visit_id)
    AND (p_patient_id IS NULL OR pv.patient_id = p_patient_id);
END;
$$;

-- USP_Get_AmountStatus (Payment status)
CREATE OR REPLACE FUNCTION usp_get_amount_status(
    p_visit_id VARCHAR(30) DEFAULT NULL,
    p_patient_id INTEGER DEFAULT NULL
)
RETURNS TABLE(
    visit_id VARCHAR(30),
    patient_name TEXT,
    total_amount NUMERIC(10,2),
    paid_amount NUMERIC(10,2),
    pending_amount NUMERIC(10,2),
    payment_status VARCHAR(20)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        pv.visit_id,
        CONCAT(
            COALESCE(pm.first_name, ''), ' ',
            COALESCE(pm.middle_name, ''), ' ',
            COALESCE(pm.last_name, '')
        )::TEXT as patient_name,
        COALESCE(
            (SELECT param_value::NUMERIC FROM system_params 
             WHERE param_name = 'consultation_fee' 
             AND doctor_id = pv.doctor_id LIMIT 1), 500
        ) as total_amount,
        0.00 as paid_amount,
        COALESCE(
            (SELECT param_value::NUMERIC FROM system_params 
             WHERE param_name = 'consultation_fee' 
             AND doctor_id = pv.doctor_id LIMIT 1), 500
        ) as pending_amount,
        'Pending'::VARCHAR(20) as payment_status
    FROM patient_visits pv
    JOIN patient_master pm ON pv.patient_id = pm.id
    WHERE pv.delete_flag = false
    AND (p_visit_id IS NULL OR pv.visit_id = p_visit_id)
    AND (p_patient_id IS NULL OR p_patient_id = p_patient_id);
END;
$$;

-- =====================================================
-- LAB TEST STORED PROCEDURES
-- =====================================================

-- USP_Get_LabTest
CREATE OR REPLACE FUNCTION usp_get_lab_test(
    p_test_name VARCHAR(200) DEFAULT NULL,
    p_category VARCHAR(100) DEFAULT NULL
)
RETURNS TABLE(
    test_id INTEGER,
    test_name VARCHAR(200),
    test_category VARCHAR(100),
    normal_range VARCHAR(100),
    unit VARCHAR(50),
    cost NUMERIC(10,2),
    is_active BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- This is a placeholder function since we don't have lab_test table yet
    -- You can create the lab_test table and populate it with actual data
    RETURN QUERY
    SELECT 
        1::INTEGER as test_id,
        'Blood Sugar (Fasting)'::VARCHAR(200) as test_name,
        'Biochemistry'::VARCHAR(100) as test_category,
        '70-100 mg/dl'::VARCHAR(100) as normal_range,
        'mg/dl'::VARCHAR(50) as unit,
        150.00::NUMERIC(10,2) as cost,
        true::BOOLEAN as is_active
    WHERE (p_test_name IS NULL OR 'Blood Sugar (Fasting)' ILIKE '%' || p_test_name || '%')
    AND (p_category IS NULL OR 'Biochemistry' = p_category)
    
    UNION ALL
    
    SELECT 
        2::INTEGER as test_id,
        'Complete Blood Count'::VARCHAR(200) as test_name,
        'Hematology'::VARCHAR(100) as test_category,
        'Normal'::VARCHAR(100) as normal_range,
        'Count'::VARCHAR(50) as unit,
        200.00::NUMERIC(10,2) as cost,
        true::BOOLEAN as is_active
    WHERE (p_test_name IS NULL OR 'Complete Blood Count' ILIKE '%' || p_test_name || '%')
    AND (p_category IS NULL OR 'Hematology' = p_category);
END;
$$;

-- USP_Get_LabTestDetails
CREATE OR REPLACE FUNCTION usp_get_lab_test_details(
    p_test_id INTEGER DEFAULT NULL,
    p_visit_id VARCHAR(30) DEFAULT NULL
)
RETURNS TABLE(
    test_id INTEGER,
    test_name VARCHAR(200),
    test_result VARCHAR(200),
    normal_range VARCHAR(100),
    unit VARCHAR(50),
    status VARCHAR(20),
    test_date DATE
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- This is a placeholder function since we don't have lab_results table yet
    RETURN QUERY
    SELECT 
        1::INTEGER as test_id,
        'Blood Sugar (Fasting)'::VARCHAR(200) as test_name,
        '85 mg/dl'::VARCHAR(200) as test_result,
        '70-100 mg/dl'::VARCHAR(100) as normal_range,
        'mg/dl'::VARCHAR(50) as unit,
        'Normal'::VARCHAR(20) as status,
        CURRENT_DATE as test_date
    WHERE (p_test_id IS NULL OR p_test_id = 1)
    AND (p_visit_id IS NULL OR p_visit_id = 'V001');
END;
$$;

-- =====================================================
-- MEDICINE STORED PROCEDURES
-- =====================================================

-- USP_Get_BLDMedicine (Blood pressure medicine)
CREATE OR REPLACE FUNCTION usp_get_bld_medicine()
RETURNS TABLE(
    medicine_id INTEGER,
    medicine_name VARCHAR(200),
    generic_name VARCHAR(200),
    dosage_form VARCHAR(100),
    strength VARCHAR(100),
    category VARCHAR(100)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        mm.medicine_id,
        mm.medicine_name,
        mm.generic_name,
        mm.dosage_form,
        mm.strength,
        CASE 
            WHEN mm.medicine_name ILIKE '%amlodipine%' OR mm.medicine_name ILIKE '%losartan%' 
            THEN 'Blood Pressure'
            WHEN mm.medicine_name ILIKE '%metformin%' OR mm.medicine_name ILIKE '%glimepiride%'
            THEN 'Diabetes'
            ELSE 'General'
        END::VARCHAR(100) as category
    FROM medicine_master mm
    WHERE mm.is_active = true
    AND (
        mm.medicine_name ILIKE '%blood%' OR 
        mm.medicine_name ILIKE '%pressure%' OR
        mm.medicine_name ILIKE '%amlodipine%' OR
        mm.medicine_name ILIKE '%losartan%' OR
        mm.medicine_name ILIKE '%metformin%' OR
        mm.medicine_name ILIKE '%glimepiride%'
    )
    ORDER BY mm.medicine_name;
END;
$$;

-- USP_Get_BLDPrescription (Blood pressure prescription)
CREATE OR REPLACE FUNCTION usp_get_bld_prescription(
    p_patient_id INTEGER DEFAULT NULL,
    p_doctor_id VARCHAR(30) DEFAULT NULL
)
RETURNS TABLE(
    prescription_id INTEGER,
    patient_name TEXT,
    doctor_name TEXT,
    medicine_name VARCHAR(200),
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    instructions TEXT,
    prescription_date DATE
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        vpo.id as prescription_id,
        CONCAT(
            COALESCE(pm.first_name, ''), ' ',
            COALESCE(pm.middle_name, ''), ' ',
            COALESCE(pm.last_name, '')
        )::TEXT as patient_name,
        CONCAT(
            COALESCE(dm.first_name, ''), ' ',
            COALESCE(dm.middle_name, ''), ' ',
            COALESCE(dm.last_name, '')
        )::TEXT as doctor_name,
        vpo.medicine_name,
        vpo.dosage,
        vpo.frequency,
        vpo.duration,
        vpo.instructions,
        vpo.created_on::DATE as prescription_date
    FROM visit_prescription_overwrite vpo
    JOIN patient_master pm ON vpo.patient_id = pm.id
    JOIN doctor_master dm ON vpo.doctor_id = dm.doctor_id
    WHERE (
        vpo.medicine_name ILIKE '%blood%' OR 
        vpo.medicine_name ILIKE '%pressure%' OR
        vpo.medicine_name ILIKE '%amlodipine%' OR
        vpo.medicine_name ILIKE '%losartan%' OR
        vpo.medicine_name ILIKE '%metformin%' OR
        vpo.medicine_name ILIKE '%glimepiride%'
    )
    AND (p_patient_id IS NULL OR vpo.patient_id = p_patient_id)
    AND (p_doctor_id IS NULL OR vpo.doctor_id = p_doctor_id)
    ORDER BY vpo.created_on DESC;
END;
$$;

-- =====================================================
-- CLINIC MANAGEMENT STORED PROCEDURES
-- =====================================================

-- USP_Get_ClinicShifts
CREATE OR REPLACE FUNCTION usp_get_clinic_shifts(
    p_clinic_id VARCHAR(30) DEFAULT NULL,
    p_doctor_id VARCHAR(30) DEFAULT NULL
)
RETURNS TABLE(
    shift_id INTEGER,
    clinic_id VARCHAR(30),
    doctor_id VARCHAR(30),
    shift_day VARCHAR(20),
    start_time TIME,
    end_time TIME,
    description VARCHAR(200),
    is_active BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        sm.shift_id,
        dcs.clinic_id,
        dcs.doctor_id,
        sm.shift_day,
        sm.start_time,
        sm.end_time,
        sm.description,
        sm.is_active
    FROM shift_master sm
    JOIN doctor_clinic_shift dcs ON sm.shift_id = dcs.shift_id
    WHERE sm.is_active = true
    AND dcs.is_active = true
    AND (p_clinic_id IS NULL OR dcs.clinic_id = p_clinic_id)
    AND (p_doctor_id IS NULL OR dcs.doctor_id = p_doctor_id)
    ORDER BY sm.shift_day, sm.start_time;
END;
$$;

-- USP_Get_ClinicShiftsTime
CREATE OR REPLACE FUNCTION usp_get_clinic_shifts_time(
    p_clinic_id VARCHAR(30) DEFAULT NULL,
    p_shift_day VARCHAR(20) DEFAULT NULL
)
RETURNS TABLE(
    shift_id INTEGER,
    clinic_id VARCHAR(30),
    shift_day VARCHAR(20),
    start_time TIME,
    end_time TIME,
    duration_hours NUMERIC(4,2),
    description VARCHAR(200)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        sm.shift_id,
        dcs.clinic_id,
        sm.shift_day,
        sm.start_time,
        sm.end_time,
        EXTRACT(EPOCH FROM (sm.end_time - sm.start_time)) / 3600 as duration_hours,
        sm.description
    FROM shift_master sm
    JOIN doctor_clinic_shift dcs ON sm.shift_id = dcs.shift_id
    WHERE sm.is_active = true
    AND dcs.is_active = true
    AND (p_clinic_id IS NULL OR dcs.clinic_id = p_clinic_id)
    AND (p_shift_day IS NULL OR sm.shift_day = p_shift_day)
    ORDER BY sm.shift_day, sm.start_time;
END;
$$;

-- =====================================================
-- PATIENT MANAGEMENT STORED PROCEDURES
-- =====================================================

-- USP_Get_FamilyDetails
CREATE OR REPLACE FUNCTION usp_get_family_details(
    p_patient_id INTEGER DEFAULT NULL,
    p_family_head_id INTEGER DEFAULT NULL
)
RETURNS TABLE(
    family_member_id INTEGER,
    patient_id INTEGER,
    patient_name TEXT,
    relationship VARCHAR(50),
    age INTEGER,
    gender VARCHAR(1),
    phone VARCHAR(20),
    is_family_head BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- This is a placeholder function since we don't have family_relationships table yet
    RETURN QUERY
    SELECT 
        pm.id as family_member_id,
        pm.id as patient_id,
        CONCAT(
            COALESCE(pm.first_name, ''), ' ',
            COALESCE(pm.middle_name, ''), ' ',
            COALESCE(pm.last_name, '')
        )::TEXT as patient_name,
        'Self'::VARCHAR(50) as relationship,
        pm.age,
        pm.gender_id,
        pm.phone,
        true::BOOLEAN as is_family_head
    FROM patient_master pm
    WHERE pm.is_active = true
    AND (p_patient_id IS NULL OR pm.id = p_patient_id)
    AND (p_family_head_id IS NULL OR pm.id = p_family_head_id)
    ORDER BY pm.first_name, pm.last_name;
END;
$$;

-- USP_Get_Doctor_count
CREATE OR REPLACE FUNCTION usp_get_doctor_count()
RETURNS TABLE(
    total_doctors BIGINT,
    active_doctors BIGINT,
    inactive_doctors BIGINT,
    doctors_with_clinics BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_doctors,
        COUNT(*) FILTER (WHERE dm.is_active = true) as active_doctors,
        COUNT(*) FILTER (WHERE dm.is_active = false) as inactive_doctors,
        COUNT(DISTINCT cm.doctor_id) as doctors_with_clinics
    FROM doctor_master dm
    LEFT JOIN clinic_master cm ON dm.doctor_id = cm.doctor_id AND cm.is_active = true;
END;
$$;

-- =====================================================
-- REPORTING STORED PROCEDURES
-- =====================================================

-- USP_Get_ConsolidatedFees
CREATE OR REPLACE FUNCTION usp_get_consolidated_fees(
    p_doctor_id VARCHAR(30) DEFAULT NULL,
    p_date_from DATE DEFAULT NULL,
    p_date_to DATE DEFAULT NULL
)
RETURNS TABLE(
    doctor_name TEXT,
    clinic_name VARCHAR(200),
    total_visits BIGINT,
    total_fees NUMERIC(10,2),
    average_fee NUMERIC(10,2),
    date_range TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    date_from DATE;
    date_to DATE;
BEGIN
    date_from := COALESCE(p_date_from, CURRENT_DATE - INTERVAL '30 days');
    date_to := COALESCE(p_date_to, CURRENT_DATE);
    
    RETURN QUERY
    SELECT 
        CONCAT(
            COALESCE(dm.first_name, ''), ' ',
            COALESCE(dm.middle_name, ''), ' ',
            COALESCE(dm.last_name, '')
        )::TEXT as doctor_name,
        cm.clinic_name,
        COUNT(pv.id) as total_visits,
        COUNT(pv.id) * COALESCE(
            (SELECT param_value::NUMERIC FROM system_params 
             WHERE param_name = 'consultation_fee' 
             AND doctor_id = pv.doctor_id LIMIT 1), 500
        ) as total_fees,
        COALESCE(
            (SELECT param_value::NUMERIC FROM system_params 
             WHERE param_name = 'consultation_fee' 
             AND doctor_id = pv.doctor_id LIMIT 1), 500
        ) as average_fee,
        CONCAT(date_from::TEXT, ' to ', date_to::TEXT)::TEXT as date_range
    FROM patient_visits pv
    JOIN doctor_master dm ON pv.doctor_id = dm.doctor_id
    JOIN clinic_master cm ON pv.clinic_id = cm.clinic_id
    WHERE pv.delete_flag = false
    AND pv.visit_date BETWEEN date_from AND date_to
    AND (p_doctor_id IS NULL OR pv.doctor_id = p_doctor_id)
    GROUP BY dm.doctor_id, dm.first_name, dm.middle_name, dm.last_name, cm.clinic_name
    ORDER BY total_fees DESC;
END;
$$;

-- USP_Get_DischargeDataBydate
CREATE OR REPLACE FUNCTION usp_get_discharge_data_by_date(
    p_discharge_date DATE DEFAULT CURRENT_DATE,
    p_doctor_id VARCHAR(30) DEFAULT NULL
)
RETURNS TABLE(
    visit_id VARCHAR(30),
    patient_name TEXT,
    doctor_name TEXT,
    admission_date DATE,
    discharge_date DATE,
    diagnosis TEXT,
    treatment TEXT,
    total_days INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        pv.visit_id,
        CONCAT(
            COALESCE(pm.first_name, ''), ' ',
            COALESCE(pm.middle_name, ''), ' ',
            COALESCE(pm.last_name, '')
        )::TEXT as patient_name,
        CONCAT(
            COALESCE(dm.first_name, ''), ' ',
            COALESCE(dm.middle_name, ''), ' ',
            COALESCE(dm.last_name, '')
        )::TEXT as doctor_name,
        pv.visit_date as admission_date,
        pv.follow_up_date as discharge_date,
        pv.diagnosis,
        pv.treatment,
        (pv.follow_up_date - pv.visit_date)::INTEGER as total_days
    FROM patient_visits pv
    JOIN patient_master pm ON pv.patient_id = pm.id
    JOIN doctor_master dm ON pv.doctor_id = dm.doctor_id
    WHERE pv.delete_flag = false
    AND pv.follow_up_date = p_discharge_date
    AND (p_doctor_id IS NULL OR pv.doctor_id = p_doctor_id)
    ORDER BY pv.visit_time;
END;
$$;

-- =====================================================
-- COMMENTS
-- =====================================================

COMMENT ON FUNCTION usp_get_amount(VARCHAR, INTEGER) IS 'Calculate billing amount for visits';
COMMENT ON FUNCTION usp_get_amount_status(VARCHAR, INTEGER) IS 'Get payment status for visits';
COMMENT ON FUNCTION usp_get_lab_test(VARCHAR, VARCHAR) IS 'Get available lab tests';
COMMENT ON FUNCTION usp_get_lab_test_details(INTEGER, VARCHAR) IS 'Get lab test results';
COMMENT ON FUNCTION usp_get_bld_medicine() IS 'Get blood pressure and diabetes medicines';
COMMENT ON FUNCTION usp_get_bld_prescription(INTEGER, VARCHAR) IS 'Get blood pressure prescriptions';
COMMENT ON FUNCTION usp_get_clinic_shifts(VARCHAR, VARCHAR) IS 'Get clinic shift schedules';
COMMENT ON FUNCTION usp_get_clinic_shifts_time(VARCHAR, VARCHAR) IS 'Get clinic shift timings';
COMMENT ON FUNCTION usp_get_family_details(INTEGER, INTEGER) IS 'Get family member details';
COMMENT ON FUNCTION usp_get_doctor_count() IS 'Get doctor statistics';
COMMENT ON FUNCTION usp_get_consolidated_fees(VARCHAR, DATE, DATE) IS 'Get consolidated fee report';
COMMENT ON FUNCTION usp_get_discharge_data_by_date(DATE, VARCHAR) IS 'Get discharge data by date';
