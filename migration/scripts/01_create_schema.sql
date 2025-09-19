-- =====================================================
-- Climasys Database Migration Scripts
-- SQL Server to PostgreSQL Migration
-- =====================================================
-- 
-- This script creates the PostgreSQL schema and tables
-- for the Climasys application
--
-- Source: SQL Server schema 'Climasys-00010'
-- Target: PostgreSQL schema 'climasys_dev'
--
-- =====================================================

-- Create the climasys_dev schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS climasys_dev;

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- CORE MASTER TABLES
-- =====================================================

-- Language Master
CREATE TABLE IF NOT EXISTS language_master (
    language_id SERIAL PRIMARY KEY,
    language_name VARCHAR(50) NOT NULL,
    language_code VARCHAR(10),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Role Master
CREATE TABLE IF NOT EXISTS role_master (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Doctor Master
CREATE TABLE IF NOT EXISTS doctor_master (
    doctor_id VARCHAR(30) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    qualification VARCHAR(200),
    specialization VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clinic Master
CREATE TABLE IF NOT EXISTS clinic_master (
    clinic_id VARCHAR(30) PRIMARY KEY,
    doctor_id VARCHAR(30) NOT NULL REFERENCES doctor_master(doctor_id),
    clinic_name VARCHAR(200) NOT NULL,
    clinic_address TEXT,
    clinic_phone VARCHAR(20),
    clinic_email VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Master
CREATE TABLE IF NOT EXISTS user_master (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30) NOT NULL REFERENCES doctor_master(doctor_id),
    login_id VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    language_id INTEGER NOT NULL REFERENCES language_master(language_id),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Role
CREATE TABLE IF NOT EXISTS user_role (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES user_master(id),
    role_id INTEGER NOT NULL REFERENCES role_master(role_id),
    clinic_id VARCHAR(30) NOT NULL REFERENCES clinic_master(clinic_id),
    is_default_clinic BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id, clinic_id)
);

-- =====================================================
-- PATIENT MANAGEMENT TABLES
-- =====================================================

-- Gender Master
CREATE TABLE IF NOT EXISTS gender_master (
    gender_id VARCHAR(1) PRIMARY KEY,
    gender_name VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT true
);

-- Blood Group Master
CREATE TABLE IF NOT EXISTS blood_group_master (
    blood_group_id SERIAL PRIMARY KEY,
    blood_group_name VARCHAR(10) NOT NULL,
    is_active BOOLEAN DEFAULT true
);

-- Patient Master
CREATE TABLE IF NOT EXISTS patient_master (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30) NOT NULL REFERENCES doctor_master(doctor_id),
    patient_id VARCHAR(30) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    gender_id VARCHAR(1) REFERENCES gender_master(gender_id),
    date_of_birth DATE,
    age INTEGER,
    blood_group_id INTEGER REFERENCES blood_group_master(blood_group_id),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    emergency_contact VARCHAR(20),
    emergency_contact_name VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patient Visits
CREATE TABLE IF NOT EXISTS patient_visits (
    id SERIAL PRIMARY KEY,
    visit_id VARCHAR(30) UNIQUE,
    patient_id INTEGER NOT NULL REFERENCES patient_master(id),
    doctor_id VARCHAR(30) NOT NULL REFERENCES doctor_master(doctor_id),
    clinic_id VARCHAR(30) NOT NULL REFERENCES clinic_master(clinic_id),
    visit_date DATE NOT NULL,
    visit_time TIME,
    chief_complaint TEXT,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    follow_up_date DATE,
    attended_by_id INTEGER REFERENCES user_master(id),
    delete_flag BOOLEAN DEFAULT false,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- SHIFT AND SCHEDULING TABLES
-- =====================================================

-- Shift Master
CREATE TABLE IF NOT EXISTS shift_master (
    shift_id SERIAL PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    shift_day VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Doctor Clinic Shift
CREATE TABLE IF NOT EXISTS doctor_clinic_shift (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30) NOT NULL REFERENCES doctor_master(doctor_id),
    clinic_id VARCHAR(30) NOT NULL REFERENCES clinic_master(clinic_id),
    shift_id INTEGER NOT NULL REFERENCES shift_master(shift_id),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(doctor_id, clinic_id, shift_id)
);

-- =====================================================
-- MEDICINE AND PRESCRIPTION TABLES
-- =====================================================

-- Medicine Master
CREATE TABLE IF NOT EXISTS medicine_master (
    medicine_id SERIAL PRIMARY KEY,
    medicine_name VARCHAR(200) NOT NULL,
    generic_name VARCHAR(200),
    manufacturer VARCHAR(200),
    unit VARCHAR(50),
    dosage_form VARCHAR(100),
    strength VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Visit Prescription Overwrite
CREATE TABLE IF NOT EXISTS visit_prescription_overwrite (
    id SERIAL PRIMARY KEY,
    visit_id VARCHAR(30) NOT NULL,
    patient_id INTEGER NOT NULL REFERENCES patient_master(id),
    doctor_id VARCHAR(30) NOT NULL REFERENCES doctor_master(doctor_id),
    medicine_id INTEGER REFERENCES medicine_master(medicine_id),
    medicine_name VARCHAR(200),
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    instructions TEXT,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- SYSTEM CONFIGURATION TABLES
-- =====================================================

-- System Parameters
CREATE TABLE IF NOT EXISTS system_params (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30) REFERENCES doctor_master(doctor_id),
    param_name VARCHAR(100) NOT NULL,
    param_value TEXT,
    param_description TEXT,
    is_main_doctor BOOLEAN DEFAULT false,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- License Key
CREATE TABLE IF NOT EXISTS license_key (
    id SERIAL PRIMARY KEY,
    license_key VARCHAR(200) NOT NULL,
    installation_date DATE,
    valid_to DATE,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Model
CREATE TABLE IF NOT EXISTS model (
    model_id SERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    model_description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Doctor Model
CREATE TABLE IF NOT EXISTS doctor_model (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30) REFERENCES doctor_master(doctor_id),
    clinic_id VARCHAR(30) REFERENCES clinic_master(clinic_id),
    model_id INTEGER NOT NULL REFERENCES model(model_id),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Model Config Params
CREATE TABLE IF NOT EXISTS model_config_params (
    config_id SERIAL PRIMARY KEY,
    model_id INTEGER NOT NULL REFERENCES model(model_id),
    param_name VARCHAR(100) NOT NULL,
    param_value TEXT,
    is_enabled BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- User Master indexes
CREATE INDEX IF NOT EXISTS idx_user_master_login_id ON user_master(login_id);
CREATE INDEX IF NOT EXISTS idx_user_master_doctor_id ON user_master(doctor_id);

-- Patient Master indexes
CREATE INDEX IF NOT EXISTS idx_patient_master_doctor_id ON patient_master(doctor_id);
CREATE INDEX IF NOT EXISTS idx_patient_master_patient_id ON patient_master(patient_id);
CREATE INDEX IF NOT EXISTS idx_patient_master_created_on ON patient_master(created_on);

-- Patient Visits indexes
CREATE INDEX IF NOT EXISTS idx_patient_visits_patient_id ON patient_visits(patient_id);
CREATE INDEX IF NOT EXISTS idx_patient_visits_doctor_id ON patient_visits(doctor_id);
CREATE INDEX IF NOT EXISTS idx_patient_visits_visit_date ON patient_visits(visit_date);
CREATE INDEX IF NOT EXISTS idx_patient_visits_delete_flag ON patient_visits(delete_flag);

-- User Role indexes
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_clinic_id ON user_role(clinic_id);

-- =====================================================
-- COMMENTS
-- =====================================================

COMMENT ON SCHEMA climasys_dev IS 'Climasys application database schema';
COMMENT ON TABLE user_master IS 'Master table for system users';
COMMENT ON TABLE doctor_master IS 'Master table for doctors';
COMMENT ON TABLE clinic_master IS 'Master table for clinics';
COMMENT ON TABLE patient_master IS 'Master table for patients';
COMMENT ON TABLE patient_visits IS 'Patient visit records';
COMMENT ON TABLE user_role IS 'User role assignments';
COMMENT ON TABLE system_params IS 'System configuration parameters';
COMMENT ON TABLE license_key IS 'System license information';
