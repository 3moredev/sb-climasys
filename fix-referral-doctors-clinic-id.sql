-- Fix for referral doctors clinic_id column issue
-- This script adds the missing clinic_id column to referrel_doctors_list table

-- Check if the table exists and add clinic_id column
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables 
               WHERE table_schema = 'public' 
               AND table_name = 'referrel_doctors_list') THEN
        
        -- Check if clinic_id column already exists
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_schema = 'public' 
                      AND table_name = 'referrel_doctors_list' 
                      AND column_name = 'clinic_id') THEN
            
            -- Add clinic_id column to referrel_doctors_list table (nullable initially)
            ALTER TABLE public.referrel_doctors_list 
            ADD COLUMN clinic_id VARCHAR(30);
            
            -- Set a default clinic_id for existing records (you may need to adjust this)
            UPDATE public.referrel_doctors_list 
            SET clinic_id = 'CL-00001' 
            WHERE clinic_id IS NULL;
            
            -- Make clinic_id NOT NULL after setting default values
            ALTER TABLE public.referrel_doctors_list 
            ALTER COLUMN clinic_id SET NOT NULL;
            
            -- Create index for performance
            CREATE INDEX IF NOT EXISTS idx_referrel_doctors_list_clinic_id 
            ON public.referrel_doctors_list(clinic_id);
            
            -- Add comment
            COMMENT ON COLUMN public.referrel_doctors_list.clinic_id IS 'Clinic ID for multi-clinic support';
            
            RAISE NOTICE 'Successfully added clinic_id column to referrel_doctors_list table';
        ELSE
            RAISE NOTICE 'clinic_id column already exists in referrel_doctors_list table';
        END IF;
    ELSE
        RAISE NOTICE 'referrel_doctors_list table does not exist, skipping...';
    END IF;
END $$;

-- =====================================================
-- FOREIGN KEY CONSTRAINT CONSIDERATIONS
-- =====================================================

-- IMPORTANT: clinic_master table has a composite primary key (clinic_id, doctor_id)
-- This means we CANNOT create a simple foreign key constraint from 
-- referrel_doctors_list.clinic_id to clinic_master.clinic_id alone.

-- Option 1: Add doctor_id column and create composite foreign key
-- Option 2: Use application-level validation (recommended for this case)

-- Since referral doctors are not necessarily tied to specific doctors within a clinic,
-- we'll use application-level validation instead of database constraints.

-- Application-level validation query (use in your service layer):
-- SELECT COUNT(*) FROM clinic_master WHERE clinic_id = ?;

-- This approach allows:
-- 1. Referral doctors to be associated with clinics without specific doctor assignments
-- 2. Multiple doctors per clinic (which is the normal case)
-- 3. Flexible data management without complex constraint dependencies

-- Verify the column was added
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_schema = 'public' 
AND table_name = 'referrel_doctors_list'
ORDER BY ordinal_position;
