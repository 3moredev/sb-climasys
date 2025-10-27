-- =====================================================
-- UPDATED CONSTRAINT DEFINITIONS WITH CLINIC_ID
-- =====================================================
-- This file contains the updated constraint definitions for tables
-- that have been modified to include clinic_id field
-- Based on Climasys_Dev.unknown schema
--
-- =====================================================

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- 1. STATUS_ORDER TABLE CONSTRAINTS
-- =====================================================

-- Original constraint: staus_id_pk PRIMARY KEY (doctor_id, role_id, status_id)
-- Updated constraint: status_order_pkey PRIMARY KEY (doctor_id, role_id, status_id, clinic_id)

-- Drop original constraint
ALTER TABLE ONLY public.status_order
DROP CONSTRAINT IF EXISTS staus_id_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.status_order
ADD CONSTRAINT status_order_pkey PRIMARY KEY (doctor_id, role_id, status_id, clinic_id);

-- Add comment
COMMENT ON CONSTRAINT status_order_pkey ON public.status_order IS 'Updated primary key to include clinic_id for multi-clinic support';

-- =====================================================
-- 2. ROLE_MASTER TABLE CONSTRAINTS
-- =====================================================

-- Original constraint: role_master_pk PRIMARY KEY (role_id)
-- Updated constraint: role_master_pkey PRIMARY KEY (role_id, clinic_id)

-- Drop original constraint
ALTER TABLE ONLY public.role_master
DROP CONSTRAINT IF EXISTS role_master_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.role_master
ADD CONSTRAINT role_master_pkey PRIMARY KEY (role_id, clinic_id);

-- Add comment
COMMENT ON CONSTRAINT role_master_pkey ON public.role_master IS 'Updated primary key to include clinic_id for multi-clinic support';

-- =====================================================
-- 3. LAB_TEST_MASTER TABLE CONSTRAINTS
-- =====================================================

-- Original constraint: lab_test_ref_pk PRIMARY KEY (doctor_id, id)
-- Updated constraint: lab_test_master_pkey PRIMARY KEY (doctor_id, id, clinic_id)

-- Drop original constraint
ALTER TABLE ONLY public.lab_test_master
DROP CONSTRAINT IF EXISTS lab_test_ref_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.lab_test_master
ADD CONSTRAINT lab_test_master_pkey PRIMARY KEY (doctor_id, id, clinic_id);

-- Add comment
COMMENT ON CONSTRAINT lab_test_master_pkey ON public.lab_test_master IS 'Updated primary key to include clinic_id for multi-clinic support';

-- =====================================================
-- 4. LAB_TEST_PARAMETER TABLE CONSTRAINTS
-- =====================================================

-- Original constraint: lab_test_parameter_pk PRIMARY KEY (lab_test_id, doctor_id, id)
-- Updated constraint: lab_test_parameter_pkey PRIMARY KEY (doctor_id, id, lab_test_id, clinic_id)

-- Drop original constraint
ALTER TABLE ONLY public.lab_test_parameter
DROP CONSTRAINT IF EXISTS lab_test_parameter_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.lab_test_parameter
ADD CONSTRAINT lab_test_parameter_pkey PRIMARY KEY (doctor_id, id, lab_test_id, clinic_id);

-- Add comment
COMMENT ON CONSTRAINT lab_test_parameter_pkey ON public.lab_test_parameter IS 'Updated primary key to include clinic_id for multi-clinic support';

-- =====================================================
-- 5. COMPLAINT_MASTER TABLE CONSTRAINTS
-- =====================================================

-- Original constraint: symptom_ref_pk PRIMARY KEY (short_description, doctor_id)
-- Updated constraint: complaint_master_pkey PRIMARY KEY (short_description, doctor_id, clinic_id)

-- Drop original constraint
ALTER TABLE ONLY public.complaint_master
DROP CONSTRAINT IF EXISTS symptom_ref_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.complaint_master
ADD CONSTRAINT complaint_master_pkey PRIMARY KEY (short_description, doctor_id, clinic_id);

-- Add comment
COMMENT ON CONSTRAINT complaint_master_pkey ON public.complaint_master IS 'Updated primary key to include clinic_id for multi-clinic support';

-- =====================================================
-- 6. MEDICINE_MASTER TABLE CONSTRAINTS
-- =====================================================

-- Original constraint: diagnosis_masterv1_pk PRIMARY KEY (short_description, doctor_id)
-- Updated constraint: medicine_master_pkey PRIMARY KEY (short_description, clinic_id)

-- Drop original constraint
ALTER TABLE ONLY public.medicine_master
DROP CONSTRAINT IF EXISTS diagnosis_masterv1_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.medicine_master
ADD CONSTRAINT medicine_master_pkey PRIMARY KEY (short_description, doctor_id, clinic_id);

-- Add comment
COMMENT ON CONSTRAINT medicine_master_pkey ON public.medicine_master IS 'Updated primary key to include clinic_id for multi-clinic support';

-- =====================================================
-- 7. DIAGNOSIS_MASTER TABLE CONSTRAINTS
-- =====================================================

-- Original constraint: symptom_refv1_pk PRIMARY KEY (short_description, doctor_id)
-- Updated constraint: diagnosis_master_pkey PRIMARY KEY (short_description, doctor_id, clinic_id)

-- Drop original constraint
ALTER TABLE ONLY public.diagnosis_master
DROP CONSTRAINT IF EXISTS symptom_refv1_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.diagnosis_master
ADD CONSTRAINT diagnosis_master_pkey PRIMARY KEY (short_description, doctor_id, clinic_id);

-- Add comment
COMMENT ON CONSTRAINT diagnosis_master_pkey ON public.diagnosis_master IS 'Updated primary key to include clinic_id for multi-clinic support';

-- =====================================================
-- PERFORMANCE INDEXES
-- =====================================================

-- Create indexes for optimal performance with clinic_id
CREATE INDEX IF NOT EXISTS idx_status_order_clinic_id ON public.status_order(clinic_id);
CREATE INDEX IF NOT EXISTS idx_status_order_doctor_clinic ON public.status_order(doctor_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_role_master_clinic_id ON public.role_master(clinic_id);

CREATE INDEX IF NOT EXISTS idx_lab_test_master_clinic_id ON public.lab_test_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_master_doctor_clinic ON public.lab_test_master(doctor_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_clinic_id ON public.lab_test_parameter(clinic_id);
CREATE INDEX IF NOT EXISTS idx_lab_test_parameter_doctor_clinic ON public.lab_test_parameter(doctor_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_complaint_master_clinic_id ON public.complaint_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_complaint_master_doctor_clinic ON public.complaint_master(doctor_id, clinic_id);

CREATE INDEX IF NOT EXISTS idx_medicine_master_clinic_id ON public.medicine_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_medicine_master_description_clinic ON public.medicine_master(short_description, clinic_id);

CREATE INDEX IF NOT EXISTS idx_diagnosis_master_clinic_id ON public.diagnosis_master(clinic_id);
CREATE INDEX IF NOT EXISTS idx_diagnosis_master_doctor_clinic ON public.diagnosis_master(doctor_id, clinic_id);
CREATE INDEX IF NOT EXISTS idx_diagnosis_master_description_clinic ON public.diagnosis_master(short_description, clinic_id);

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Verify all primary key constraints have been updated
SELECT 
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type,
    STRING_AGG(kcu.column_name, ', ' ORDER BY kcu.ordinal_position) as columns
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'climasys_dev' 
AND tc.constraint_type = 'PRIMARY KEY'
AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master')
ORDER BY tc.table_name;

-- Verify clinic_id columns exist
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable
FROM information_schema.columns 
WHERE table_schema = 'climasys_dev' 
AND column_name = 'clinic_id'
AND table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master')
ORDER BY table_name;

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================

DO $$
BEGIN
    RAISE NOTICE 'Successfully updated all primary key constraints to include clinic_id';
    RAISE NOTICE 'All performance indexes have been created';
    RAISE NOTICE 'Multi-clinic support is now fully implemented';
END $$;
