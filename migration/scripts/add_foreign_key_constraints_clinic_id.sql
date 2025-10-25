-- =====================================================
-- ADD FOREIGN KEY CONSTRAINTS FOR CLINIC_ID
-- =====================================================
-- This script adds foreign key constraints after data migration is complete
-- Since clinic_master has composite primary key (clinic_id, doctor_id),
-- we need to create a unique constraint on clinic_id first
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- STEP 1: CREATE UNIQUE CONSTRAINT ON CLINIC_ID IN CLINIC_MASTER
-- =====================================================

-- First, ensure clinic_id is unique in clinic_master
-- This creates a unique constraint that allows foreign key references
ALTER TABLE clinic_master 
ADD CONSTRAINT uk_clinic_master_clinic_id UNIQUE (clinic_id);

-- =====================================================
-- STEP 2: RECREATE ORIGINAL FOREIGN KEY CONSTRAINTS
-- =====================================================

-- Recreate the original foreign key constraint for lab_test_parameter
ALTER TABLE lab_test_parameter 
ADD CONSTRAINT lab_test_parameter_lab_test_ref_fk 
FOREIGN KEY (doctor_id, lab_test_id) REFERENCES lab_test_master(doctor_id, id);

-- Recreate the original foreign key constraint for status_order
ALTER TABLE status_order 
ADD CONSTRAINT status_order_role_master_fk 
FOREIGN KEY (role_id) REFERENCES role_master(role_id);

-- Recreate the original foreign key constraint for user_role
ALTER TABLE user_role 
ADD CONSTRAINT user_role_role_master_fk 
FOREIGN KEY (role_id) REFERENCES role_master(role_id);

-- =====================================================
-- STEP 3: ADD NEW FOREIGN KEY CONSTRAINTS FOR CLINIC_ID
-- =====================================================

-- Add foreign key constraint for status_order
ALTER TABLE status_order 
ADD CONSTRAINT fk_status_order_clinic_id 
FOREIGN KEY (clinic_id) REFERENCES clinic_master(clinic_id);

-- Add foreign key constraint for role_master
ALTER TABLE role_master 
ADD CONSTRAINT fk_role_master_clinic_id 
FOREIGN KEY (clinic_id) REFERENCES clinic_master(clinic_id);

-- Add foreign key constraint for lab_test_master
ALTER TABLE lab_test_master 
ADD CONSTRAINT fk_lab_test_master_clinic_id 
FOREIGN KEY (clinic_id) REFERENCES clinic_master(clinic_id);

-- Add foreign key constraint for lab_test_parameter
ALTER TABLE lab_test_parameter 
ADD CONSTRAINT fk_lab_test_parameter_clinic_id 
FOREIGN KEY (clinic_id) REFERENCES clinic_master(clinic_id);

-- Add foreign key constraint for complaint_master
ALTER TABLE complaint_master 
ADD CONSTRAINT fk_complaint_master_clinic_id 
FOREIGN KEY (clinic_id) REFERENCES clinic_master(clinic_id);

-- Add foreign key constraint for medicine_master
ALTER TABLE medicine_master 
ADD CONSTRAINT fk_medicine_master_clinic_id 
FOREIGN KEY (clinic_id) REFERENCES clinic_master(clinic_id);

-- Add foreign key constraint for referal_doctors_list (if exists)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables 
               WHERE table_schema = 'climasys_dev' 
               AND table_name = 'referal_doctors_list') THEN
        
        ALTER TABLE referal_doctors_list 
        ADD CONSTRAINT fk_referal_doctors_list_clinic_id 
        FOREIGN KEY (clinic_id) REFERENCES clinic_master(clinic_id);
        
        RAISE NOTICE 'Successfully added foreign key constraint to referal_doctors_list';
    ELSE
        RAISE NOTICE 'referal_doctors_list table does not exist, skipping...';
    END IF;
END $$;

-- =====================================================
-- STEP 3: VERIFY FOREIGN KEY CONSTRAINTS
-- =====================================================

-- Check that all foreign key constraints were created successfully
SELECT 
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage ccu 
    ON ccu.constraint_name = tc.constraint_name
WHERE tc.table_schema = 'climasys_dev' 
AND tc.constraint_type = 'FOREIGN KEY'
AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'referal_doctors_list')
ORDER BY tc.table_name, tc.constraint_name;

-- =====================================================
-- STEP 4: VERIFY DATA INTEGRITY
-- =====================================================

-- Check for orphaned clinic_id values
SELECT 'status_order' as table_name, COUNT(*) as orphaned_count
FROM status_order s
LEFT JOIN clinic_master c ON s.clinic_id = c.clinic_id
WHERE c.clinic_id IS NULL AND s.clinic_id IS NOT NULL
UNION ALL
SELECT 'role_master' as table_name, COUNT(*) as orphaned_count
FROM role_master r
LEFT JOIN clinic_master c ON r.clinic_id = c.clinic_id
WHERE c.clinic_id IS NULL AND r.clinic_id IS NOT NULL
UNION ALL
SELECT 'lab_test_master' as table_name, COUNT(*) as orphaned_count
FROM lab_test_master l
LEFT JOIN clinic_master c ON l.clinic_id = c.clinic_id
WHERE c.clinic_id IS NULL AND l.clinic_id IS NOT NULL
UNION ALL
SELECT 'lab_test_parameter' as table_name, COUNT(*) as orphaned_count
FROM lab_test_parameter lp
LEFT JOIN clinic_master c ON lp.clinic_id = c.clinic_id
WHERE c.clinic_id IS NULL AND lp.clinic_id IS NOT NULL
UNION ALL
SELECT 'complaint_master' as table_name, COUNT(*) as orphaned_count
FROM complaint_master cm
LEFT JOIN clinic_master c ON cm.clinic_id = c.clinic_id
WHERE c.clinic_id IS NULL AND cm.clinic_id IS NOT NULL
UNION ALL
SELECT 'medicine_master' as table_name, COUNT(*) as orphaned_count
FROM medicine_master m
LEFT JOIN clinic_master c ON m.clinic_id = c.clinic_id
WHERE c.clinic_id IS NULL AND m.clinic_id IS NOT NULL;

-- =====================================================
-- STEP 5: CREATE ADDITIONAL INDEXES FOR PERFORMANCE
-- =====================================================

-- Create indexes on foreign key columns for better performance
CREATE INDEX IF NOT EXISTS idx_status_order_clinic_id_fk ON status_order(clinic_id);
CREATE INDEX IF NOT EXISTS idx_role_master_clinic_id_fk ON role_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_master_clinic_id_fk ON lab_test_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_clinic_id_fk ON lab_test_parameter(clinic_id);
CREATE INDEX IF NOT EXISTS idx_complaint_master_clinic_id_fk ON complaint_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_medicine_master_clinic_id_fk ON medicine_master(clinic_id);

-- Create index on referal_doctors_list if it exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables 
               WHERE table_schema = 'climasys_dev' 
               AND table_name = 'referal_doctors_list') THEN
        
        CREATE INDEX IF NOT EXISTS idx_referal_doctors_list_clinic_id_fk ON referal_doctors_list(clinic_id);
        
        RAISE NOTICE 'Successfully created index on referal_doctors_list';
    END IF;
END $$;

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================

DO $$
BEGIN
    RAISE NOTICE 'Successfully added foreign key constraints for clinic_id';
    RAISE NOTICE 'All tables now have proper referential integrity';
    RAISE NOTICE 'Performance indexes have been created';
    RAISE NOTICE 'Please verify data integrity using the provided queries';
END $$;
