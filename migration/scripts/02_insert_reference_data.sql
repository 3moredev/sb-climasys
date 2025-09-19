-- =====================================================
-- Reference Data Insertion Script
-- =====================================================
-- 
-- This script inserts reference/master data that is
-- typically static and doesn't change frequently
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- LANGUAGE MASTER DATA
-- =====================================================

INSERT INTO language_master (language_name, language_code, is_active) VALUES
('English', 'EN', true),
('Hindi', 'HI', true),
('Marathi', 'MR', true),
('Gujarati', 'GU', true),
('Tamil', 'TA', true),
('Telugu', 'TE', true),
('Bengali', 'BN', true),
('Kannada', 'KN', true),
('Malayalam', 'ML', true),
('Punjabi', 'PA', true)
ON CONFLICT DO NOTHING;

-- =====================================================
-- ROLE MASTER DATA
-- =====================================================

INSERT INTO role_master (role_name, role_description, is_active) VALUES
('Doctor', 'Primary doctor role with full access', true),
('Nurse', 'Nursing staff with limited access', true),
('Receptionist', 'Front desk and appointment management', true),
('Pharmacist', 'Medicine and prescription management', true),
('Lab Technician', 'Laboratory test management', true),
('Admin', 'System administration role', true),
('Compounder', 'Medicine compounding and dispensing', true),
('Accountant', 'Financial and billing management', true)
ON CONFLICT (role_name) DO NOTHING;

-- =====================================================
-- GENDER MASTER DATA
-- =====================================================

INSERT INTO gender_master (gender_id, gender_name, is_active) VALUES
('M', 'Male', true),
('F', 'Female', true),
('O', 'Other', true)
ON CONFLICT (gender_id) DO NOTHING;

-- =====================================================
-- BLOOD GROUP MASTER DATA
-- =====================================================

INSERT INTO blood_group_master (blood_group_name, is_active) VALUES
('A+', true),
('A-', true),
('B+', true),
('B-', true),
('AB+', true),
('AB-', true),
('O+', true),
('O-', true)
ON CONFLICT DO NOTHING;

-- =====================================================
-- SHIFT MASTER DATA
-- =====================================================

INSERT INTO shift_master (description, shift_day, start_time, end_time, is_active) VALUES
-- Monday Shifts
('Morning Shift - Monday', 'Monday', '09:00:00', '17:00:00', true),
('Evening Shift - Monday', 'Monday', '17:00:00', '21:00:00', true),

-- Tuesday Shifts
('Morning Shift - Tuesday', 'Tuesday', '09:00:00', '17:00:00', true),
('Evening Shift - Tuesday', 'Tuesday', '17:00:00', '21:00:00', true),

-- Wednesday Shifts
('Morning Shift - Wednesday', 'Wednesday', '09:00:00', '17:00:00', true),
('Evening Shift - Wednesday', 'Wednesday', '17:00:00', '21:00:00', true),

-- Thursday Shifts
('Morning Shift - Thursday', 'Thursday', '09:00:00', '17:00:00', true),
('Evening Shift - Thursday', 'Thursday', '17:00:00', '21:00:00', true),

-- Friday Shifts
('Morning Shift - Friday', 'Friday', '09:00:00', '17:00:00', true),
('Evening Shift - Friday', 'Friday', '17:00:00', '21:00:00', true),

-- Saturday Shifts
('Morning Shift - Saturday', 'Saturday', '09:00:00', '13:00:00', true),
('Evening Shift - Saturday', 'Saturday', '13:00:00', '17:00:00', true),

-- Sunday Shifts
('Emergency Shift - Sunday', 'Sunday', '09:00:00', '13:00:00', true)
ON CONFLICT DO NOTHING;

-- =====================================================
-- MODEL MASTER DATA
-- =====================================================

INSERT INTO model (model_name, model_description, is_active) VALUES
('Basic Model', 'Basic clinic management model', true),
('Standard Model', 'Standard clinic management with advanced features', true),
('Premium Model', 'Premium clinic management with all features', true),
('Enterprise Model', 'Enterprise-level clinic management system', true)
ON CONFLICT DO NOTHING;

-- =====================================================
-- MODEL CONFIG PARAMS
-- =====================================================

INSERT INTO model_config_params (model_id, param_name, param_value, is_enabled) VALUES
-- Basic Model Config
(1, 'max_patients_per_day', '50', true),
(1, 'max_users', '5', true),
(1, 'backup_enabled', 'false', true),
(1, 'reporting_enabled', 'false', true),

-- Standard Model Config
(2, 'max_patients_per_day', '100', true),
(2, 'max_users', '10', true),
(2, 'backup_enabled', 'true', true),
(2, 'reporting_enabled', 'true', true),
(2, 'lab_integration', 'true', true),

-- Premium Model Config
(3, 'max_patients_per_day', '200', true),
(3, 'max_users', '25', true),
(3, 'backup_enabled', 'true', true),
(3, 'reporting_enabled', 'true', true),
(3, 'lab_integration', 'true', true),
(3, 'pharmacy_integration', 'true', true),
(3, 'billing_advanced', 'true', true),

-- Enterprise Model Config
(4, 'max_patients_per_day', '500', true),
(4, 'max_users', '100', true),
(4, 'backup_enabled', 'true', true),
(4, 'reporting_enabled', 'true', true),
(4, 'lab_integration', 'true', true),
(4, 'pharmacy_integration', 'true', true),
(4, 'billing_advanced', 'true', true),
(4, 'multi_clinic', 'true', true),
(4, 'api_access', 'true', true)
ON CONFLICT DO NOTHING;

-- =====================================================
-- MEDICINE MASTER DATA (Common Medicines)
-- =====================================================

INSERT INTO medicine_master (medicine_name, generic_name, manufacturer, unit, dosage_form, strength, is_active) VALUES
-- Common Pain Relief
('Paracetamol', 'Acetaminophen', 'Generic', 'Tablet', 'Tablet', '500mg', true),
('Ibuprofen', 'Ibuprofen', 'Generic', 'Tablet', 'Tablet', '400mg', true),
('Aspirin', 'Acetylsalicylic Acid', 'Generic', 'Tablet', 'Tablet', '75mg', true),

-- Common Antibiotics
('Amoxicillin', 'Amoxicillin', 'Generic', 'Capsule', 'Capsule', '500mg', true),
('Ciprofloxacin', 'Ciprofloxacin', 'Generic', 'Tablet', 'Tablet', '500mg', true),
('Azithromycin', 'Azithromycin', 'Generic', 'Tablet', 'Tablet', '500mg', true),

-- Common Antacids
('Omeprazole', 'Omeprazole', 'Generic', 'Capsule', 'Capsule', '20mg', true),
('Ranitidine', 'Ranitidine', 'Generic', 'Tablet', 'Tablet', '150mg', true),

-- Common Vitamins
('Vitamin D3', 'Cholecalciferol', 'Generic', 'Tablet', 'Tablet', '1000 IU', true),
('Vitamin B12', 'Cyanocobalamin', 'Generic', 'Tablet', 'Tablet', '1000mcg', true),

-- Common Antihistamines
('Cetirizine', 'Cetirizine', 'Generic', 'Tablet', 'Tablet', '10mg', true),
('Loratadine', 'Loratadine', 'Generic', 'Tablet', 'Tablet', '10mg', true)
ON CONFLICT DO NOTHING;

-- =====================================================
-- SYSTEM PARAMETERS
-- =====================================================

INSERT INTO system_params (param_name, param_value, param_description, is_main_doctor) VALUES
('system_name', 'Climasys', 'Name of the clinic management system', false),
('version', '2.0', 'System version', false),
('timezone', 'Asia/Kolkata', 'System timezone', false),
('date_format', 'DD-MM-YYYY', 'Default date format', false),
('currency', 'INR', 'Default currency', false),
('max_login_attempts', '3', 'Maximum login attempts before lockout', false),
('session_timeout', '30', 'Session timeout in minutes', false),
('backup_frequency', 'daily', 'Backup frequency', false),
('auto_logout', 'true', 'Enable automatic logout', false),
('maintenance_mode', 'false', 'System maintenance mode', false)
ON CONFLICT DO NOTHING;

-- =====================================================
-- LICENSE KEY (Default)
-- =====================================================

INSERT INTO license_key (license_key, installation_date, valid_to, is_active) VALUES
('CLIMASYS-DEMO-LICENSE-2024', '2019-06-04', '2026-03-31', true)
ON CONFLICT DO NOTHING;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Display counts of inserted data
SELECT 'Language Master' as table_name, COUNT(*) as record_count FROM language_master
UNION ALL
SELECT 'Role Master', COUNT(*) FROM role_master
UNION ALL
SELECT 'Gender Master', COUNT(*) FROM gender_master
UNION ALL
SELECT 'Blood Group Master', COUNT(*) FROM blood_group_master
UNION ALL
SELECT 'Shift Master', COUNT(*) FROM shift_master
UNION ALL
SELECT 'Model Master', COUNT(*) FROM model
UNION ALL
SELECT 'Model Config Params', COUNT(*) FROM model_config_params
UNION ALL
SELECT 'Medicine Master', COUNT(*) FROM medicine_master
UNION ALL
SELECT 'System Params', COUNT(*) FROM system_params
UNION ALL
SELECT 'License Key', COUNT(*) FROM license_key
ORDER BY table_name;
