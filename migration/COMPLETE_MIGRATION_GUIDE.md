# 🚀 Complete Climasys Migration Guide
## SQL Server to PostgreSQL Migration

This guide provides a comprehensive step-by-step process to migrate your Climasys application from SQL Server to PostgreSQL.

## 📋 Prerequisites

### Required Software
- **PostgreSQL 12+** - Database server
- **Python 3.8+** - For migration scripts
- **Java 17+** - For Spring Boot application
- **Maven 3.6+** - For building the application

### Required Python Packages
```bash
pip install psycopg2-binary pandas python-dotenv
```

### Required Tools
- **psql** - PostgreSQL command-line client
- **SQL Server Management Studio** - For data extraction

## 🎯 Migration Overview

The migration process consists of these main steps:

1. **Setup PostgreSQL Environment**
2. **Extract Data from SQL Server**
3. **Create PostgreSQL Schema**
4. **Import Data to PostgreSQL**
5. **Migrate Stored Procedures**
6. **Update Application Configuration**
7. **Test and Verify**

## 📁 Migration Files Structure

```
migration/
├── scripts/
│   ├── 01_create_schema.sql              # PostgreSQL schema creation
│   ├── 02_insert_reference_data.sql      # Reference data insertion
│   ├── 04_sqlserver_extraction_queries.sql # SQL Server data extraction
│   ├── enhanced_sqlserver_extraction.sql # Enhanced extraction queries
│   ├── 05_postgresql_import_script.py    # Basic import script
│   ├── enhanced_postgresql_import.py     # Enhanced import script
│   ├── 09_migrate_stored_procedures.sql  # Stored procedures migration
│   └── csv_data/                         # Directory for CSV files
└── COMPLETE_MIGRATION_GUIDE.md           # This guide

backend-spring/
├── scripts/
│   ├── complete-migration.sh             # Linux/macOS migration script
│   ├── complete-migration.bat            # Windows migration script
│   ├── setup-postgres.sh                 # PostgreSQL setup script
│   └── test-postgres-connection.sh       # Connection test script
└── src/main/resources/
    └── application.yml                   # Application configuration
```

## 🚀 Quick Start (Automated Migration)

### For Linux/macOS Users

1. **Make scripts executable:**
   ```bash
   chmod +x backend-spring/scripts/complete-migration.sh
   ```

2. **Run the migration:**
   ```bash
   cd backend-spring/scripts
   ./complete-migration.sh
   ```

### For Windows Users

1. **Run the migration:**
   ```cmd
   cd backend-spring\scripts
   complete-migration.bat
   ```

The automated script will:
- Check prerequisites
- Create PostgreSQL database
- Create schema and tables
- Insert reference data
- Create stored procedures
- Import CSV data (if available)
- Create application configuration
- Verify the migration

## 📖 Manual Migration Steps

### Step 1: Setup PostgreSQL Environment

1. **Install PostgreSQL:**
   - Download from: https://www.postgresql.org/download/
   - Install with default settings
   - Remember the postgres user password

2. **Create Database:**
   ```sql
   CREATE DATABASE climasys_dev;
   ```

3. **Test Connection:**
   ```bash
   psql -h localhost -U postgres -d climasys_dev
   ```

### Step 2: Extract Data from SQL Server

1. **Connect to your SQL Server instance**
2. **Run extraction queries:**
   - Use `migration/scripts/enhanced_sqlserver_extraction.sql`
   - Run each query separately
   - Export results to CSV files

3. **Required CSV files:**
   ```
   language_master.csv
   role_master.csv
   gender_master.csv
   blood_group_master.csv
   doctor_master.csv
   clinic_master.csv
   user_master.csv
   user_role.csv
   patient_master.csv
   patient_visits.csv
   medicine_master.csv
   prescription_master.csv
   visit_prescription_overwrite.csv
   system_params.csv
   license_key.csv
   shift_master.csv
   doctor_clinic_shift.csv
   model.csv
   doctor_model.csv
   model_config_params.csv
   ```

4. **Place CSV files in:**
   ```
   migration/scripts/csv_data/
   ```

### Step 3: Create PostgreSQL Schema

1. **Run schema creation script:**
   ```bash
   psql -h localhost -U postgres -d climasys_dev -f migration/scripts/01_create_schema.sql
   ```

2. **Insert reference data:**
   ```bash
   psql -h localhost -U postgres -d climasys_dev -f migration/scripts/02_insert_reference_data.sql
   ```

### Step 4: Import Data to PostgreSQL

1. **Create environment file:**
   ```bash
   cd migration/scripts
   cat > .env << EOF
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=climasys_dev
   DB_USER=postgres
   DB_PASSWORD=your_password
   CSV_DIRECTORY=./csv_data
   EOF
   ```

2. **Run enhanced import script:**
   ```bash
   python3 enhanced_postgresql_import.py
   ```

### Step 5: Migrate Stored Procedures

1. **Create stored procedures:**
   ```bash
   psql -h localhost -U postgres -d climasys_dev -f migration/scripts/09_migrate_stored_procedures.sql
   ```

### Step 6: Update Application Configuration

1. **Update application.yml:**
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/climasys_dev
       username: postgres
       password: ${DB_PASSWORD:your_password}
       driver-class-name: org.postgresql.Driver
     jpa:
       hibernate:
         dialect: org.hibernate.dialect.PostgreSQLDialect
   ```

2. **Create environment file:**
   ```bash
   cd backend-spring
   cat > .env << EOF
   DB_PASSWORD=your_password
   JWT_SECRET=your-super-secret-jwt-key
   CLIMASYS_ENCRYPTION_KEY=PA1ANDE61INI6
   SPRING_PROFILES_ACTIVE=dev
   APP_API_BASE_URL=http://localhost:8080/api
   EOF
   ```

### Step 7: Test and Verify

1. **Build the application:**
   ```bash
   cd backend-spring
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Test endpoints:**
   ```bash
   # Health check
   curl http://localhost:8080/actuator/health
   
   # Dashboard data
   curl http://localhost:8080/api/dashboard/data
   ```

## 🔍 Verification Steps

### Database Verification

1. **Check table counts:**
   ```sql
   SELECT 
       schemaname,
       tablename,
       n_tup_ins as inserted_rows
   FROM pg_stat_user_tables 
   WHERE schemaname = 'climasys_dev'
   ORDER BY tablename;
   ```

2. **Test stored procedures:**
   ```sql
   SELECT usp_get_dashboard_data_json();
   ```

3. **Verify data integrity:**
   ```sql
   SELECT COUNT(*) FROM climasys_dev.doctor_master;
   SELECT COUNT(*) FROM climasys_dev.patient_master;
   SELECT COUNT(*) FROM climasys_dev.user_master;
   ```

### Application Verification

1. **Check application logs** for any errors
2. **Test login functionality**
3. **Verify dashboard loads** with real data
4. **Test all API endpoints**

## 🛠️ Troubleshooting

### Common Issues

#### 1. Connection Issues
```
Error: connection to server at "localhost" (127.0.0.1), port 5432 failed
```
**Solution:**
- Ensure PostgreSQL is running
- Check firewall settings
- Verify connection parameters

#### 2. Permission Issues
```
Error: permission denied for schema climasys_dev
```
**Solution:**
- Grant necessary permissions to user
- Run as postgres superuser

#### 3. Data Import Issues
```
Error: invalid input syntax for type integer
```
**Solution:**
- Check CSV data format
- Verify data types match schema
- Clean data before import

#### 4. Application Issues
```
Error: relation "climasys_dev.doctor_master" does not exist
```
**Solution:**
- Verify schema creation
- Check search_path setting
- Ensure tables exist

### Debug Commands

1. **Check PostgreSQL status:**
   ```bash
   sudo systemctl status postgresql
   ```

2. **Check database connections:**
   ```sql
   SELECT * FROM pg_stat_activity;
   ```

3. **Check schema:**
   ```sql
   \dn
   \dt climasys_dev.*
   ```

4. **Check logs:**
   ```bash
   tail -f /var/log/postgresql/postgresql-*.log
   ```

## 📊 Migration Summary

After successful migration, you should have:

- ✅ **PostgreSQL database** with `climasys_dev` schema
- ✅ **All tables** created and populated
- ✅ **Stored procedures** migrated to PostgreSQL functions
- ✅ **Application** running on PostgreSQL
- ✅ **All functionality** working as expected

## 🔄 Rollback Plan

If you need to rollback to SQL Server:

1. **Stop the application**
2. **Update application.yml** to use SQL Server
3. **Restart the application**
4. **Verify functionality**

## 📞 Support

If you encounter issues:

1. **Check the logs** in the migration directory
2. **Review error messages** carefully
3. **Verify prerequisites** are installed
4. **Test database connectivity** manually
5. **Check file permissions** and paths

## 🎉 Success Criteria

Migration is successful when:

- ✅ All tables are created in `climasys_dev` schema
- ✅ Data is imported successfully
- ✅ Stored procedures are working
- ✅ Application starts without errors
- ✅ Dashboard loads with real data
- ✅ All API endpoints respond correctly
- ✅ Login functionality works
- ✅ No data loss or corruption

---

**Congratulations!** 🎉 Your Climasys application has been successfully migrated to PostgreSQL!

