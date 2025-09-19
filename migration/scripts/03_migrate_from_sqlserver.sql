-- =====================================================
-- SQL Server to PostgreSQL Data Migration Script
-- =====================================================
-- 
-- This script provides the framework for migrating data
-- from SQL Server schema 'Climasys-00010' to PostgreSQL
-- schema 'climasys_dev'
--
-- IMPORTANT: This script contains the structure and
-- sample queries. You need to modify the connection
-- details and run the actual data extraction from
-- your SQL Server instance.
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- DATA MIGRATION FUNCTIONS
-- =====================================================

-- Function to migrate doctor data
CREATE OR REPLACE FUNCTION migrate_doctors()
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    result TEXT;
BEGIN
    -- This function should be called after extracting data from SQL Server
    -- Example of how to insert doctor data:
    
    -- Sample doctor data (replace with actual SQL Server extraction)
    INSERT INTO doctor_master (doctor_id, first_name, middle_name, last_name, qualification, specialization, phone, email, address, is_active)
    VALUES 
        ('DOC001', 'John', '', 'Smith', 'MBBS, MD', 'General Medicine', '+91-9876543210', 'dr.john@clinic.com', '123 Main Street, City', true),
        ('DOC002', 'Jane', 'A', 'Doe', 'MBBS, MS', 'Surgery', '+91-9876543211', 'dr.jane@clinic.com', '456 Oak Avenue, City', true)
    ON CONFLICT (doctor_id) DO UPDATE SET
        first_name = EXCLUDED.first_name,
        middle_name = EXCLUDED.middle_name,
        last_name = EXCLUDED.last_name,
        qualification = EXCLUDED.qualification,
        specialization = EXCLUDED.specialization,
        phone = EXCLUDED.phone,
        email = EXCLUDED.email,
        address = EXCLUDED.address,
        updated_on = CURRENT_TIMESTAMP;
    
    result := 'Doctor migration completed successfully';
    RETURN result;
END;
$$;

-- Function to migrate clinic data
CREATE OR REPLACE FUNCTION migrate_clinics()
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    result TEXT;
BEGIN
    -- Sample clinic data (replace with actual SQL Server extraction)
    INSERT INTO clinic_master (clinic_id, doctor_id, clinic_name, clinic_address, clinic_phone, clinic_email, is_active)
    VALUES 
        ('CLINIC001', 'DOC001', 'City Medical Center', '123 Main Street, City, State 12345', '+1-555-0123', 'info@citymedical.com', true),
        ('CLINIC002', 'DOC002', 'Oak Avenue Clinic', '456 Oak Avenue, City, State 12345', '+1-555-0124', 'info@oakclinic.com', true)
    ON CONFLICT (clinic_id) DO UPDATE SET
        clinic_name = EXCLUDED.clinic_name,
        clinic_address = EXCLUDED.clinic_address,
        clinic_phone = EXCLUDED.clinic_phone,
        clinic_email = EXCLUDED.clinic_email,
        updated_on = CURRENT_TIMESTAMP;
    
    result := 'Clinic migration completed successfully';
    RETURN result;
END;
$$;

-- Function to migrate user data
CREATE OR REPLACE FUNCTION migrate_users()
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    result TEXT;
BEGIN
    -- Sample user data (replace with actual SQL Server extraction)
    INSERT INTO user_master (doctor_id, login_id, password, first_name, middle_name, last_name, language_id, is_active)
    VALUES 
        ('DOC001', 'dr.john', 'hashed_password_here', 'John', '', 'Smith', 1, true),
        ('DOC002', 'dr.jane', 'hashed_password_here', 'Jane', 'A', 'Doe', 1, true),
        ('DOC001', 'nurse1', 'hashed_password_here', 'Mary', '', 'Johnson', 1, true)
    ON CONFLICT (login_id) DO UPDATE SET
        first_name = EXCLUDED.first_name,
        middle_name = EXCLUDED.middle_name,
        last_name = EXCLUDED.last_name,
        language_id = EXCLUDED.language_id,
        updated_on = CURRENT_TIMESTAMP;
    
    result := 'User migration completed successfully';
    RETURN result;
END;
$$;

-- Function to migrate user roles
CREATE OR REPLACE FUNCTION migrate_user_roles()
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    result TEXT;
BEGIN
    -- Sample user role data (replace with actual SQL Server extraction)
    INSERT INTO user_role (user_id, role_id, clinic_id, is_default_clinic, is_active)
    SELECT 
        um.id,
        rm.role_id,
        'CLINIC001',
        true,
        true
    FROM user_master um
    CROSS JOIN role_master rm
    WHERE um.login_id IN ('dr.john', 'dr.jane')
    AND rm.role_name = 'Doctor'
    
    UNION ALL
    
    SELECT 
        um.id,
        rm.role_id,
        'CLINIC001',
        true,
        true
    FROM user_master um
    CROSS JOIN role_master rm
    WHERE um.login_id = 'nurse1'
    AND rm.role_name = 'Nurse'
    
    ON CONFLICT (user_id, role_id, clinic_id) DO UPDATE SET
        is_default_clinic = EXCLUDED.is_default_clinic,
        updated_on = CURRENT_TIMESTAMP;
    
    result := 'User role migration completed successfully';
    RETURN result;
END;
$$;

-- Function to migrate patient data
CREATE OR REPLACE FUNCTION migrate_patients()
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    result TEXT;
BEGIN
    -- Sample patient data (replace with actual SQL Server extraction)
    INSERT INTO patient_master (doctor_id, patient_id, first_name, middle_name, last_name, gender_id, date_of_birth, age, blood_group_id, phone, email, address, emergency_contact, emergency_contact_name, is_active)
    VALUES 
        ('DOC001', 'P001', 'Alice', '', 'Johnson', 'F', '1985-03-15', 39, 1, '+91-9876543212', 'alice@email.com', '789 Pine Street, City', '+91-9876543213', 'Bob Johnson', true),
        ('DOC001', 'P002', 'Bob', 'M', 'Smith', 'M', '1990-07-22', 34, 2, '+91-9876543214', 'bob@email.com', '321 Elm Street, City', '+91-9876543215', 'Alice Smith', true),
        ('DOC002', 'P003', 'Charlie', '', 'Brown', 'M', '1978-12-10', 45, 3, '+91-9876543216', 'charlie@email.com', '654 Maple Avenue, City', '+91-9876543217', 'Diana Brown', true)
    ON CONFLICT (patient_id) DO UPDATE SET
        first_name = EXCLUDED.first_name,
        middle_name = EXCLUDED.middle_name,
        last_name = EXCLUDED.last_name,
        gender_id = EXCLUDED.gender_id,
        date_of_birth = EXCLUDED.date_of_birth,
        age = EXCLUDED.age,
        blood_group_id = EXCLUDED.blood_group_id,
        phone = EXCLUDED.phone,
        email = EXCLUDED.email,
        address = EXCLUDED.address,
        emergency_contact = EXCLUDED.emergency_contact,
        emergency_contact_name = EXCLUDED.emergency_contact_name,
        updated_on = CURRENT_TIMESTAMP;
    
    result := 'Patient migration completed successfully';
    RETURN result;
END;
$$;

-- Function to migrate patient visits
CREATE OR REPLACE FUNCTION migrate_patient_visits()
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    result TEXT;
BEGIN
    -- Sample patient visit data (replace with actual SQL Server extraction)
    INSERT INTO patient_visits (visit_id, patient_id, doctor_id, clinic_id, visit_date, visit_time, chief_complaint, diagnosis, treatment, prescription, follow_up_date, attended_by_id, delete_flag)
    SELECT 
        'V001',
        pm.id,
        'DOC001',
        'CLINIC001',
        CURRENT_DATE - INTERVAL '5 days',
        '10:30:00',
        'Fever and headache',
        'Viral fever',
        'Rest and medication',
        'Paracetamol 500mg twice daily for 3 days',
        CURRENT_DATE + INTERVAL '3 days',
        um.id,
        false
    FROM patient_master pm
    JOIN user_master um ON um.doctor_id = pm.doctor_id
    WHERE pm.patient_id = 'P001'
    AND um.login_id = 'dr.john'
    
    UNION ALL
    
    SELECT 
        'V002',
        pm.id,
        'DOC001',
        'CLINIC001',
        CURRENT_DATE - INTERVAL '3 days',
        '14:15:00',
        'Chest pain',
        'Acid reflux',
        'Diet modification and medication',
        'Omeprazole 20mg once daily for 2 weeks',
        CURRENT_DATE + INTERVAL '2 weeks',
        um.id,
        false
    FROM patient_master pm
    JOIN user_master um ON um.doctor_id = pm.doctor_id
    WHERE pm.patient_id = 'P002'
    AND um.login_id = 'dr.john'
    
    ON CONFLICT (visit_id) DO UPDATE SET
        chief_complaint = EXCLUDED.chief_complaint,
        diagnosis = EXCLUDED.diagnosis,
        treatment = EXCLUDED.treatment,
        prescription = EXCLUDED.prescription,
        follow_up_date = EXCLUDED.follow_up_date,
        updated_on = CURRENT_TIMESTAMP;
    
    result := 'Patient visit migration completed successfully';
    RETURN result;
END;
$$;

-- Function to migrate system parameters
CREATE OR REPLACE FUNCTION migrate_system_params()
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    result TEXT;
BEGIN
    -- Sample system parameters (replace with actual SQL Server extraction)
    INSERT INTO system_params (doctor_id, param_name, param_value, param_description, is_main_doctor)
    VALUES 
        ('DOC001', 'main_doctor', 'true', 'Main doctor flag', true),
        ('DOC001', 'clinic_timings', '09:00-17:00', 'Clinic operating hours', false),
        ('DOC001', 'consultation_fee', '500', 'Default consultation fee', false),
        ('DOC002', 'main_doctor', 'false', 'Main doctor flag', false),
        ('DOC002', 'clinic_timings', '14:00-21:00', 'Clinic operating hours', false),
        ('DOC002', 'consultation_fee', '600', 'Default consultation fee', false)
    ON CONFLICT DO NOTHING;
    
    result := 'System parameters migration completed successfully';
    RETURN result;
END;
$$;

-- =====================================================
-- MAIN MIGRATION FUNCTION
-- =====================================================

CREATE OR REPLACE FUNCTION run_full_migration()
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    result TEXT := '';
    temp_result TEXT;
BEGIN
    -- Run all migration functions
    SELECT migrate_doctors() INTO temp_result;
    result := result || temp_result || E'\n';
    
    SELECT migrate_clinics() INTO temp_result;
    result := result || temp_result || E'\n';
    
    SELECT migrate_users() INTO temp_result;
    result := result || temp_result || E'\n';
    
    SELECT migrate_user_roles() INTO temp_result;
    result := result || temp_result || E'\n';
    
    SELECT migrate_patients() INTO temp_result;
    result := result || temp_result || E'\n';
    
    SELECT migrate_patient_visits() INTO temp_result;
    result := result || temp_result || E'\n';
    
    SELECT migrate_system_params() INTO temp_result;
    result := result || temp_result || E'\n';
    
    result := result || 'Full migration completed successfully!';
    RETURN result;
END;
$$;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Function to verify migration
CREATE OR REPLACE FUNCTION verify_migration()
RETURNS TABLE(table_name TEXT, record_count BIGINT)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 'Doctor Master'::TEXT, COUNT(*)::BIGINT FROM doctor_master
    UNION ALL
    SELECT 'Clinic Master'::TEXT, COUNT(*)::BIGINT FROM clinic_master
    UNION ALL
    SELECT 'User Master'::TEXT, COUNT(*)::BIGINT FROM user_master
    UNION ALL
    SELECT 'User Role'::TEXT, COUNT(*)::BIGINT FROM user_role
    UNION ALL
    SELECT 'Patient Master'::TEXT, COUNT(*)::BIGINT FROM patient_master
    UNION ALL
    SELECT 'Patient Visits'::TEXT, COUNT(*)::BIGINT FROM patient_visits
    UNION ALL
    SELECT 'System Params'::TEXT, COUNT(*)::BIGINT FROM system_params
    ORDER BY table_name;
END;
$$;

-- =====================================================
-- USAGE INSTRUCTIONS
-- =====================================================

/*
To use this migration script:

1. First, run the schema creation script:
   \i 01_create_schema.sql

2. Then, run the reference data script:
   \i 02_insert_reference_data.sql

3. Modify the sample data in the migration functions above
   with your actual SQL Server data extraction queries.

4. Run the migration:
   SELECT run_full_migration();

5. Verify the migration:
   SELECT * FROM verify_migration();

6. Check for any errors and fix them as needed.

Note: You'll need to extract the actual data from your SQL Server
instance and replace the sample data in the functions above.
*/
