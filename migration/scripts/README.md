# Climasys Database Migration Scripts

This directory contains comprehensive scripts to migrate data from SQL Server schema `Climasys-00010` to PostgreSQL schema `climasys_dev`.

## 📋 Overview

The migration process consists of several steps:

1. **Schema Creation** - Create PostgreSQL tables and structure
2. **Reference Data** - Insert static reference data
3. **Data Migration** - Migrate data from SQL Server to PostgreSQL
4. **Verification** - Verify the migration was successful

## 📁 Files Description

| File | Description |
|------|-------------|
| `01_create_schema.sql` | Creates the PostgreSQL schema and all tables |
| `02_insert_reference_data.sql` | Inserts reference/master data (languages, roles, etc.) |
| `03_migrate_from_sqlserver.sql` | Migration functions and sample data |
| `04_sqlserver_extraction_queries.sql` | SQL queries to extract data from SQL Server |
| `05_postgresql_import_script.py` | Python script to import CSV data to PostgreSQL |
| `06_migration_runner.sh` | Automated migration runner script |
| `README.md` | This documentation file |

## 🚀 Quick Start

### Prerequisites

1. **PostgreSQL** installed and running
2. **Python 3.7+** with required packages
3. **Access to SQL Server** with the source data
4. **psql** command-line tool

### Installation

1. **Install Python dependencies:**
   ```bash
   pip install psycopg2-binary pandas python-dotenv
   ```

2. **Make the runner script executable:**
   ```bash
   chmod +x 06_migration_runner.sh
   ```

### Configuration

Set the following environment variables (or use defaults):

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=climasys_dev
export DB_USER=postgres
export DB_PASSWORD=your_password
export CSV_DIRECTORY=./csv_data
```

## 📖 Step-by-Step Migration

### Step 1: Extract Data from SQL Server

1. **Connect to your SQL Server instance**
2. **Run the extraction queries** from `04_sqlserver_extraction_queries.sql`
3. **Export results to CSV files** with the following names:
   - `doctor_master.csv`
   - `clinic_master.csv`
   - `user_master.csv`
   - `user_role.csv`
   - `patient_master.csv`
   - `patient_visits.csv`
   - `medicine_master.csv`
   - `system_params.csv`
   - `license_key.csv`
   - And others as listed in the extraction script

4. **Place all CSV files** in the `csv_data` directory

### Step 2: Run the Migration

#### Option A: Automated Migration (Recommended)

```bash
./06_migration_runner.sh
```

#### Option B: Manual Step-by-Step

1. **Create the schema:**
   ```bash
   psql -h localhost -U postgres -d climasys_dev -f 01_create_schema.sql
   ```

2. **Insert reference data:**
   ```bash
   psql -h localhost -U postgres -d climasys_dev -f 02_insert_reference_data.sql
   ```

3. **Run migration functions:**
   ```bash
   psql -h localhost -U postgres -d climasys_dev -f 03_migrate_from_sqlserver.sql
   ```

4. **Import CSV data:**
   ```bash
   python3 05_postgresql_import_script.py
   ```

### Step 3: Verify Migration

```bash
./06_migration_runner.sh --verify-only
```

## 🔧 Advanced Usage

### Migration Runner Options

```bash
# Skip specific steps
./06_migration_runner.sh --skip-schema
./06_migration_runner.sh --skip-reference
./06_migration_runner.sh --skip-migration
./06_migration_runner.sh --skip-csv

# Only verify existing data
./06_migration_runner.sh --verify-only

# Show help
./06_migration_runner.sh --help
```

### Custom Database Configuration

```bash
DB_HOST=your_host DB_PASSWORD=your_pass ./06_migration_runner.sh
```

### Python Script Configuration

Create a `.env` file in the scripts directory:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=climasys_dev
DB_USER=postgres
DB_PASSWORD=your_password
CSV_DIRECTORY=./csv_data
```

## 📊 Database Schema

### Core Tables

- **doctor_master** - Doctor information
- **clinic_master** - Clinic information
- **user_master** - System users
- **user_role** - User role assignments
- **patient_master** - Patient information
- **patient_visits** - Patient visit records

### Reference Tables

- **language_master** - Supported languages
- **role_master** - User roles
- **gender_master** - Gender options
- **blood_group_master** - Blood group types
- **shift_master** - Clinic shift schedules
- **medicine_master** - Medicine catalog

### Configuration Tables

- **system_params** - System configuration
- **license_key** - License information
- **model** - System models
- **model_config_params** - Model configuration

## 🔍 Troubleshooting

### Common Issues

1. **Connection Failed**
   - Check PostgreSQL is running
   - Verify connection parameters
   - Ensure user has proper permissions

2. **CSV Import Failed**
   - Check CSV file format
   - Verify column names match database schema
   - Check for data type mismatches

3. **Schema Creation Failed**
   - Ensure user has CREATE privileges
   - Check for existing conflicting objects
   - Verify PostgreSQL version compatibility

### Log Files

- Migration logs are saved to `migration_YYYYMMDD_HHMMSS.log`
- Check logs for detailed error information

### Data Validation

After migration, verify:

1. **Record counts** match between source and target
2. **Data integrity** is maintained
3. **Foreign key relationships** are correct
4. **Date/time formats** are properly converted

## 🔄 Rollback

If you need to rollback the migration:

```sql
-- Drop the schema (WARNING: This will delete all data)
DROP SCHEMA IF EXISTS climasys_dev CASCADE;

-- Recreate the schema
\i 01_create_schema.sql
\i 02_insert_reference_data.sql
```

## 📝 Notes

- **Data Types**: SQL Server data types are automatically converted to PostgreSQL equivalents
- **Boolean Values**: `1/0` and `true/false` are properly handled
- **Date/Time**: SQL Server datetime formats are converted to PostgreSQL timestamp/time
- **Case Sensitivity**: PostgreSQL is case-sensitive, so column names are preserved exactly
- **Primary Keys**: Auto-incrementing IDs are handled with PostgreSQL SERIAL type

## 🆘 Support

If you encounter issues:

1. Check the log files for detailed error messages
2. Verify your database configuration
3. Ensure all prerequisites are installed
4. Check the troubleshooting section above

## 📄 License

These migration scripts are part of the Climasys project and follow the same licensing terms.
