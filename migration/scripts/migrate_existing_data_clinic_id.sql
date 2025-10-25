-- =====================================================
-- DATA MIGRATION SCRIPT FOR CLINIC_ID FIELD
-- =====================================================
-- This script handles migration of existing data after adding clinic_id field
-- It provides options for setting default clinic_id values
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- OPTION 1: SET DEFAULT CLINIC_ID FOR ALL RECORDS
-- =====================================================
-- Uncomment and modify the clinic_id value as needed


-- Update status_order with default clinic_id
UPDATE status_order 
SET clinic_id = 'CL-00001' 
WHERE clinic_id IS NULL;

-- Update role_master with default clinic_id  
UPDATE role_master 
SET clinic_id = 'CL-00001' 
WHERE clinic_id IS NULL;

-- Update lab_test_master with default clinic_id
UPDATE lab_test_master 
SET clinic_id = 'CL-00001' 
WHERE clinic_id IS NULL;

-- Update lab_test_parameter with default clinic_id
UPDATE lab_test_parameter 
SET clinic_id = 'CL-00001' 
WHERE clinic_id IS NULL;

-- Update complaint_master with default clinic_id
UPDATE complaint_master 
SET clinic_id = 'CL-00001' 
WHERE clinic_id IS NULL;

-- Update medicine_master with default clinic_id
UPDATE medicine_master 
SET clinic_id = 'CL-00001' 
WHERE clinic_id IS NULL;

-- Update referal_doctors_list with default clinic_id (if exists)
UPDATE referal_doctors_list 
SET clinic_id = 'CL-00001' 
WHERE clinic_id IS NULL;


-- =====================================================
-- OPTION 2: SET CLINIC_ID BASED ON DOCTOR_ID
-- =====================================================
-- This approach sets clinic_id based on the doctor's primary clinic
-- Uncomment and modify as needed

/*
-- Update status_order based on doctor's clinic
UPDATE status_order 
SET clinic_id = (
    SELECT c.clinic_id 
    FROM clinic_master c 
    WHERE c.doctor_id = status_order.doctor_id 
    LIMIT 1
)
WHERE clinic_id IS NULL;

-- Update lab_test_master based on doctor's clinic
UPDATE lab_test_master 
SET clinic_id = (
    SELECT c.clinic_id 
    FROM clinic_master c 
    WHERE c.doctor_id = lab_test_master.doctor_id 
    LIMIT 1
)
WHERE clinic_id IS NULL;

-- Update lab_test_parameter based on doctor's clinic
UPDATE lab_test_parameter 
SET clinic_id = (
    SELECT c.clinic_id 
    FROM clinic_master c 
    WHERE c.doctor_id = lab_test_parameter.doctor_id 
    LIMIT 1
)
WHERE clinic_id IS NULL;

-- Update complaint_master based on doctor's clinic
UPDATE complaint_master 
SET clinic_id = (
    SELECT c.clinic_id 
    FROM clinic_master c 
    WHERE c.doctor_id = complaint_master.doctor_id 
    LIMIT 1
)
WHERE clinic_id IS NULL;
*/

-- =====================================================
-- OPTION 3: SET CLINIC_ID BASED ON USER_ROLE TABLE
-- =====================================================
-- This approach uses the user_role table to determine clinic_id
-- Uncomment and modify as needed

/*
-- Update status_order based on user_role
UPDATE status_order 
SET clinic_id = (
    SELECT ur.clinic_id 
    FROM user_role ur 
    JOIN user_master um ON ur.user_id = um.id
    WHERE um.doctor_id = status_order.doctor_id 
    AND ur.role_id = status_order.role_id
    LIMIT 1
)
WHERE clinic_id IS NULL;

-- Update role_master based on user_role
UPDATE role_master 
SET clinic_id = (
    SELECT ur.clinic_id 
    FROM user_role ur 
    WHERE ur.role_id = role_master.role_id
    LIMIT 1
)
WHERE clinic_id IS NULL;
*/

-- =====================================================
-- OPTION 4: MANUAL CLINIC_ID ASSIGNMENT
-- =====================================================
-- Use this approach for specific clinic assignments
-- Modify the WHERE conditions and clinic_id values as needed

/*
-- Example: Assign specific clinic_id to specific doctors
UPDATE status_order 
SET clinic_id = 'CLINIC_001' 
WHERE doctor_id = 'DR-00001' AND clinic_id IS NULL;

UPDATE status_order 
SET clinic_id = 'CLINIC_002' 
WHERE doctor_id = 'DR-00002' AND clinic_id IS NULL;

-- Example: Assign specific clinic_id to specific roles
UPDATE role_master 
SET clinic_id = 'CLINIC_001' 
WHERE role_id = 1 AND clinic_id IS NULL;

UPDATE role_master 
SET clinic_id = 'CLINIC_002' 
WHERE role_id = 2 AND clinic_id IS NULL;
*/

-- =====================================================
-- DATA VALIDATION QUERIES
-- =====================================================

-- Check for records with NULL clinic_id
SELECT 'status_order' as table_name, COUNT(*) as null_clinic_count 
FROM status_order WHERE clinic_id IS NULL
UNION ALL
SELECT 'role_master' as table_name, COUNT(*) as null_clinic_count 
FROM role_master WHERE clinic_id IS NULL
UNION ALL
SELECT 'lab_test_master' as table_name, COUNT(*) as null_clinic_count 
FROM lab_test_master WHERE clinic_id IS NULL
UNION ALL
SELECT 'lab_test_parameter' as table_name, COUNT(*) as null_clinic_count 
FROM lab_test_parameter WHERE clinic_id IS NULL
UNION ALL
SELECT 'complaint_master' as table_name, COUNT(*) as null_clinic_count 
FROM complaint_master WHERE clinic_id IS NULL
UNION ALL
SELECT 'medicine_master' as table_name, COUNT(*) as null_clinic_count 
FROM medicine_master WHERE clinic_id IS NULL;

-- Check clinic_id distribution
SELECT 
    clinic_id,
    COUNT(*) as record_count
FROM status_order 
GROUP BY clinic_id
ORDER BY clinic_id;

-- Check for orphaned clinic_id values (clinic_id that don't exist in clinic_master)
SELECT DISTINCT s.clinic_id
FROM status_order s
LEFT JOIN clinic_master c ON s.clinic_id = c.clinic_id
WHERE c.clinic_id IS NULL AND s.clinic_id IS NOT NULL;

-- =====================================================
-- CLEANUP SCRIPTS (if needed)
-- =====================================================

-- Remove records with invalid clinic_id (uncomment if needed)
/*
DELETE FROM status_order 
WHERE clinic_id NOT IN (SELECT clinic_id FROM clinic_master);

DELETE FROM role_master 
WHERE clinic_id NOT IN (SELECT clinic_id FROM clinic_master);

DELETE FROM lab_test_master 
WHERE clinic_id NOT IN (SELECT clinic_id FROM clinic_master);

DELETE FROM lab_test_parameter 
WHERE clinic_id NOT IN (SELECT clinic_id FROM clinic_master);

DELETE FROM complaint_master 
WHERE clinic_id NOT IN (SELECT clinic_id FROM clinic_master);

DELETE FROM medicine_master 
WHERE clinic_id NOT IN (SELECT clinic_id FROM clinic_master);
*/

-- =====================================================
-- FINAL VERIFICATION
-- =====================================================

-- Verify all records have clinic_id
DO $$
DECLARE
    null_count INTEGER;
BEGIN
    -- Check status_order
    SELECT COUNT(*) INTO null_count FROM status_order WHERE clinic_id IS NULL;
    IF null_count > 0 THEN
        RAISE WARNING 'status_order has % records with NULL clinic_id', null_count;
    END IF;
    
    -- Check role_master
    SELECT COUNT(*) INTO null_count FROM role_master WHERE clinic_id IS NULL;
    IF null_count > 0 THEN
        RAISE WARNING 'role_master has % records with NULL clinic_id', null_count;
    END IF;
    
    -- Check lab_test_master
    SELECT COUNT(*) INTO null_count FROM lab_test_master WHERE clinic_id IS NULL;
    IF null_count > 0 THEN
        RAISE WARNING 'lab_test_master has % records with NULL clinic_id', null_count;
    END IF;
    
    -- Check lab_test_parameter
    SELECT COUNT(*) INTO null_count FROM lab_test_parameter WHERE clinic_id IS NULL;
    IF null_count > 0 THEN
        RAISE WARNING 'lab_test_parameter has % records with NULL clinic_id', null_count;
    END IF;
    
    -- Check complaint_master
    SELECT COUNT(*) INTO null_count FROM complaint_master WHERE clinic_id IS NULL;
    IF null_count > 0 THEN
        RAISE WARNING 'complaint_master has % records with NULL clinic_id', null_count;
    END IF;
    
    -- Check medicine_master
    SELECT COUNT(*) INTO null_count FROM medicine_master WHERE clinic_id IS NULL;
    IF null_count > 0 THEN
        RAISE WARNING 'medicine_master has % records with NULL clinic_id', null_count;
    END IF;
    
    RAISE NOTICE 'Data migration verification completed';
END $$;

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================

DO $$
BEGIN
    RAISE NOTICE 'Data migration script completed';
    RAISE NOTICE 'Please review the results and run appropriate migration options';
    RAISE NOTICE 'Ensure all records have valid clinic_id values before proceeding';
END $$;
