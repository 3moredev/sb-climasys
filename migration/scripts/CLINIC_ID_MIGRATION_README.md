# Clinic ID Migration Documentation

## Overview

This document describes the migration process for adding `clinic_id` field to specified tables in the Climasys database. The migration is designed to support multi-clinic functionality by adding clinic-specific constraints to master tables.

## Tables Modified

The following tables have been modified to include `clinic_id` field:

1. **status_order** - Status ordering for roles
2. **role_master** - Master table for user roles  
3. **lab_test_master** - Master table for lab tests
4. **lab_test_parameter** - Parameters for lab tests
5. **complaint_master** - Master table for complaints
6. **medicine_master** - Master table for medicines
7. **referal_doctors_list** - List of referral doctors (if exists)

## Migration Scripts

### 1. Complete Schema Migration Script
**File:** `add_clinic_id_complete_migration.sql`

**Note:** This script handles dependency management by:
- Dropping dependent foreign key constraints first
- Dropping primary key constraints
- Adding clinic_id columns
- Creating new primary key constraints
- Creating performance indexes

### 2. Original Schema Alteration Script (Deprecated)
**File:** `add_clinic_id_alter_scripts.sql`

This script performs the following operations:
- Adds `clinic_id VARCHAR(30)` column to each table
- Adds foreign key constraints referencing `clinic_master(clinic_id)`
- Updates primary key constraints to include `clinic_id`
- Creates performance indexes
- Adds table and column comments

### 2. Data Migration Script  
**File:** `migrate_existing_data_clinic_id.sql`

This script provides multiple options for migrating existing data:
- **Option 1:** Set default clinic_id for all records
- **Option 2:** Set clinic_id based on doctor_id
- **Option 3:** Set clinic_id based on user_role table
- **Option 4:** Manual clinic_id assignment

## Primary Key Changes

The primary key constraints have been updated to include `clinic_id`:

| Table | Original Primary Key | New Primary Key |
|-------|---------------------|-----------------|
| status_order | (doctor_id, role_id, status_id) | (doctor_id, role_id, status_id, clinic_id) |
| role_master | (role_id) | (role_id, clinic_id) |
| lab_test_master | (doctor_id, id) | (doctor_id, id, clinic_id) |
| lab_test_parameter | (doctor_id, id, lab_test_id) | (doctor_id, id, lab_test_id, clinic_id) |
| complaint_master | (short_description, doctor_id) | (short_description, doctor_id, clinic_id) |
| medicine_master | (short_description) | (short_description, clinic_id) |
| referal_doctors_list | (id) | (id, clinic_id) |

## Foreign Key Constraints

**Important Note:** The `clinic_master` table has a composite primary key `(clinic_id, doctor_id)`, not just `clinic_id`. This means we cannot directly create foreign key constraints referencing only `clinic_id`.

### Solution:
1. **First:** Add a unique constraint on `clinic_id` in `clinic_master`
2. **Then:** Add foreign key constraints referencing the unique `clinic_id`

```sql
-- Step 1: Create unique constraint on clinic_id
ALTER TABLE clinic_master 
ADD CONSTRAINT uk_clinic_master_clinic_id UNIQUE (clinic_id);

-- Step 2: Add foreign key constraints
ALTER TABLE table_name 
ADD CONSTRAINT fk_table_name_clinic_id 
FOREIGN KEY (clinic_id) REFERENCES clinic_master(clinic_id);
```

## Performance Indexes

The following indexes have been created for optimal performance:

### Single Column Indexes
- `idx_table_name_clinic_id` - For clinic_id lookups
- `idx_table_name_doctor_clinic` - For doctor-clinic combinations

### Composite Indexes  
- `idx_status_order_doctor_role_clinic` - For status order queries
- `idx_lab_test_master_doctor_id_clinic` - For lab test queries
- `idx_lab_test_parameter_doctor_lab_clinic` - For parameter queries
- `idx_complaint_master_doctor_clinic` - For complaint queries
- `idx_medicine_master_medicine_clinic` - For medicine queries (using short_description)

## Migration Steps

### Step 1: Backup Database
```bash
pg_dump -h localhost -U postgres -d climasys_dev > backup_before_clinic_id_migration.sql
```

### Step 2: Run Complete Schema Migration
```bash
psql -h localhost -U postgres -d climasys_dev -f add_clinic_id_complete_migration.sql
```

### Step 3: Migrate Existing Data
```bash
psql -h localhost -U postgres -d climasys_dev -f migrate_existing_data_clinic_id.sql
```

### Step 4: Add Foreign Key Constraints
```bash
psql -h localhost -U postgres -d climasys_dev -f add_foreign_key_constraints_clinic_id.sql
```

### Step 5: Verify Migration
```sql
-- Check for NULL clinic_id values
SELECT 'status_order' as table_name, COUNT(*) as null_count 
FROM status_order WHERE clinic_id IS NULL
UNION ALL
SELECT 'role_master' as table_name, COUNT(*) as null_count 
FROM role_master WHERE clinic_id IS NULL;
```

## Data Migration Options

### Option 1: Default Clinic ID
Set a default clinic_id for all existing records:
```sql
UPDATE status_order SET clinic_id = 'DEFAULT_CLINIC' WHERE clinic_id IS NULL;
```

### Option 2: Doctor-Based Assignment
Assign clinic_id based on doctor's primary clinic:
```sql
UPDATE status_order 
SET clinic_id = (
    SELECT c.clinic_id 
    FROM clinic_master c 
    WHERE c.doctor_id = status_order.doctor_id 
    LIMIT 1
)
WHERE clinic_id IS NULL;
```

### Option 3: Role-Based Assignment
Assign clinic_id based on user roles:
```sql
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
```

## Verification Queries

### Check Schema Changes
```sql
-- Verify clinic_id columns exist
SELECT table_name, column_name, data_type 
FROM information_schema.columns 
WHERE table_schema = 'climasys_dev' 
AND column_name = 'clinic_id';

-- Verify primary key constraints
SELECT tc.table_name, tc.constraint_name, kcu.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'climasys_dev' 
AND tc.constraint_type = 'PRIMARY KEY'
AND tc.table_name IN ('status_order', 'role_master', 'lab_test_master');
```

### Check Data Integrity
```sql
-- Check for orphaned clinic_id values
SELECT DISTINCT s.clinic_id
FROM status_order s
LEFT JOIN clinic_master c ON s.clinic_id = c.clinic_id
WHERE c.clinic_id IS NULL AND s.clinic_id IS NOT NULL;

-- Check clinic_id distribution
SELECT clinic_id, COUNT(*) as record_count
FROM status_order 
GROUP BY clinic_id;
```

## Rollback Procedure

If rollback is needed:

### Step 1: Drop Foreign Key Constraints
```sql
ALTER TABLE status_order DROP CONSTRAINT IF EXISTS fk_status_order_clinic_id;
ALTER TABLE role_master DROP CONSTRAINT IF EXISTS fk_role_master_clinic_id;
-- Repeat for all tables
```

### Step 2: Drop Primary Key Constraints
```sql
ALTER TABLE status_order DROP CONSTRAINT IF EXISTS status_order_pkey;
ALTER TABLE role_master DROP CONSTRAINT IF EXISTS role_master_pkey;
-- Repeat for all tables
```

### Step 3: Drop Indexes
```sql
DROP INDEX IF EXISTS idx_status_order_clinic_id;
DROP INDEX IF EXISTS idx_role_master_clinic_id;
-- Repeat for all indexes
```

### Step 4: Drop Columns
```sql
ALTER TABLE status_order DROP COLUMN IF EXISTS clinic_id;
ALTER TABLE role_master DROP COLUMN IF EXISTS clinic_id;
-- Repeat for all tables
```

### Step 5: Restore Original Primary Keys
```sql
ALTER TABLE status_order ADD CONSTRAINT status_order_pkey PRIMARY KEY (doctor_id, role_id, status_id);
ALTER TABLE role_master ADD CONSTRAINT role_master_pkey PRIMARY KEY (role_id);
-- Repeat for all tables
```

## Application Impact

### Code Changes Required
1. **API Endpoints:** Update to include clinic_id in requests/responses
2. **Database Queries:** Modify queries to include clinic_id in WHERE clauses
3. **Primary Key Handling:** Update application logic to handle composite primary keys
4. **Data Validation:** Add clinic_id validation in application layer

### Example Query Updates
```sql
-- Before
SELECT * FROM status_order WHERE doctor_id = 'DR-001';

-- After  
SELECT * FROM status_order WHERE doctor_id = 'DR-001' AND clinic_id = 'CLINIC-001';
```

## Best Practices

1. **Always backup** before running migration scripts
2. **Test migration** on a copy of production data first
3. **Verify data integrity** after migration
4. **Update application code** to handle new schema
5. **Monitor performance** after migration
6. **Document any custom clinic_id assignment logic**

## Troubleshooting

### Common Issues

1. **Foreign Key Violations:** Ensure all clinic_id values exist in clinic_master
2. **Primary Key Conflicts:** Check for duplicate combinations after adding clinic_id
3. **Performance Issues:** Monitor query performance with new indexes
4. **Data Migration Failures:** Review and adjust migration options

### Support Queries

```sql
-- Find records with invalid clinic_id
SELECT * FROM status_order 
WHERE clinic_id NOT IN (SELECT clinic_id FROM clinic_master);

-- Check for duplicate primary keys
SELECT doctor_id, role_id, status_id, clinic_id, COUNT(*)
FROM status_order 
GROUP BY doctor_id, role_id, status_id, clinic_id
HAVING COUNT(*) > 1;
```

## Conclusion

This migration adds multi-clinic support to the Climasys database by introducing clinic_id constraints to master tables. The changes ensure data isolation between clinics while maintaining referential integrity and optimal performance.
