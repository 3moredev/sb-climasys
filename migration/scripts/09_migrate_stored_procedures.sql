-- =====================================================
-- Climasys Stored Procedures Migration Script
-- SQL Server to PostgreSQL Migration
-- =====================================================
-- 
-- This script migrates the most commonly used stored procedures
-- from SQL Server to PostgreSQL functions
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- DASHBOARD STORED PROCEDURES
-- =====================================================

-- USP_Get_DashboardData (Already exists, but let's enhance it)
CREATE OR REPLACE FUNCTION usp_get_dashboard_data()
RETURNS JSON
LANGUAGE plpgsql
AS $$
DECLARE
    result JSON;
    perday INTEGER;
    dt_year INTEGER;
    installation_date DATE;
    total_patients INTEGER;
    last_days INTEGER;
    female_patients INTEGER;
    male_patients INTEGER;
    patients_visits INTEGER;
    patients_per_day INTEGER;
    prescription_count INTEGER;
    license_end_date DATE;
    main_doctor_id VARCHAR(30);
BEGIN
    -- Calculate per day visits
    SELECT COALESCE(COUNT(DISTINCT visit_date), 1) INTO perday 
    FROM patient_visits 
    WHERE delete_flag = false;
    
    -- Get license information
    SELECT 
        EXTRACT(YEAR FROM installation_date)::INTEGER,
        installation_date,
        valid_to
    INTO dt_year, installation_date, license_end_date
    FROM license_key 
    WHERE is_active = true 
    LIMIT 1;
    
    -- Get patient statistics
    SELECT COUNT(*) INTO total_patients FROM patient_master WHERE is_active = true;
    
    SELECT COUNT(*) INTO last_days 
    FROM patient_master 
    WHERE created_on > (CURRENT_DATE - INTERVAL '30 days') AND is_active = true;
    
    SELECT COUNT(*) INTO female_patients 
    FROM patient_master 
    WHERE gender_id = 'F' AND is_active = true;
    
    SELECT COUNT(*) INTO male_patients 
    FROM patient_master 
    WHERE gender_id = 'M' AND is_active = true;
    
    SELECT COUNT(*) INTO patients_visits 
    FROM patient_visits 
    WHERE delete_flag = false;
    
    SELECT (COUNT(*) / perday)::INTEGER INTO patients_per_day 
    FROM patient_visits 
    WHERE delete_flag = false;
    
    SELECT COUNT(*) INTO prescription_count 
    FROM visit_prescription_overwrite;
    
    -- Get main doctor ID
    SELECT doctor_id INTO main_doctor_id 
    FROM system_params 
    WHERE is_main_doctor = true 
    LIMIT 1;
    
    -- Build result JSON
    result := json_build_object(
        'dtYear', COALESCE(dt_year, EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER),
        'installationDate', COALESCE(installation_date::TEXT, 'N/A'),
        'totalPatients', total_patients,
        'lastdays', last_days,
        'female', female_patients,
        'male', male_patients,
        'patientsVisits', patients_visits,
        'perday', patients_per_day,
        'prescription', prescription_count,
        'licenseEndDate', COALESCE(license_end_date::TEXT, 'N/A'),
        'mainDoctorId', COALESCE(main_doctor_id, 'N/A')
    );
    
    RETURN result;
END;
$$;

-- =====================================================
-- LOGIN STORED PROCEDURES
-- =====================================================

-- USP_Get_LoginDetails (Enhanced version)
CREATE OR REPLACE FUNCTION usp_get_logindetails(
    p_login_id VARCHAR(60),
    p_password VARCHAR(100),
    p_todays_day VARCHAR(20),
    p_language_id INTEGER
)
RETURNS JSON
LANGUAGE plpgsql
AS $$
DECLARE
    result JSON;
    user_exists BOOLEAN;
    user_data JSON;
    shift_times JSON;
    available_roles JSON;
    system_params JSON;
    license_key_data JSON;
BEGIN
    -- Check if user exists and password matches
    SELECT EXISTS(
        SELECT 1 
        FROM user_master um
        JOIN user_role ur ON um.id = ur.user_id
        JOIN role_master rm ON ur.role_id = rm.role_id
        WHERE um.login_id = p_login_id 
        AND um.password = p_password
        AND um.is_active = true
    ) INTO user_exists;
    
    IF user_exists THEN
        -- Get user details
        SELECT json_build_object(
            'id', um.id,
            'doctorId', dm.doctor_id,
            'clinicId', cm.clinic_id,
            'loginId', um.login_id,
            'firstName', um.first_name,
            'password', um.password,
            'roleName', rm.role_name,
            'roleId', rm.role_id,
            'doctorName', CONCAT(
                COALESCE(dm.first_name, ''), ' ',
                COALESCE(dm.middle_name, ''), ' ',
                COALESCE(dm.last_name, '')
            ),
            'clinicName', cm.clinic_name,
            'languageId', um.language_id,
            'isActive', um.is_active,
            'financialYear', CASE 
                WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 4 
                THEN EXTRACT(YEAR FROM CURRENT_DATE) + 1 
                ELSE EXTRACT(YEAR FROM CURRENT_DATE) 
            END,
            'modelId', m.model_id,
            'configId', mcp.config_id,
            'isEnabled', mcp.is_enabled
        ) INTO user_data
        FROM user_master um
        JOIN user_role ur ON um.id = ur.user_id
        JOIN role_master rm ON ur.role_id = rm.role_id
        JOIN doctor_master dm ON um.doctor_id = dm.doctor_id
        JOIN clinic_master cm ON dm.doctor_id = cm.doctor_id AND ur.clinic_id = cm.clinic_id
        JOIN doctor_model dmo ON dmo.clinic_id = cm.clinic_id
        JOIN model m ON dmo.model_id = m.model_id
        LEFT JOIN model_config_params mcp ON m.model_id = mcp.model_id
        WHERE um.login_id = p_login_id 
        AND um.language_id = p_language_id
        AND ur.is_default_clinic = true
        LIMIT 1;
        
        -- Get shift times
        SELECT json_agg(
            json_build_object(
                'shiftId', sm.shift_id,
                'description', CONCAT(
                    sm.description, ' - ', sm.shift_day, ' - ',
                    TO_CHAR(sm.start_time, 'HH24'), ' - ',
                    TO_CHAR(sm.end_time, 'HH24')
                )
            )
        ) INTO shift_times
        FROM shift_master sm
        JOIN doctor_clinic_shift dcs ON sm.shift_id = dcs.shift_id
        JOIN clinic_master cm ON dcs.clinic_id = cm.clinic_id
        JOIN doctor_master dm ON dcs.doctor_id = dm.doctor_id
        WHERE sm.shift_day = p_todays_day
        AND sm.is_active = true
        ORDER BY ABS(EXTRACT(HOUR FROM (CURRENT_TIME - sm.start_time)));
        
        -- Get available roles
        SELECT json_agg(
            json_build_object(
                'roleId', rm.role_id,
                'roleName', rm.role_name
            )
        ) INTO available_roles
        FROM role_master rm
        WHERE rm.role_name NOT IN (
            SELECT rm2.role_name
            FROM user_master um2
            JOIN user_role ur2 ON um2.id = ur2.user_id
            JOIN role_master rm2 ON ur2.role_id = rm2.role_id
            WHERE um2.login_id = p_login_id
            AND um2.language_id = p_language_id
        )
        AND rm.is_active = true;
        
        -- Get system parameters
        SELECT json_agg(
            json_build_object(
                'paramName', sp.param_name,
                'paramValue', sp.param_value,
                'doctorId', sp.doctor_id
            )
        ) INTO system_params
        FROM system_params sp;
        
        -- Get license key
        SELECT json_build_object(
            'licenseKey', lk.license_key,
            'installationDate', lk.installation_date,
            'validTo', lk.valid_to
        ) INTO license_key_data
        FROM license_key lk
        WHERE lk.is_active = true
        LIMIT 1;
        
        -- Build final result
        result := json_build_object(
            'loginStatus', 1,
            'userDetails', user_data,
            'shiftTimes', shift_times,
            'availableRoles', available_roles,
            'systemParams', system_params,
            'licenseKey', license_key_data
        );
    ELSE
        result := json_build_object('loginStatus', 0);
    END IF;
    
    RETURN result;
END;
$$;

-- =====================================================
-- PATIENT STORED PROCEDURES
-- =====================================================

-- USP_Get_All_Patients_Report
CREATE OR REPLACE FUNCTION usp_get_all_patients_report(
    p_doctor_id VARCHAR(30) DEFAULT NULL,
    p_date_from DATE DEFAULT NULL,
    p_date_to DATE DEFAULT NULL
)
RETURNS TABLE(
    patient_id VARCHAR(30),
    patient_name TEXT,
    gender VARCHAR(1),
    age INTEGER,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    last_visit_date DATE,
    total_visits BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        pm.patient_id,
        CONCAT(
            COALESCE(pm.first_name, ''), ' ',
            COALESCE(pm.middle_name, ''), ' ',
            COALESCE(pm.last_name, '')
        )::TEXT as patient_name,
        pm.gender_id,
        pm.age,
        pm.phone,
        pm.email,
        pm.address,
        MAX(pv.visit_date) as last_visit_date,
        COUNT(pv.id) as total_visits
    FROM patient_master pm
    LEFT JOIN patient_visits pv ON pm.id = pv.patient_id AND pv.delete_flag = false
    WHERE pm.is_active = true
    AND (p_doctor_id IS NULL OR pm.doctor_id = p_doctor_id)
    AND (p_date_from IS NULL OR pv.visit_date >= p_date_from)
    AND (p_date_to IS NULL OR pv.visit_date <= p_date_to)
    GROUP BY pm.id, pm.patient_id, pm.first_name, pm.middle_name, pm.last_name, 
             pm.gender_id, pm.age, pm.phone, pm.email, pm.address
    ORDER BY pm.first_name, pm.last_name;
END;
$$;

-- USP_Get_AllDoctors
CREATE OR REPLACE FUNCTION usp_get_all_doctors()
RETURNS TABLE(
    doctor_id VARCHAR(30),
    doctor_name TEXT,
    qualification VARCHAR(200),
    specialization VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    is_active BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        dm.doctor_id,
        CONCAT(
            COALESCE(dm.first_name, ''), ' ',
            COALESCE(dm.middle_name, ''), ' ',
            COALESCE(dm.last_name, '')
        )::TEXT as doctor_name,
        dm.qualification,
        dm.specialization,
        dm.phone,
        dm.email,
        dm.is_active
    FROM doctor_master dm
    WHERE dm.is_active = true
    ORDER BY dm.first_name, dm.last_name;
END;
$$;

-- USP_Get_ClinicDetails
CREATE OR REPLACE FUNCTION usp_get_clinic_details(
    p_doctor_id VARCHAR(30) DEFAULT NULL
)
RETURNS TABLE(
    clinic_id VARCHAR(30),
    clinic_name VARCHAR(200),
    clinic_address TEXT,
    clinic_phone VARCHAR(20),
    clinic_email VARCHAR(100),
    doctor_name TEXT,
    is_active BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        cm.clinic_id,
        cm.clinic_name,
        cm.clinic_address,
        cm.clinic_phone,
        cm.clinic_email,
        CONCAT(
            COALESCE(dm.first_name, ''), ' ',
            COALESCE(dm.middle_name, ''), ' ',
            COALESCE(dm.last_name, '')
        )::TEXT as doctor_name,
        cm.is_active
    FROM clinic_master cm
    JOIN doctor_master dm ON cm.doctor_id = dm.doctor_id
    WHERE cm.is_active = true
    AND (p_doctor_id IS NULL OR cm.doctor_id = p_doctor_id)
    ORDER BY cm.clinic_name;
END;
$$;

-- =====================================================
-- MEDICINE STORED PROCEDURES
-- =====================================================

-- USP_Get_Active_Medicine
CREATE OR REPLACE FUNCTION usp_get_active_medicine()
RETURNS TABLE(
    medicine_id INTEGER,
    medicine_name VARCHAR(200),
    generic_name VARCHAR(200),
    manufacturer VARCHAR(200),
    unit VARCHAR(50),
    dosage_form VARCHAR(100),
    strength VARCHAR(100)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        mm.medicine_id,
        mm.medicine_name,
        mm.generic_name,
        mm.manufacturer,
        mm.unit,
        mm.dosage_form,
        mm.strength
    FROM medicine_master mm
    WHERE mm.is_active = true
    ORDER BY mm.medicine_name;
END;
$$;

-- USP_Get_Active_Prescription
CREATE OR REPLACE FUNCTION usp_get_active_prescription(
    p_patient_id INTEGER DEFAULT NULL,
    p_doctor_id VARCHAR(30) DEFAULT NULL
)
RETURNS TABLE(
    prescription_id INTEGER,
    visit_id VARCHAR(30),
    patient_name TEXT,
    doctor_name TEXT,
    medicine_name VARCHAR(200),
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    instructions TEXT,
    created_on TIMESTAMP
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        vpo.id as prescription_id,
        vpo.visit_id,
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
        vpo.created_on
    FROM visit_prescription_overwrite vpo
    JOIN patient_master pm ON vpo.patient_id = pm.id
    JOIN doctor_master dm ON vpo.doctor_id = dm.doctor_id
    WHERE (p_patient_id IS NULL OR vpo.patient_id = p_patient_id)
    AND (p_doctor_id IS NULL OR vpo.doctor_id = p_doctor_id)
    ORDER BY vpo.created_on DESC;
END;
$$;

-- =====================================================
-- APPOINTMENT STORED PROCEDURES
-- =====================================================

-- USP_Get_FutureAppointments_All_New
CREATE OR REPLACE FUNCTION usp_get_future_appointments_all_new(
    p_doctor_id VARCHAR(30) DEFAULT NULL,
    p_date_from DATE DEFAULT NULL
)
RETURNS TABLE(
    visit_id VARCHAR(30),
    patient_name TEXT,
    doctor_name TEXT,
    clinic_name VARCHAR(200),
    visit_date DATE,
    visit_time TIME,
    chief_complaint TEXT,
    status VARCHAR(50)
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
        cm.clinic_name,
        pv.visit_date,
        pv.visit_time,
        pv.chief_complaint,
        CASE 
            WHEN pv.follow_up_date > CURRENT_DATE THEN 'Scheduled'
            WHEN pv.follow_up_date = CURRENT_DATE THEN 'Today'
            ELSE 'Overdue'
        END::VARCHAR(50) as status
    FROM patient_visits pv
    JOIN patient_master pm ON pv.patient_id = pm.id
    JOIN doctor_master dm ON pv.doctor_id = dm.doctor_id
    JOIN clinic_master cm ON pv.clinic_id = cm.clinic_id
    WHERE pv.delete_flag = false
    AND pv.follow_up_date >= COALESCE(p_date_from, CURRENT_DATE)
    AND (p_doctor_id IS NULL OR pv.doctor_id = p_doctor_id)
    ORDER BY pv.follow_up_date, pv.visit_time;
END;
$$;

-- =====================================================
-- REPORTING STORED PROCEDURES
-- =====================================================

-- USP_Get_DoctorTodaysVisit
CREATE OR REPLACE FUNCTION usp_get_doctor_todays_visit(
    p_doctor_id VARCHAR(30) DEFAULT NULL,
    p_visit_date DATE DEFAULT CURRENT_DATE
)
RETURNS TABLE(
    visit_id VARCHAR(30),
    patient_name TEXT,
    visit_time TIME,
    chief_complaint TEXT,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT
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
        pv.visit_time,
        pv.chief_complaint,
        pv.diagnosis,
        pv.treatment,
        pv.prescription
    FROM patient_visits pv
    JOIN patient_master pm ON pv.patient_id = pm.id
    WHERE pv.delete_flag = false
    AND pv.visit_date = p_visit_date
    AND (p_doctor_id IS NULL OR pv.doctor_id = p_doctor_id)
    ORDER BY pv.visit_time;
END;
$$;

-- USP_Get_AreaWisePatients
CREATE OR REPLACE FUNCTION usp_get_area_wise_patients(
    p_doctor_id VARCHAR(30) DEFAULT NULL
)
RETURNS TABLE(
    area TEXT,
    patient_count BIGINT,
    percentage NUMERIC(5,2)
)
LANGUAGE plpgsql
AS $$
DECLARE
    total_patients BIGINT;
BEGIN
    -- Get total patients for percentage calculation
    SELECT COUNT(*) INTO total_patients
    FROM patient_master pm
    WHERE pm.is_active = true
    AND (p_doctor_id IS NULL OR pm.doctor_id = p_doctor_id);
    
    RETURN QUERY
    SELECT 
        CASE 
            WHEN pm.address IS NULL OR pm.address = '' THEN 'Unknown'
            ELSE SPLIT_PART(pm.address, ',', -1)::TEXT
        END as area,
        COUNT(*) as patient_count,
        ROUND((COUNT(*)::NUMERIC / total_patients::NUMERIC) * 100, 2) as percentage
    FROM patient_master pm
    WHERE pm.is_active = true
    AND (p_doctor_id IS NULL OR pm.doctor_id = p_doctor_id)
    GROUP BY 
        CASE 
            WHEN pm.address IS NULL OR pm.address = '' THEN 'Unknown'
            ELSE SPLIT_PART(pm.address, ',', -1)
        END
    ORDER BY patient_count DESC;
END;
$$;

-- =====================================================
-- UTILITY FUNCTIONS
-- =====================================================

-- Function to get current financial year
CREATE OR REPLACE FUNCTION get_current_financial_year()
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN CASE 
        WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 4 
        THEN EXTRACT(YEAR FROM CURRENT_DATE) + 1 
        ELSE EXTRACT(YEAR FROM CURRENT_DATE) 
    END;
END;
$$;

-- Function to format date for display
CREATE OR REPLACE FUNCTION format_date_for_display(input_date DATE)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN TO_CHAR(input_date, 'DD-MM-YYYY');
END;
$$;

-- Function to calculate age from date of birth
CREATE OR REPLACE FUNCTION calculate_age(birth_date DATE)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN EXTRACT(YEAR FROM AGE(CURRENT_DATE, birth_date));
END;
$$;

-- =====================================================
-- COMMENTS
-- =====================================================

COMMENT ON FUNCTION usp_get_dashboard_data() IS 'Get comprehensive dashboard data including patient statistics, visits, and system information';
COMMENT ON FUNCTION usp_get_logindetails(VARCHAR, VARCHAR, VARCHAR, INTEGER) IS 'Authenticate user and return login details with user information, shifts, and roles';
COMMENT ON FUNCTION usp_get_all_patients_report(VARCHAR, DATE, DATE) IS 'Get all patients report with optional filtering by doctor and date range';
COMMENT ON FUNCTION usp_get_all_doctors() IS 'Get list of all active doctors';
COMMENT ON FUNCTION usp_get_clinic_details(VARCHAR) IS 'Get clinic details with optional filtering by doctor';
COMMENT ON FUNCTION usp_get_active_medicine() IS 'Get list of all active medicines';
COMMENT ON FUNCTION usp_get_active_prescription(INTEGER, VARCHAR) IS 'Get active prescriptions with optional filtering by patient and doctor';
COMMENT ON FUNCTION usp_get_future_appointments_all_new(VARCHAR, DATE) IS 'Get future appointments with optional filtering by doctor and date';
COMMENT ON FUNCTION usp_get_doctor_todays_visit(VARCHAR, DATE) IS 'Get todays visits for a doctor';
COMMENT ON FUNCTION usp_get_area_wise_patients(VARCHAR) IS 'Get patient distribution by area';
COMMENT ON FUNCTION get_current_financial_year() IS 'Get current financial year (April to March)';
COMMENT ON FUNCTION format_date_for_display(DATE) IS 'Format date as DD-MM-YYYY';
COMMENT ON FUNCTION calculate_age(DATE) IS 'Calculate age from date of birth';
