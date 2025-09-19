-- Recreate tables with correct data types for string IDs from SQL Server
-- This script drops and recreates the tables with VARCHAR IDs

-- Set schema
SET search_path TO climasys_dev, public;

-- Drop foreign key constraints first
ALTER TABLE patient_visits DROP CONSTRAINT IF EXISTS patient_visits_patient_id_fkey;
ALTER TABLE patient_visits DROP CONSTRAINT IF EXISTS patient_visits_doctor_id_fkey;
ALTER TABLE patient_visits DROP CONSTRAINT IF EXISTS patient_visits_clinic_id_fkey;
ALTER TABLE user_role DROP CONSTRAINT IF EXISTS user_role_user_id_fkey;
ALTER TABLE user_role DROP CONSTRAINT IF EXISTS user_role_clinic_id_fkey;
ALTER TABLE user_role DROP CONSTRAINT IF EXISTS user_role_role_id_fkey;
ALTER TABLE doctor_clinic_shift DROP CONSTRAINT IF EXISTS doctor_clinic_shift_doctor_id_fkey;
ALTER TABLE doctor_clinic_shift DROP CONSTRAINT IF EXISTS doctor_clinic_shift_clinic_id_fkey;
ALTER TABLE doctor_clinic_shift DROP CONSTRAINT IF EXISTS doctor_clinic_shift_shift_id_fkey;
ALTER TABLE doctor_model DROP CONSTRAINT IF EXISTS doctor_model_doctor_id_fkey;
ALTER TABLE doctor_model DROP CONSTRAINT IF EXISTS doctor_model_model_id_fkey;
ALTER TABLE model_config_params DROP CONSTRAINT IF EXISTS model_config_params_model_id_fkey;
ALTER TABLE visit_prescription_overwrite DROP CONSTRAINT IF EXISTS visit_prescription_overwrite_patient_id_fkey;
ALTER TABLE visit_prescription_overwrite DROP CONSTRAINT IF EXISTS visit_prescription_overwrite_doctor_id_fkey;
ALTER TABLE patient_master DROP CONSTRAINT IF EXISTS patient_master_blood_group_id_fkey;
ALTER TABLE patient_master DROP CONSTRAINT IF EXISTS patient_master_gender_id_fkey;
ALTER TABLE user_master DROP CONSTRAINT IF EXISTS user_master_language_id_fkey;

-- Drop and recreate patient_master table
DROP TABLE IF EXISTS patient_master CASCADE;
CREATE TABLE patient_master (
    id SERIAL PRIMARY KEY,
    patient_id VARCHAR(50) UNIQUE NOT NULL,
    doctor_id VARCHAR(50),
    first_name VARCHAR(100),
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    gender_id VARCHAR(10),
    date_of_birth DATE,
    age INTEGER,
    blood_group_id VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    emergency_contact VARCHAR(20),
    emergency_contact_name VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate doctor_master table
DROP TABLE IF EXISTS doctor_master CASCADE;
CREATE TABLE doctor_master (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    qualification VARCHAR(100),
    specialization VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate patient_visits table
DROP TABLE IF EXISTS patient_visits CASCADE;
CREATE TABLE patient_visits (
    id SERIAL PRIMARY KEY,
    visit_id VARCHAR(50) UNIQUE NOT NULL,
    patient_id VARCHAR(50),
    doctor_id VARCHAR(50),
    clinic_id VARCHAR(50),
    visit_date DATE,
    visit_time TIME,
    chief_complaint TEXT,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    follow_up_date DATE,
    attended_by_id VARCHAR(50),
    delete_flag BOOLEAN DEFAULT false,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate gender_master table
DROP TABLE IF EXISTS gender_master CASCADE;
CREATE TABLE gender_master (
    id SERIAL PRIMARY KEY,
    gender_id VARCHAR(10) UNIQUE NOT NULL,
    gender_name VARCHAR(50) NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate language_master table
DROP TABLE IF EXISTS language_master CASCADE;
CREATE TABLE language_master (
    id SERIAL PRIMARY KEY,
    language_id VARCHAR(50) UNIQUE NOT NULL,
    language_name VARCHAR(100) NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate blood_group_master table
DROP TABLE IF EXISTS blood_group_master CASCADE;
CREATE TABLE blood_group_master (
    id SERIAL PRIMARY KEY,
    blood_group_id VARCHAR(50) UNIQUE NOT NULL,
    blood_group_name VARCHAR(20) NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate clinic_master table
DROP TABLE IF EXISTS clinic_master CASCADE;
CREATE TABLE clinic_master (
    id SERIAL PRIMARY KEY,
    clinic_id VARCHAR(50) UNIQUE NOT NULL,
    clinic_name VARCHAR(100) NOT NULL,
    address TEXT,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate user_master table
DROP TABLE IF EXISTS user_master CASCADE;
CREATE TABLE user_master (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    user_role VARCHAR(50),
    email VARCHAR(100),
    clinic_id VARCHAR(50),
    doctor_id VARCHAR(50),
    language_id VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate system_params table
DROP TABLE IF EXISTS system_params CASCADE;
CREATE TABLE system_params (
    id SERIAL PRIMARY KEY,
    param_id VARCHAR(50) UNIQUE NOT NULL,
    param_name VARCHAR(100) NOT NULL,
    param_value TEXT,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate user_role table
DROP TABLE IF EXISTS user_role CASCADE;
CREATE TABLE user_role (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50),
    clinic_id VARCHAR(50),
    role_id VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate license_key table
DROP TABLE IF EXISTS license_key CASCADE;
CREATE TABLE license_key (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(50),
    clinic_id VARCHAR(50),
    installation_date DATE,
    valid_to DATE,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate role_master table
DROP TABLE IF EXISTS role_master CASCADE;
CREATE TABLE role_master (
    id SERIAL PRIMARY KEY,
    role_id VARCHAR(50) UNIQUE NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate shift_master table
DROP TABLE IF EXISTS shift_master CASCADE;
CREATE TABLE shift_master (
    id SERIAL PRIMARY KEY,
    shift_id VARCHAR(50) UNIQUE NOT NULL,
    shift_name VARCHAR(100) NOT NULL,
    shift_description TEXT,
    shift_day VARCHAR(20),
    start_time TIME,
    end_time TIME,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate doctor_clinic_shift table
DROP TABLE IF EXISTS doctor_clinic_shift CASCADE;
CREATE TABLE doctor_clinic_shift (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(50),
    clinic_id VARCHAR(50),
    shift_id VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate doctor_model table
DROP TABLE IF EXISTS doctor_model CASCADE;
CREATE TABLE doctor_model (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(50),
    model_id VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate model table
DROP TABLE IF EXISTS model CASCADE;
CREATE TABLE model (
    id SERIAL PRIMARY KEY,
    model_id VARCHAR(50) UNIQUE NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    model_description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate model_config_params table
DROP TABLE IF EXISTS model_config_params CASCADE;
CREATE TABLE model_config_params (
    id SERIAL PRIMARY KEY,
    model_id VARCHAR(50),
    param_id VARCHAR(50),
    param_name VARCHAR(100),
    param_value TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop and recreate visit_prescription_overwrite table
DROP TABLE IF EXISTS visit_prescription_overwrite CASCADE;
CREATE TABLE visit_prescription_overwrite (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(50),
    patient_id VARCHAR(50),
    visit_id VARCHAR(50),
    prescription_data TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Commit the changes
COMMIT;

-- Display success message
SELECT 'Tables recreated successfully with VARCHAR IDs for SQL Server compatibility' as message;
