-- Update PostgreSQL schema to handle string IDs from SQL Server
-- This script modifies the existing tables to use VARCHAR for IDs

-- Set schema
SET search_path TO climasys_dev, public;

-- Update patient_master table to use VARCHAR for patient_id
ALTER TABLE patient_master ALTER COLUMN patient_id TYPE VARCHAR(50);

-- Update patient_visits table to use VARCHAR for IDs
ALTER TABLE patient_visits ALTER COLUMN visit_id TYPE VARCHAR(50);
ALTER TABLE patient_visits ALTER COLUMN patient_id TYPE VARCHAR(50);

-- Update doctor_master table to use VARCHAR for doctor_id
ALTER TABLE doctor_master ALTER COLUMN doctor_id TYPE VARCHAR(50);

-- Update clinic_master table to use VARCHAR for clinic_id
ALTER TABLE clinic_master ALTER COLUMN clinic_id TYPE VARCHAR(50);

-- Update user_master table to use VARCHAR for user_id
ALTER TABLE user_master ALTER COLUMN user_id TYPE VARCHAR(50);

-- Update system_params table to use VARCHAR for param_id
ALTER TABLE system_params ALTER COLUMN param_id TYPE VARCHAR(50);

-- Update user_role table to use VARCHAR for IDs
ALTER TABLE user_role ALTER COLUMN user_id TYPE VARCHAR(50);
ALTER TABLE user_role ALTER COLUMN clinic_id TYPE VARCHAR(50);

-- Update license_key table to use VARCHAR for IDs
ALTER TABLE license_key ALTER COLUMN doctor_id TYPE VARCHAR(50);
ALTER TABLE license_key ALTER COLUMN clinic_id TYPE VARCHAR(50);

-- Update role_master table to use VARCHAR for role_id
ALTER TABLE role_master ALTER COLUMN role_id TYPE VARCHAR(50);

-- Update shift_master table to use VARCHAR for shift_id
ALTER TABLE shift_master ALTER COLUMN shift_id TYPE VARCHAR(50);

-- Update doctor_clinic_shift table to use VARCHAR for IDs
ALTER TABLE doctor_clinic_shift ALTER COLUMN doctor_id TYPE VARCHAR(50);
ALTER TABLE doctor_clinic_shift ALTER COLUMN clinic_id TYPE VARCHAR(50);
ALTER TABLE doctor_clinic_shift ALTER COLUMN shift_id TYPE VARCHAR(50);

-- Update doctor_model table to use VARCHAR for doctor_id
ALTER TABLE doctor_model ALTER COLUMN doctor_id TYPE VARCHAR(50);

-- Update model table to use VARCHAR for model_id
ALTER TABLE model ALTER COLUMN model_id TYPE VARCHAR(50);

-- Update model_config_params table to use VARCHAR for IDs
ALTER TABLE model_config_params ALTER COLUMN model_id TYPE VARCHAR(50);
ALTER TABLE model_config_params ALTER COLUMN param_id TYPE VARCHAR(50);

-- Update visit_prescription_overwrite table to use VARCHAR for IDs
ALTER TABLE visit_prescription_overwrite ALTER COLUMN doctor_id TYPE VARCHAR(50);
ALTER TABLE visit_prescription_overwrite ALTER COLUMN clinic_id TYPE VARCHAR(50);
ALTER TABLE visit_prescription_overwrite ALTER COLUMN patient_id TYPE VARCHAR(50);
ALTER TABLE visit_prescription_overwrite ALTER COLUMN visit_id TYPE VARCHAR(50);

-- Update blood_group_master table to use VARCHAR for blood_group_id
ALTER TABLE blood_group_master ALTER COLUMN blood_group_id TYPE VARCHAR(50);

-- Update gender_master table to use VARCHAR for gender_id
ALTER TABLE gender_master ALTER COLUMN gender_id TYPE VARCHAR(50);

-- Update language_master table to use VARCHAR for language_id
ALTER TABLE language_master ALTER COLUMN language_id TYPE VARCHAR(50);

-- Commit the changes
COMMIT;

-- Display success message
SELECT 'Schema updated successfully to handle string IDs from SQL Server' as message;
