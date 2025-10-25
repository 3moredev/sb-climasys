-- =====================================================
-- ALTER SCRIPTS TO ADD CLINIC_ID FIELD
-- =====================================================
-- This script adds clinic_id field to specified tables
-- and updates primary key constraints to include clinic_id
--
-- Tables to be modified:
-- 1. status_order
-- 2. role_master  
-- 3. lab_test_master
-- 4. lab_test_parameter
-- 5. complaint_master
-- 6. medicine_master
-- 7. referal_doctors_list (if exists)
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- 1. STATUS_ORDER TABLE
-- =====================================================

-- Add clinic_id column to status_order table
ALTER TABLE status_order 
ADD COLUMN clinic_id VARCHAR(30);

-- Note: Cannot add foreign key constraint to clinic_id alone since clinic_master has composite primary key (clinic_id, doctor_id)
-- Foreign key constraint will be added after ensuring data integrity

-- Drop existing primary key constraint
ALTER TABLE status_order 
DROP CONSTRAINT IF EXISTS staus_id_pk;

-- Add new primary key constraint including clinic_id
ALTER TABLE status_order 
ADD CONSTRAINT status_order_pkey 
PRIMARY KEY (doctor_id, role_id, status_id, clinic_id);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_status_order_clinic_id ON status_order(clinic_id);
CREATE INDEX IF NOT EXISTS idx_status_order_doctor_clinic ON status_order(doctor_id, clinic_id);

-- Add comments
COMMENT ON COLUMN status_order.clinic_id IS 'Clinic ID for multi-clinic support';

-- =====================================================
-- 2. ROLE_MASTER TABLE
-- =====================================================

-- Add clinic_id column to role_master table
ALTER TABLE role_master 
ADD COLUMN clinic_id VARCHAR(30);

-- Note: Cannot add foreign key constraint to clinic_id alone since clinic_master has composite primary key (clinic_id, doctor_id)
-- Foreign key constraint will be added after ensuring data integrity

-- Drop existing primary key constraint
ALTER TABLE role_master 
DROP CONSTRAINT IF EXISTS role_master_pk;

-- Add new primary key constraint including clinic_id
ALTER TABLE role_master 
ADD CONSTRAINT role_master_pkey 
PRIMARY KEY (role_id, clinic_id);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_role_master_clinic_id ON role_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_role_master_role_clinic ON role_master(role_id, clinic_id);

-- Add comments
COMMENT ON COLUMN role_master.clinic_id IS 'Clinic ID for multi-clinic support';

-- =====================================================
-- 3. LAB_TEST_MASTER TABLE
-- =====================================================

-- Add clinic_id column to lab_test_master table
ALTER TABLE lab_test_master 
ADD COLUMN clinic_id VARCHAR(30);

-- Note: Cannot add foreign key constraint to clinic_id alone since clinic_master has composite primary key (clinic_id, doctor_id)
-- Foreign key constraint will be added after ensuring data integrity

-- Drop existing primary key constraint
ALTER TABLE lab_test_master 
DROP CONSTRAINT IF EXISTS lab_test_ref_pk;

-- Add new primary key constraint including clinic_id
ALTER TABLE lab_test_master 
ADD CONSTRAINT lab_test_master_pkey 
PRIMARY KEY (doctor_id, id, clinic_id);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_lab_test_master_clinic_id ON lab_test_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_master_doctor_clinic ON lab_test_master(doctor_id, clinic_id);

-- Add comments
COMMENT ON COLUMN lab_test_master.clinic_id IS 'Clinic ID for multi-clinic support';

-- =====================================================
-- 4. LAB_TEST_PARAMETER TABLE
-- =====================================================

-- Add clinic_id column to lab_test_parameter table
ALTER TABLE lab_test_parameter 
ADD COLUMN clinic_id VARCHAR(30);

-- Note: Cannot add foreign key constraint to clinic_id alone since clinic_master has composite primary key (clinic_id, doctor_id)
-- Foreign key constraint will be added after ensuring data integrity

-- Drop existing primary key constraint
ALTER TABLE lab_test_parameter 
DROP CONSTRAINT IF EXISTS lab_test_parameter_pk;

-- Add new primary key constraint including clinic_id
ALTER TABLE lab_test_parameter 
ADD CONSTRAINT lab_test_parameter_pkey 
PRIMARY KEY (doctor_id, id, lab_test_id, clinic_id);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_clinic_id ON lab_test_parameter(clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_doctor_clinic ON lab_test_parameter(doctor_id, clinic_id);

-- Add comments
COMMENT ON COLUMN lab_test_parameter.clinic_id IS 'Clinic ID for multi-clinic support';

-- =====================================================
-- 5. COMPLAINT_MASTER TABLE
-- =====================================================

-- Add clinic_id column to complaint_master table
ALTER TABLE complaint_master 
ADD COLUMN clinic_id VARCHAR(30);

-- Note: Cannot add foreign key constraint to clinic_id alone since clinic_master has composite primary key (clinic_id, doctor_id)
-- Foreign key constraint will be added after ensuring data integrity

-- Drop existing primary key constraint (if exists)
ALTER TABLE complaint_master 
DROP CONSTRAINT IF EXISTS symptom_ref_pk;

-- Add new primary key constraint including clinic_id
ALTER TABLE complaint_master 
ADD CONSTRAINT complaint_master_pkey 
PRIMARY KEY (short_description, doctor_id, clinic_id);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_complaint_master_clinic_id ON complaint_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_complaint_master_doctor_clinic ON complaint_master(doctor_id, clinic_id);

-- Add comments
COMMENT ON COLUMN complaint_master.clinic_id IS 'Clinic ID for multi-clinic support';

-- =====================================================
-- 6. MEDICINE_MASTER TABLE
-- =====================================================

-- Add clinic_id column to medicine_master table
ALTER TABLE medicine_master 
ADD COLUMN clinic_id VARCHAR(30);

-- Note: Cannot add foreign key constraint to clinic_id alone since clinic_master has composite primary key (clinic_id, doctor_id)
-- Foreign key constraint will be added after ensuring data integrity

-- Drop existing primary key constraint
ALTER TABLE medicine_master 
DROP CONSTRAINT IF EXISTS diagnosis_masterv1_pk;

-- Add new primary key constraint including clinic_id
ALTER TABLE medicine_master 
ADD CONSTRAINT medicine_master_pkey 
PRIMARY KEY (short_description, clinic_id);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_medicine_master_clinic_id ON medicine_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_medicine_master_medicine_clinic ON medicine_master(short_description, clinic_id);

-- Add comments
COMMENT ON COLUMN medicine_master.clinic_id IS 'Clinic ID for multi-clinic support';

-- =====================================================
-- 7. REFERAL_DOCTORS_LIST TABLE (if exists)
-- =====================================================

-- Check if referal_doctors_list table exists and add clinic_id if it does
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables 
               WHERE table_schema = 'climasys_dev' 
               AND table_name = 'referal_doctors_list') THEN
        
        -- Add clinic_id column to referal_doctors_list table
        ALTER TABLE referal_doctors_list 
        ADD COLUMN clinic_id VARCHAR(30);
        
        -- Note: Cannot add foreign key constraint to clinic_id alone since clinic_master has composite primary key (clinic_id, doctor_id)
        -- Foreign key constraint will be added after ensuring data integrity
        
        -- Drop existing primary key constraint (if exists)
        ALTER TABLE referal_doctors_list 
        DROP CONSTRAINT IF EXISTS referal_doctors_list_pkey;
        
        -- Add new primary key constraint including clinic_id
        -- Note: Adjust the primary key columns based on actual table structure
        ALTER TABLE referal_doctors_list 
        ADD CONSTRAINT referal_doctors_list_pkey 
        PRIMARY KEY (id, clinic_id);
        
        -- Create index for performance
        CREATE INDEX IF NOT EXISTS idx_referal_doctors_list_clinic_id ON referal_doctors_list(clinic_id);
        
        -- Add comments
        COMMENT ON COLUMN referal_doctors_list.clinic_id IS 'Clinic ID for multi-clinic support';
        
        RAISE NOTICE 'Successfully added clinic_id to referal_doctors_list table';
    ELSE
        RAISE NOTICE 'referal_doctors_list table does not exist, skipping...';
    END IF;
END $$;

-- =====================================================
-- ADDITIONAL INDEXES FOR PERFORMANCE
-- =====================================================

-- Create composite indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_status_order_doctor_role_clinic ON status_order(doctor_id, role_id, clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_master_doctor_id_clinic ON lab_test_master(doctor_id, clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_doctor_lab_clinic ON lab_test_parameter(doctor_id, lab_test_id, clinic_id);
CREATE INDEX IF NOT EXISTS idx_complaint_master_doctor_clinic ON complaint_master(doctor_id, clinic_id);
CREATE INDEX IF NOT EXISTS idx_medicine_master_medicine_clinic ON medicine_master(short_description, clinic_id);

-- =====================================================
-- UPDATE EXISTING DATA (OPTIONAL)
-- =====================================================

-- Note: You may need to update existing data to set appropriate clinic_id values
-- This should be done based on your business logic and data migration requirements

-- Example for updating existing records (uncomment and modify as needed):
/*
-- Update status_order with default clinic_id
UPDATE status_order SET clinic_id = 'DEFAULT_CLINIC' WHERE clinic_id IS NULL;

-- Update role_master with default clinic_id  
UPDATE role_master SET clinic_id = 'DEFAULT_CLINIC' WHERE clinic_id IS NULL;

-- Update lab_test_master with default clinic_id
UPDATE lab_test_master SET clinic_id = 'DEFAULT_CLINIC' WHERE clinic_id IS NULL;

-- Update lab_test_parameter with default clinic_id
UPDATE lab_test_parameter SET clinic_id = 'DEFAULT_CLINIC' WHERE clinic_id IS NULL;

-- Update complaint_master with default clinic_id
UPDATE complaint_master SET clinic_id = 'DEFAULT_CLINIC' WHERE clinic_id IS NULL;

-- Update medicine_master with default clinic_id
UPDATE medicine_master SET clinic_id = 'DEFAULT_CLINIC' WHERE clinic_id IS NULL;
*/

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Verify that clinic_id columns have been added successfully
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable
FROM information_schema.columns 
WHERE table_schema = 'climasys_dev' 
AND column_name = 'clinic_id'
AND table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'referal_doctors_list')
ORDER BY table_name;

-- Verify primary key constraints
SELECT 
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'climasys_dev' 
AND tc.constraint_type = 'PRIMARY KEY'
AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'referal_doctors_list')
ORDER BY tc.table_name, kcu.ordinal_position;

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================

DO $$
BEGIN
    RAISE NOTICE 'Successfully completed adding clinic_id field to all specified tables';
    RAISE NOTICE 'Primary key constraints have been updated to include clinic_id';
    RAISE NOTICE 'Performance indexes have been created';
    RAISE NOTICE 'IMPORTANT: Foreign key constraints will be added after data migration';
    RAISE NOTICE 'Please run migrate_existing_data_clinic_id.sql to update existing data';
    RAISE NOTICE 'Then run add_foreign_key_constraints_clinic_id.sql to add foreign keys';
END $$;
