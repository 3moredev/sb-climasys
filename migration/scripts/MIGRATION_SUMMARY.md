# Climasys Database Migration - Complete Solution

## 🎯 Overview

This migration solution provides a complete framework to migrate data from SQL Server schema `Climasys-00010` to PostgreSQL schema `climasys_dev`. The solution includes automated scripts, manual processes, and comprehensive documentation.

## 📁 Migration Scripts Created

### Core Migration Scripts

| File | Purpose | Platform |
|------|---------|----------|
| `01_create_schema.sql` | Creates PostgreSQL schema and tables | All |
| `02_insert_reference_data.sql` | Inserts reference/master data | All |
| `03_migrate_from_sqlserver.sql` | Migration functions and sample data | All |
| `04_sqlserver_extraction_queries.sql` | SQL queries to extract from SQL Server | All |
| `05_postgresql_import_script.py` | Python script for CSV import | All |

### Platform-Specific Runners

| File | Purpose | Platform |
|------|---------|----------|
| `06_migration_runner.sh` | Automated migration runner | Linux/macOS |
| `06_migration_runner.bat` | Automated migration runner | Windows CMD |
| `06_migration_runner.ps1` | Automated migration runner | Windows PowerShell |

### Supporting Files

| File | Purpose |
|------|---------|
| `README.md` | Comprehensive documentation |
| `requirements.txt` | Python dependencies |
| `test_connection.py` | Environment testing script |
| `env.example` | Environment configuration template |
| `MIGRATION_SUMMARY.md` | This summary document |

## 🚀 Quick Start Guide

### For Windows Users (PowerShell)

1. **Install Python dependencies:**
   ```powershell
   pip install -r requirements.txt
   ```

2. **Test your environment:**
   ```powershell
   python test_connection.py
   ```

3. **Run the migration:**
   ```powershell
   .\06_migration_runner.ps1
   ```

### For Windows Users (Command Prompt)

1. **Install Python dependencies:**
   ```cmd
   pip install -r requirements.txt
   ```

2. **Test your environment:**
   ```cmd
   python test_connection.py
   ```

3. **Run the migration:**
   ```cmd
   06_migration_runner.bat
   ```

### For Linux/macOS Users

1. **Install Python dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

2. **Make scripts executable:**
   ```bash
   chmod +x 06_migration_runner.sh
   ```

3. **Test your environment:**
   ```bash
   python3 test_connection.py
   ```

4. **Run the migration:**
   ```bash
   ./06_migration_runner.sh
   ```

## 📋 Migration Process

### Step 1: Extract Data from SQL Server

1. **Connect to your SQL Server instance**
2. **Run queries from `04_sqlserver_extraction_queries.sql`**
3. **Export results to CSV files** with these exact names:
   - `doctor_master.csv`
   - `clinic_master.csv`
   - `user_master.csv`
   - `user_role.csv`
   - `patient_master.csv`
   - `patient_visits.csv`
   - `medicine_master.csv`
   - `system_params.csv`
   - `license_key.csv`
   - `shift_master.csv`
   - `doctor_clinic_shift.csv`
   - `visit_prescription_overwrite.csv`
   - `model_master.csv`
   - `doctor_model.csv`
   - `model_config_params.csv`
   - `role_master.csv`
   - `language_master.csv`
   - `gender_master.csv`
   - `blood_group_master.csv`

4. **Place all CSV files in the `csv_data` directory**

### Step 2: Configure Environment

Create a `.env` file (copy from `env.example`):

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=climasys_dev
DB_USER=postgres
DB_PASSWORD=your_password
CSV_DIRECTORY=./csv_data
```

### Step 3: Run Migration

Choose your platform and run the appropriate script:

- **Windows PowerShell:** `.\06_migration_runner.ps1`
- **Windows CMD:** `06_migration_runner.bat`
- **Linux/macOS:** `./06_migration_runner.sh`

## 🔧 Advanced Options

### Migration Runner Options

All runners support these options:

- `--skip-schema` - Skip schema creation
- `--skip-reference` - Skip reference data insertion
- `--skip-migration` - Skip data migration functions
- `--skip-csv` - Skip CSV import
- `--verify-only` - Only run verification
- `--help` - Show help

### Examples

```bash
# Skip CSV import (if you don't have CSV files yet)
.\06_migration_runner.ps1 -SkipCsv

# Only verify existing data
.\06_migration_runner.ps1 -VerifyOnly

# Use custom database configuration
$env:DB_PASSWORD="mypassword"; .\06_migration_runner.ps1
```

## 📊 Database Schema

### Core Tables Created

- **doctor_master** - Doctor information
- **clinic_master** - Clinic information  
- **user_master** - System users
- **user_role** - User role assignments
- **patient_master** - Patient information
- **patient_visits** - Patient visit records
- **medicine_master** - Medicine catalog
- **system_params** - System configuration
- **license_key** - License information

### Reference Tables

- **language_master** - Supported languages
- **role_master** - User roles
- **gender_master** - Gender options
- **blood_group_master** - Blood group types
- **shift_master** - Clinic shift schedules
- **model** - System models
- **model_config_params** - Model configuration

## 🔍 Verification

After migration, verify the data:

1. **Check record counts:**
   ```sql
   SELECT 
       'Doctor Master' as table_name, COUNT(*) as record_count 
   FROM climasys_dev.doctor_master
   UNION ALL
   SELECT 'Clinic Master', COUNT(*) FROM climasys_dev.clinic_master
   UNION ALL
   SELECT 'User Master', COUNT(*) FROM climasys_dev.user_master
   UNION ALL
   SELECT 'Patient Master', COUNT(*) FROM climasys_dev.patient_master
   UNION ALL
   SELECT 'Patient Visits', COUNT(*) FROM climasys_dev.patient_visits
   ORDER BY table_name;
   ```

2. **Test login functionality:**
   ```sql
   SELECT usp_get_logindetails_json('test_user', 'test_password', 'Monday', 1);
   ```

## 🛠️ Troubleshooting

### Common Issues

1. **Connection Failed**
   - Check PostgreSQL is running
   - Verify connection parameters
   - Ensure user has proper permissions

2. **CSV Import Failed**
   - Check CSV file format and column names
   - Verify data types match database schema
   - Check for special characters in data

3. **Schema Creation Failed**
   - Ensure user has CREATE privileges
   - Check for existing conflicting objects
   - Verify PostgreSQL version compatibility

### Log Files

- Migration logs are saved with timestamp: `migration_YYYYMMDD_HHMMSS.log`
- Check logs for detailed error information

## 🔄 Rollback

If you need to rollback:

```sql
-- Drop the schema (WARNING: This will delete all data)
DROP SCHEMA IF EXISTS climasys_dev CASCADE;

-- Recreate the schema
\i 01_create_schema.sql
\i 02_insert_reference_data.sql
```

## 📝 Data Type Mappings

| SQL Server | PostgreSQL | Notes |
|------------|------------|-------|
| NVARCHAR | VARCHAR | Unicode support |
| INT | INTEGER | Same |
| DATETIME | TIMESTAMP | Date/time handling |
| BIT | BOOLEAN | True/false values |
| UNIQUEIDENTIFIER | UUID | GUID support |

## 🎯 Next Steps

After successful migration:

1. **Update application configuration** to use PostgreSQL
2. **Test all application functionality**
3. **Update stored procedures** to PostgreSQL functions
4. **Set up regular backups**
5. **Monitor performance** and optimize as needed

## 📞 Support

If you encounter issues:

1. Check the log files for detailed error messages
2. Run `test_connection.py` to verify environment setup
3. Verify your database configuration
4. Ensure all prerequisites are installed
5. Check the troubleshooting section in README.md

## 🏆 Success Criteria

Migration is successful when:

- ✅ All tables are created in `climasys_dev` schema
- ✅ Reference data is populated
- ✅ Core data is migrated from CSV files
- ✅ Record counts match between source and target
- ✅ Application can connect and function properly
- ✅ Login functionality works
- ✅ Dashboard data loads correctly

---

**Note:** This migration solution is designed to be comprehensive and handles the most common scenarios. For specific edge cases or custom requirements, you may need to modify the scripts accordingly.
