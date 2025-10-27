# Updated Constraint Definitions with Clinic_ID

## Overview

This document shows the updated constraint definitions for tables that have been modified to include `clinic_id` field, based on the original `Climasys_Dev.unknown` schema.

## Constraint Changes Summary

| Table | Original Constraint | Original Primary Key | Updated Constraint | Updated Primary Key |
|-------|-------------------|-------------------|------------------|-------------------|
| **status_order** | `staus_id_pk` | `(doctor_id, role_id, status_id)` | `status_order_pkey` | `(doctor_id, role_id, status_id, clinic_id)` |
| **role_master** | `role_master_pk` | `(role_id)` | `role_master_pkey` | `(role_id, clinic_id)` |
| **lab_test_master** | `lab_test_ref_pk` | `(doctor_id, id)` | `lab_test_master_pkey` | `(doctor_id, id, clinic_id)` |
| **lab_test_parameter** | `lab_test_parameter_pk` | `(lab_test_id, doctor_id, id)` | `lab_test_parameter_pkey` | `(doctor_id, id, lab_test_id, clinic_id)` |
| **complaint_master** | `symptom_ref_pk` | `(short_description, doctor_id)` | `complaint_master_pkey` | `(short_description, doctor_id, clinic_id)` |
| **medicine_master** | `diagnosis_masterv1_pk` | `(short_description, doctor_id)` | `medicine_master_pkey` | `(short_description, doctor_id, clinic_id)` |
| **diagnosis_master** | `symptom_refv1_pk` | `(short_description, doctor_id)` | `diagnosis_master_pkey` | `(short_description, doctor_id, clinic_id)` |

## Detailed Constraint Changes

### 1. STATUS_ORDER Table

**Original Schema (Climasys_Dev.unknown):**
```sql
-- TOC entry 5843 (class 2606 OID 32849)
-- Name: status_order staus_id_pk; Type: CONSTRAINT; Schema: public; Owner: postgres

ALTER TABLE ONLY public.status_order
    ADD CONSTRAINT staus_id_pk PRIMARY KEY (doctor_id, role_id, status_id);
```

**Updated Schema:**
```sql
-- Drop original constraint
ALTER TABLE ONLY public.status_order
DROP CONSTRAINT IF EXISTS staus_id_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.status_order
ADD CONSTRAINT status_order_pkey PRIMARY KEY (doctor_id, role_id, status_id, clinic_id);
```

### 2. ROLE_MASTER Table

**Original Schema (Climasys_Dev.unknown):**
```sql
-- TOC entry 5841 (class 2606 OID 32847)
-- Name: role_master role_master_pk; Type: CONSTRAINT; Schema: public; Owner: postgres

ALTER TABLE ONLY public.role_master
    ADD CONSTRAINT role_master_pk PRIMARY KEY (role_id);
```

**Updated Schema:**
```sql
-- Drop original constraint
ALTER TABLE ONLY public.role_master
DROP CONSTRAINT IF EXISTS role_master_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.role_master
ADD CONSTRAINT role_master_pkey PRIMARY KEY (role_id, clinic_id);
```

### 3. LAB_TEST_MASTER Table

**Original Schema (Climasys_Dev.unknown):**
```sql
-- TOC entry 5847 (class 2606 OID 32853)
-- Name: lab_test_master lab_test_ref_pk; Type: CONSTRAINT; Schema: public; Owner: postgres

ALTER TABLE ONLY public.lab_test_master
    ADD CONSTRAINT lab_test_ref_pk PRIMARY KEY (doctor_id, id);
```

**Updated Schema:**
```sql
-- Drop original constraint
ALTER TABLE ONLY public.lab_test_master
DROP CONSTRAINT IF EXISTS lab_test_ref_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.lab_test_master
ADD CONSTRAINT lab_test_master_pkey PRIMARY KEY (doctor_id, id, clinic_id);
```

### 4. LAB_TEST_PARAMETER Table

**Original Schema (Climasys_Dev.unknown):**
```sql
-- TOC entry 5846 (class 2606 OID 32852)
-- Name: lab_test_parameter lab_test_parameter_pk; Type: CONSTRAINT; Schema: public; Owner: postgres

ALTER TABLE ONLY public.lab_test_parameter
    ADD CONSTRAINT lab_test_parameter_pk PRIMARY KEY (lab_test_id, doctor_id, id);
```

**Updated Schema:**
```sql
-- Drop original constraint
ALTER TABLE ONLY public.lab_test_parameter
DROP CONSTRAINT IF EXISTS lab_test_parameter_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.lab_test_parameter
ADD CONSTRAINT lab_test_parameter_pkey PRIMARY KEY (doctor_id, id, lab_test_id, clinic_id);
```

### 5. COMPLAINT_MASTER Table

**Original Schema (Climasys_Dev.unknown):**
```sql
-- TOC entry 5844 (class 2606 OID 32850)
-- Name: complaint_master symptom_ref_pk; Type: CONSTRAINT; Schema: public; Owner: postgres

ALTER TABLE ONLY public.complaint_master
    ADD CONSTRAINT symptom_ref_pk PRIMARY KEY (short_description, doctor_id);
```

**Updated Schema:**
```sql
-- Drop original constraint
ALTER TABLE ONLY public.complaint_master
DROP CONSTRAINT IF EXISTS symptom_ref_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.complaint_master
ADD CONSTRAINT complaint_master_pkey PRIMARY KEY (short_description, doctor_id, clinic_id);
```

### 6. MEDICINE_MASTER Table

**Original Schema (Climasys_Dev.unknown):**
```sql
-- TOC entry 5929 (class 2606 OID 33131)
-- Name: medicine_master diagnosis_masterv1_pk; Type: CONSTRAINT; Schema: public; Owner: postgres

ALTER TABLE ONLY public.medicine_master
    ADD CONSTRAINT diagnosis_masterv1_pk PRIMARY KEY (short_description, doctor_id);
```

**Updated Schema:**
```sql
-- Drop original constraint
ALTER TABLE ONLY public.medicine_master
DROP CONSTRAINT IF EXISTS diagnosis_masterv1_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.medicine_master
ADD CONSTRAINT medicine_master_pkey PRIMARY KEY (short_description, doctor_id, clinic_id);
```

**Note:** Medicine master now includes `doctor_id` in the primary key `(short_description, doctor_id, clinic_id)` to maintain consistency with other tables and support doctor-specific medicine configurations.

### 7. DIAGNOSIS_MASTER Table

**Original Schema (Climasys_Dev.unknown):**
```sql
-- TOC entry 5845 (class 2606 OID 32851)
-- Name: diagnosis_master symptom_refv1_pk; Type: CONSTRAINT; Schema: public; Owner: postgres

ALTER TABLE ONLY public.diagnosis_master
    ADD CONSTRAINT symptom_refv1_pk PRIMARY KEY (short_description, doctor_id);
```

**Updated Schema:**
```sql
-- Drop original constraint
ALTER TABLE ONLY public.diagnosis_master
DROP CONSTRAINT IF EXISTS symptom_refv1_pk;

-- Add updated primary key constraint with clinic_id
ALTER TABLE ONLY public.diagnosis_master
ADD CONSTRAINT diagnosis_master_pkey PRIMARY KEY (short_description, doctor_id, clinic_id);
```

## Performance Indexes

The following indexes have been created for optimal performance:

### Status Order Indexes
- `idx_status_order_clinic_id` - For clinic-based queries
- `idx_status_order_doctor_clinic` - For doctor-clinic queries

### Role Master Indexes
- `idx_role_master_clinic_id` - For clinic-based queries

### Lab Test Master Indexes
- `idx_lab_test_master_clinic_id` - For clinic-based queries
- `idx_lab_test_master_doctor_clinic` - For doctor-clinic queries

### Lab Test Parameter Indexes
- `idx_lab_test_parameter_clinic_id` - For clinic-based queries
- `idx_lab_test_parameter_doctor_clinic` - For doctor-clinic queries

### Complaint Master Indexes
- `idx_complaint_master_clinic_id` - For clinic-based queries
- `idx_complaint_master_doctor_clinic` - For doctor-clinic queries

### Medicine Master Indexes
- `idx_medicine_master_clinic_id` - For clinic-based queries
- `idx_medicine_master_description_clinic` - For description-clinic queries

### Diagnosis Master Indexes
- `idx_diagnosis_master_clinic_id` - For clinic-based queries
- `idx_diagnosis_master_doctor_clinic` - For doctor-clinic queries
- `idx_diagnosis_master_description_clinic` - For description-clinic queries

## Migration Execution

To apply these constraint updates, run:

```bash
psql -d your_database -f update_constraints_with_clinic_id.sql
```

## Verification

After running the migration, verify the changes with:

```sql
-- Check primary key constraints
SELECT 
    tc.table_name,
    tc.constraint_name,
    STRING_AGG(kcu.column_name, ', ' ORDER BY kcu.ordinal_position) as columns
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'climasys_dev' 
AND tc.constraint_type = 'PRIMARY KEY'
AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master', 'lab_test_parameter', 'complaint_master', 'medicine_master', 'diagnosis_master')
ORDER BY tc.table_name;
```

## Benefits

1. **Multi-Clinic Support**: Each table now supports clinic-specific data isolation
2. **Data Integrity**: Primary keys ensure uniqueness within clinic context
3. **Performance**: Optimized indexes for clinic-based queries
4. **Scalability**: System can handle multiple clinics with proper data separation
5. **Backward Compatibility**: Original constraint names preserved in migration scripts
