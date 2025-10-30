-- Add clinic_id to reference tables used by refdata/symptom APIs
-- Safe to run multiple times: checks presence before adding

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'medicine_master' AND column_name = 'clinic_id'
    ) THEN
        ALTER TABLE public.medicine_master ADD COLUMN clinic_id varchar(10);
        CREATE INDEX IF NOT EXISTS idx_medicine_master_clinic ON public.medicine_master(clinic_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'complaint_master' AND column_name = 'clinic_id'
    ) THEN
        ALTER TABLE public.complaint_master ADD COLUMN clinic_id varchar(10);
        CREATE INDEX IF NOT EXISTS idx_complaint_master_clinic ON public.complaint_master(clinic_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'dressing_master' AND column_name = 'clinic_id'
    ) THEN
        ALTER TABLE public.dressing_master ADD COLUMN clinic_id varchar(10);
        CREATE INDEX IF NOT EXISTS idx_dressing_master_clinic ON public.dressing_master(clinic_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'diagnosis_master' AND column_name = 'clinic_id'
    ) THEN
        ALTER TABLE public.diagnosis_master ADD COLUMN clinic_id varchar(10);
        CREATE INDEX IF NOT EXISTS idx_diagnosis_master_clinic ON public.diagnosis_master(clinic_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'prescription_medicines' AND column_name = 'clinic_id'
    ) THEN
        ALTER TABLE public.prescription_medicines ADD COLUMN clinic_id varchar(10);
        CREATE INDEX IF NOT EXISTS idx_prescription_medicines_clinic ON public.prescription_medicines(clinic_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'doctor_procedure_master' AND column_name = 'clinic_id'
    ) THEN
        ALTER TABLE public.doctor_procedure_master ADD COLUMN clinic_id varchar(10);
        CREATE INDEX IF NOT EXISTS idx_doctor_procedure_master_clinic ON public.doctor_procedure_master(clinic_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'instructions_group_master' AND column_name = 'clinic_id'
    ) THEN
        ALTER TABLE public.instructions_group_master ADD COLUMN clinic_id varchar(10);
        CREATE INDEX IF NOT EXISTS idx_instructions_group_master_clinic ON public.instructions_group_master(clinic_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'billing_details_master' AND column_name = 'clinic_id'
    ) THEN
        ALTER TABLE public.billing_details_master ADD COLUMN clinic_id varchar(10);
        CREATE INDEX IF NOT EXISTS idx_billing_details_master_clinic ON public.billing_details_master(clinic_id);
    END IF;
END $$;

    UPDATE dressing_master SET clinic_id = 'CL-00001' WHERE clinic_id IS NULL;
    UPDATE prescription_medicines SET clinic_id = 'CL-00001' WHERE clinic_id IS NULL;
    UPDATE doctor_procedure_master SET clinic_id = 'CL-00001' WHERE clinic_id IS NULL;
    UPDATE instructions_group_master SET clinic_id = 'CL-00001' WHERE clinic_id IS NULL;
    UPDATE billing_details_master SET clinic_id = 'CL-00001' WHERE clinic_id IS NULL;

-- Add foreign keys to clinic_master(clinic_id) where missing
DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'dressing_master' AND column_name = 'clinic_id'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema
        WHERE tc.table_schema = 'public' AND tc.table_name = 'dressing_master' AND tc.constraint_type = 'FOREIGN KEY' AND kcu.column_name = 'clinic_id'
    ) THEN
        -- Ensure composite index exists to support composite FK
        CREATE INDEX IF NOT EXISTS idx_dressing_master_clinic_doctor ON public.dressing_master(clinic_id, doctor_id);
        ALTER TABLE public.dressing_master
            ADD CONSTRAINT fk_dressing_master_clinic_doctor
            FOREIGN KEY (clinic_id, doctor_id) REFERENCES public.clinic_master(clinic_id, doctor_id);
    END IF;
END $$;

DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'prescription_medicines' AND column_name = 'clinic_id'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema
        WHERE tc.table_schema = 'public' AND tc.table_name = 'prescription_medicines' AND tc.constraint_type = 'FOREIGN KEY' AND kcu.column_name = 'clinic_id'
    ) THEN
        CREATE INDEX IF NOT EXISTS idx_prescription_medicines_clinic_doctor ON public.prescription_medicines(clinic_id, doctor_id);
        ALTER TABLE public.prescription_medicines
            ADD CONSTRAINT fk_prescription_medicines_clinic_doctor
            FOREIGN KEY (clinic_id, doctor_id) REFERENCES public.clinic_master(clinic_id, doctor_id);
    END IF;
END $$;

DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'doctor_procedure_master' AND column_name = 'clinic_id'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema
        WHERE tc.table_schema = 'public' AND tc.table_name = 'doctor_procedure_master' AND tc.constraint_type = 'FOREIGN KEY' AND kcu.column_name = 'clinic_id'
    ) THEN
        CREATE INDEX IF NOT EXISTS idx_doctor_procedure_master_clinic_doctor ON public.doctor_procedure_master(clinic_id, doctor_id);
        ALTER TABLE public.doctor_procedure_master
            ADD CONSTRAINT fk_doctor_procedure_master_clinic_doctor
            FOREIGN KEY (clinic_id, doctor_id) REFERENCES public.clinic_master(clinic_id, doctor_id);
    END IF;
END $$;

DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'instructions_group_master' AND column_name = 'clinic_id'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema
        WHERE tc.table_schema = 'public' AND tc.table_name = 'instructions_group_master' AND tc.constraint_type = 'FOREIGN KEY' AND kcu.column_name = 'clinic_id'
    ) THEN
        CREATE INDEX IF NOT EXISTS idx_instructions_group_master_clinic_doctor ON public.instructions_group_master(clinic_id, doctor_id);
        ALTER TABLE public.instructions_group_master
            ADD CONSTRAINT fk_instructions_group_master_clinic_doctor
            FOREIGN KEY (clinic_id, doctor_id) REFERENCES public.clinic_master(clinic_id, doctor_id);
    END IF;
END $$;

-- Make clinic_id mandatory (NOT NULL) after backfilling default values
DO $$ BEGIN
    BEGIN
        ALTER TABLE public.dressing_master ALTER COLUMN clinic_id SET NOT NULL;
    EXCEPTION WHEN others THEN NULL; END;
    BEGIN
        ALTER TABLE public.prescription_medicines ALTER COLUMN clinic_id SET NOT NULL;
    EXCEPTION WHEN others THEN NULL; END;
    BEGIN
        ALTER TABLE public.doctor_procedure_master ALTER COLUMN clinic_id SET NOT NULL;
    EXCEPTION WHEN others THEN NULL; END;
    BEGIN
        ALTER TABLE public.instructions_group_master ALTER COLUMN clinic_id SET NOT NULL;
    EXCEPTION WHEN others THEN NULL; END;
    BEGIN
        ALTER TABLE public.billing_details_master ALTER COLUMN clinic_id SET NOT NULL;
    EXCEPTION WHEN others THEN NULL; END;
END $$;


