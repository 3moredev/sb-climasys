-- =====================================================
-- COMPLETE CLINIC_ID MIGRATION SCRIPT
-- =====================================================
-- This script handles the complete migration including dependency management
-- It drops foreign key constraints, primary keys, adds clinic_id, and recreates everything
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- STEP 1: DROP ALL DEPENDENT FOREIGN KEY CONSTRAINTS
-- =====================================================

-- Drop foreign key constraints that depend on primary keys we're modifying
ALTER TABLE lab_test_parameter 
DROP CONSTRAINT IF EXISTS lab_test_parameter_lab_test_ref_fk;

ALTER TABLE status_order 
DROP CONSTRAINT IF EXISTS status_order_role_master_fk;

ALTER TABLE user_role 
DROP CONSTRAINT IF EXISTS user_role_role_master_fk;

-- Dynamically find and drop any other foreign key constraints that might depend on our primary keys
DO $$
DECLARE
    constraint_record RECORD;
BEGIN
    -- Find all foreign key constraints that reference the tables we're modifying
    FOR constraint_record IN
        SELECT 
            tc.table_name,
            tc.constraint_name,
            ccu.table_name AS foreign_table_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.constraint_column_usage ccu 
            ON ccu.constraint_name = tc.constraint_name
        WHERE tc.constraint_type = 'FOREIGN KEY'
        AND tc.table_schema = 'climasys_dev'
        AND ccu.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master')
    LOOP
        EXECUTE 'ALTER TABLE ' || constraint_record.table_name || ' DROP CONSTRAINT IF EXISTS ' || constraint_record.constraint_name;
        RAISE NOTICE 'Dropped foreign key constraint: % on table %', constraint_record.constraint_name, constraint_record.table_name;
    END LOOP;
END $$;

-- =====================================================
-- STEP 1.5: VERIFY ALL FOREIGN KEY CONSTRAINTS ARE DROPPED
-- =====================================================

-- Check if any foreign key constraints still reference our tables
DO $$
DECLARE
    constraint_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO constraint_count
    FROM information_schema.table_constraints tc
    JOIN information_schema.constraint_column_usage ccu 
        ON ccu.constraint_name = tc.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY'
    AND tc.table_schema = 'climasys_dev'
    AND ccu.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master');
    
    IF constraint_count > 0 THEN
        RAISE WARNING 'Found % foreign key constraints still referencing our tables. This may cause issues.', constraint_count;
    ELSE
        RAISE NOTICE 'All foreign key constraints have been successfully dropped.';
    END IF;
END $$;

-- =====================================================
-- STEP 2: DROP ALL PRIMARY KEY CONSTRAINTS DYNAMICALLY
-- =====================================================

-- Dynamically find and drop all primary key constraints on our tables
DO $$
DECLARE
    constraint_record RECORD;
    drop_count INTEGER := 0;
BEGIN
    -- Find all primary key constraints on the tables we're modifying
    FOR constraint_record IN
        SELECT 
            tc.table_name,
            tc.constraint_name
        FROM information_schema.table_constraints tc
        WHERE tc.constraint_type = 'PRIMARY KEY'
        AND tc.table_schema = 'climasys_dev'
        AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master', 'referal_doctors_list')
    LOOP
        BEGIN
            EXECUTE 'ALTER TABLE ' || constraint_record.table_name || ' DROP CONSTRAINT ' || constraint_record.constraint_name || ' CASCADE';
            RAISE NOTICE 'Dropped primary key constraint: % on table %', constraint_record.constraint_name, constraint_record.table_name;
            drop_count := drop_count + 1;
        EXCEPTION
            WHEN OTHERS THEN
                RAISE WARNING 'Failed to drop constraint % on table %: %', constraint_record.constraint_name, constraint_record.table_name, SQLERRM;
        END;
    END LOOP;
    
    RAISE NOTICE 'Successfully dropped % primary key constraints', drop_count;
END $$;

-- Also try to drop the known constraint names as a fallback
ALTER TABLE status_order DROP CONSTRAINT IF EXISTS staus_id_pk CASCADE;
ALTER TABLE role_master DROP CONSTRAINT IF EXISTS role_master_pk CASCADE;
ALTER TABLE lab_test_master DROP CONSTRAINT IF EXISTS lab_test_ref_pk CASCADE;
ALTER TABLE lab_test_parameter DROP CONSTRAINT IF EXISTS lab_test_parameter_pk CASCADE;
ALTER TABLE complaint_master DROP CONSTRAINT IF EXISTS symptom_ref_pk CASCADE;
ALTER TABLE medicine_master DROP CONSTRAINT IF EXISTS diagnosis_masterv1_pk CASCADE;

-- Additional aggressive approach: Try to drop any remaining constraints by checking pg_constraint directly
DO $$
DECLARE
    constraint_record RECORD;
BEGIN
    -- Check pg_constraint directly for any remaining primary key constraints
    FOR constraint_record IN
        SELECT 
            c.relname as table_name,
            con.conname as constraint_name
        FROM pg_constraint con
        JOIN pg_class c ON con.conrelid = c.oid
        JOIN pg_namespace n ON c.relnamespace = n.oid
        WHERE con.contype = 'p'  -- Primary key constraint type
        AND n.nspname = 'climasys_dev'
        AND c.relname IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master', 'referal_doctors_list')
    LOOP
        BEGIN
            EXECUTE 'ALTER TABLE ' || constraint_record.table_name || ' DROP CONSTRAINT ' || constraint_record.constraint_name || ' CASCADE';
            RAISE NOTICE 'Dropped remaining primary key constraint: % on table %', constraint_record.constraint_name, constraint_record.table_name;
        EXCEPTION
            WHEN OTHERS THEN
                RAISE WARNING 'Failed to drop remaining constraint % on table %: %', constraint_record.constraint_name, constraint_record.table_name, SQLERRM;
        END;
    END LOOP;
END $$;

-- =====================================================
-- STEP 2.5: VERIFY ALL PRIMARY KEY CONSTRAINTS ARE DROPPED
-- =====================================================

-- Check if any primary key constraints still exist on our tables
DO $$
DECLARE
    constraint_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO constraint_count
    FROM information_schema.table_constraints tc
    WHERE tc.constraint_type = 'PRIMARY KEY'
    AND tc.table_schema = 'climasys_dev'
    AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master', 'referal_doctors_list');
    
    IF constraint_count > 0 THEN
        RAISE WARNING 'Found % primary key constraints still on our tables. This may cause issues.', constraint_count;
        
        -- List the remaining constraints from information_schema
        FOR constraint_record IN
            SELECT tc.table_name, tc.constraint_name
            FROM information_schema.table_constraints tc
            WHERE tc.constraint_type = 'PRIMARY KEY'
            AND tc.table_schema = 'climasys_dev'
            AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master', 'referal_doctors_list')
        LOOP
            RAISE WARNING 'Remaining primary key (info_schema): % on table %', constraint_record.constraint_name, constraint_record.table_name;
        END LOOP;
        
        -- Also check pg_constraint directly
        FOR constraint_record IN
            SELECT 
                c.relname as table_name,
                con.conname as constraint_name
            FROM pg_constraint con
            JOIN pg_class c ON con.conrelid = c.oid
            JOIN pg_namespace n ON c.relnamespace = n.oid
            WHERE con.contype = 'p'
            AND n.nspname = 'climasys_dev'
            AND c.relname IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master', 'referal_doctors_list')
        LOOP
            RAISE WARNING 'Remaining primary key (pg_constraint): % on table %', constraint_record.constraint_name, constraint_record.table_name;
        END LOOP;
    ELSE
        RAISE NOTICE 'All primary key constraints have been successfully dropped.';
    END IF;
END $$;

-- =====================================================
-- STEP 3: ADD CLINIC_ID COLUMNS
-- =====================================================

-- Add clinic_id column to all tables
ALTER TABLE status_order 
ADD COLUMN clinic_id VARCHAR(30);

ALTER TABLE role_master 
ADD COLUMN clinic_id VARCHAR(30);

ALTER TABLE lab_test_master 
ADD COLUMN clinic_id VARCHAR(30);

ALTER TABLE lab_test_parameter 
ADD COLUMN clinic_id VARCHAR(30);

ALTER TABLE complaint_master 
ADD COLUMN clinic_id VARCHAR(30);

ALTER TABLE medicine_master 
ADD COLUMN clinic_id VARCHAR(30);

ALTER TABLE diagnosis_master 
ADD COLUMN clinic_id VARCHAR(30);

-- Add clinic_id to referal_doctors_list if it exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables 
               WHERE table_schema = 'climasys_dev' 
               AND table_name = 'referal_doctors_list') THEN
        
        ALTER TABLE referal_doctors_list 
        ADD COLUMN clinic_id VARCHAR(30);
        
        RAISE NOTICE 'Successfully added clinic_id to referal_doctors_list table';
    ELSE
        RAISE NOTICE 'referal_doctors_list table does not exist, skipping...';
    END IF;
END $$;

-- =====================================================
-- STEP 4: CREATE NEW PRIMARY KEY CONSTRAINTS
-- =====================================================

-- Verify no primary key constraints exist before creating new ones
DO $$
DECLARE
    constraint_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO constraint_count
    FROM information_schema.table_constraints tc
    WHERE tc.constraint_type = 'PRIMARY KEY'
    AND tc.table_schema = 'climasys_dev'
    AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master', 'referal_doctors_list');
    
    IF constraint_count > 0 THEN
        RAISE EXCEPTION 'Cannot create new primary key constraints. % primary key constraints still exist on our tables.', constraint_count;
    END IF;
END $$;

-- Add new primary key constraints including clinic_id
ALTER TABLE status_order 
ADD CONSTRAINT status_order_pkey 
PRIMARY KEY (doctor_id, role_id, status_id, clinic_id);

ALTER TABLE role_master 
ADD CONSTRAINT role_master_pkey 
PRIMARY KEY (role_id, clinic_id);

ALTER TABLE lab_test_master 
ADD CONSTRAINT lab_test_master_pkey 
PRIMARY KEY (doctor_id, id, clinic_id);

ALTER TABLE lab_test_parameter 
ADD CONSTRAINT lab_test_parameter_pkey 
PRIMARY KEY (doctor_id, id, lab_test_id, clinic_id);

ALTER TABLE complaint_master 
ADD CONSTRAINT complaint_master_pkey 
PRIMARY KEY (short_description, doctor_id, clinic_id);

ALTER TABLE medicine_master 
ADD CONSTRAINT medicine_master_pkey 
PRIMARY KEY (short_description, clinic_id);

ALTER TABLE diagnosis_master 
ADD CONSTRAINT diagnosis_master_pkey 
PRIMARY KEY (short_description, doctor_id, clinic_id);

-- Add primary key for referal_doctors_list if it exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables 
               WHERE table_schema = 'climasys_dev' 
               AND table_name = 'referal_doctors_list') THEN
        
        ALTER TABLE referal_doctors_list 
        ADD CONSTRAINT referal_doctors_list_pkey 
        PRIMARY KEY (id, clinic_id);
        
        RAISE NOTICE 'Successfully added primary key to referal_doctors_list table';
    END IF;
END $$;

-- =====================================================
-- STEP 5: CREATE PERFORMANCE INDEXES
-- =====================================================

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_status_order_clinic_id ON status_order(clinic_id);
CREATE INDEX IF NOT EXISTS idx_status_order_doctor_clinic ON status_order(doctor_id, clinic_id);
CREATE INDEX IF NOT EXISTS idx_status_order_doctor_role_clinic ON status_order(doctor_id, role_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_role_master_clinic_id ON role_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_role_master_role_clinic ON role_master(role_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_lab_test_master_clinic_id ON lab_test_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_master_doctor_clinic ON lab_test_master(doctor_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_clinic_id ON lab_test_parameter(clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_doctor_clinic ON lab_test_parameter(doctor_id, clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_doctor_lab_clinic ON lab_test_parameter(doctor_id, lab_test_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_complaint_master_clinic_id ON complaint_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_complaint_master_doctor_clinic ON complaint_master(doctor_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_medicine_master_clinic_id ON medicine_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_medicine_master_medicine_clinic ON medicine_master(short_description, clinic_id);

CREATE INDEX IF NOT EXISTS idx_diagnosis_master_clinic_id ON diagnosis_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_diagnosis_master_doctor_clinic ON diagnosis_master(doctor_id, clinic_id);
CREATE INDEX IF NOT EXISTS idx_diagnosis_master_description_clinic ON diagnosis_master(short_description, clinic_id);

-- Create indexes for referal_doctors_list if it exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables 
               WHERE table_schema = 'climasys_dev' 
               AND table_name = 'referal_doctors_list') THEN
        
        CREATE INDEX IF NOT EXISTS idx_referal_doctors_list_clinic_id ON referal_doctors_list(clinic_id);
        
        RAISE NOTICE 'Successfully created indexes for referal_doctors_list';
    END IF;
END $$;

-- =====================================================
-- STEP 6: ADD COMMENTS
-- =====================================================

-- Add column comments
COMMENT ON COLUMN status_order.clinic_id IS 'Clinic ID for multi-clinic support';
COMMENT ON COLUMN role_master.clinic_id IS 'Clinic ID for multi-clinic support';
COMMENT ON COLUMN lab_test_master.clinic_id IS 'Clinic ID for multi-clinic support';
COMMENT ON COLUMN lab_test_parameter.clinic_id IS 'Clinic ID for multi-clinic support';
COMMENT ON COLUMN complaint_master.clinic_id IS 'Clinic ID for multi-clinic support';
COMMENT ON COLUMN medicine_master.clinic_id IS 'Clinic ID for multi-clinic support';

-- =====================================================
-- STEP 7: VERIFICATION QUERIES
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
AND table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master', 'referal_doctors_list')
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
AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master', 'referal_doctors_list')
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
