@echo off
setlocal enabledelayedexpansion

echo ==========================================
echo Climasys Complete Migration Script
echo SQL Server to PostgreSQL Migration
echo ==========================================
echo.

REM Check if PostgreSQL client is available
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] PostgreSQL client (psql) is not installed or not in PATH.
    echo [ERROR] Please install PostgreSQL client tools.
    pause
    exit /b 1
)

REM Check if Python is available
where python >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Python is not installed or not in PATH.
    echo [ERROR] Please install Python.
    pause
    exit /b 1
)

echo [SUCCESS] Prerequisites check passed!
echo.

REM Get database configuration
echo [INFO] Please provide PostgreSQL configuration:
echo.

set /p DB_HOST="PostgreSQL host (default: localhost): "
if "%DB_HOST%"=="" set DB_HOST=localhost

set /p DB_PORT="PostgreSQL port (default: 5432): "
if "%DB_PORT%"=="" set DB_PORT=5432

set /p DB_USERNAME="PostgreSQL username (default: postgres): "
if "%DB_USERNAME%"=="" set DB_USERNAME=postgres

set /p DB_PASSWORD="PostgreSQL password: "

set /p DB_NAME="Database name (default: climasys_dev): "
if "%DB_NAME%"=="" set DB_NAME=climasys_dev

echo.

REM Create database
echo [INFO] Creating database '%DB_NAME%' if it doesn't exist...
set PGPASSWORD=%DB_PASSWORD%
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USERNAME% -d postgres -c "CREATE DATABASE %DB_NAME%;" >nul 2>nul
if %errorlevel% equ 0 (
    echo [SUCCESS] Database '%DB_NAME%' created successfully!
) else (
    echo [WARNING] Database '%DB_NAME%' might already exist or there was an error.
)

REM Test connection
echo [INFO] Testing PostgreSQL connection...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USERNAME% -d %DB_NAME% -c "SELECT 1;" >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] PostgreSQL connection failed!
    pause
    exit /b 1
)
echo [SUCCESS] PostgreSQL connection successful!
echo.

REM Set migration directory
set MIGRATION_DIR=..\..\migration\scripts

REM Step 1: Create schema
echo [INFO] Step 1: Creating database schema...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USERNAME% -d %DB_NAME% -f "%MIGRATION_DIR%\01_create_schema.sql" >nul 2>nul
if %errorlevel% equ 0 (
    echo [SUCCESS] Schema creation completed!
) else (
    echo [ERROR] Schema creation failed!
    pause
    exit /b 1
)
echo.

REM Step 2: Insert reference data
echo [INFO] Step 2: Inserting reference data...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USERNAME% -d %DB_NAME% -f "%MIGRATION_DIR%\02_insert_reference_data.sql" >nul 2>nul
if %errorlevel% equ 0 (
    echo [SUCCESS] Reference data insertion completed!
) else (
    echo [ERROR] Reference data insertion failed!
    pause
    exit /b 1
)
echo.

REM Step 3: Create stored procedures/functions
echo [INFO] Step 3: Creating stored procedures and functions...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USERNAME% -d %DB_NAME% -f "%MIGRATION_DIR%\09_migrate_stored_procedures.sql" >nul 2>nul
if %errorlevel% equ 0 (
    echo [SUCCESS] Stored procedures creation completed!
) else (
    echo [WARNING] Stored procedures creation had issues (this might be expected if they already exist).
)
echo.

REM Step 4: Check for CSV data migration
set CSV_DIR=%MIGRATION_DIR%\csv_data
if exist "%CSV_DIR%" (
    echo [INFO] Step 4: Found CSV data directory. Running data migration...
    
    REM Create .env file for Python scripts
    echo DB_HOST=%DB_HOST% > "%MIGRATION_DIR%\.env"
    echo DB_PORT=%DB_PORT% >> "%MIGRATION_DIR%\.env"
    echo DB_NAME=%DB_NAME% >> "%MIGRATION_DIR%\.env"
    echo DB_USER=%DB_USERNAME% >> "%MIGRATION_DIR%\.env"
    echo DB_PASSWORD=%DB_PASSWORD% >> "%MIGRATION_DIR%\.env"
    echo CSV_DIRECTORY=%CSV_DIR% >> "%MIGRATION_DIR%\.env"
    
    REM Run Python import script
    cd /d "%MIGRATION_DIR%"
    python "%MIGRATION_DIR%\enhanced_postgresql_import.py"
    if %errorlevel% equ 0 (
        echo [SUCCESS] CSV data migration completed!
    ) else (
        echo [WARNING] CSV data migration had issues. Check the logs for details.
    )
    cd /d "%~dp0"
) else (
    echo [WARNING] Step 4: No CSV data directory found. Skipping data migration.
    echo [WARNING] To migrate data from SQL Server:
    echo [WARNING] 1. Run the queries from enhanced_sqlserver_extraction.sql on your SQL Server
    echo [WARNING] 2. Export results to CSV files in the csv_data directory
    echo [WARNING] 3. Re-run this script
)
echo.

REM Step 5: Create application configuration
echo [INFO] Step 5: Creating application configuration...

REM Create .env file for the application
echo # PostgreSQL Configuration > .env
echo DB_PASSWORD=%DB_PASSWORD% >> .env
echo DB_HOST=%DB_HOST% >> .env
echo DB_PORT=%DB_PORT% >> .env
echo DB_NAME=%DB_NAME% >> .env
echo DB_USERNAME=%DB_USERNAME% >> .env
echo. >> .env
echo # JWT Configuration >> .env
echo JWT_SECRET=your-super-secret-jwt-key-change-in-production-%RANDOM% >> .env
echo. >> .env
echo # Climasys Configuration >> .env
echo CLIMASYS_ENCRYPTION_KEY=PA1ANDE61INI6 >> .env
echo. >> .env
echo # Spring Profile >> .env
echo SPRING_PROFILES_ACTIVE=dev >> .env
echo. >> .env
echo # API Configuration >> .env
echo APP_API_BASE_URL=http://localhost:8080/api >> .env

echo [SUCCESS] Application configuration created!
echo.

REM Step 6: Verification
echo [INFO] Step 6: Verifying migration...

REM Check if tables exist
for /f %%i in ('psql -h %DB_HOST% -p %DB_PORT% -U %DB_USERNAME% -d %DB_NAME% -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'climasys_dev';" 2^>nul') do set TABLE_COUNT=%%i
set TABLE_COUNT=%TABLE_COUNT: =%

if %TABLE_COUNT% gtr 0 (
    echo [SUCCESS] Migration verification passed! Found %TABLE_COUNT% tables in climasys_dev schema.
) else (
    echo [ERROR] Migration verification failed! No tables found in climasys_dev schema.
    pause
    exit /b 1
)

echo.
echo [SUCCESS] ==========================================
echo [SUCCESS] Migration completed successfully!
echo [SUCCESS] ==========================================
echo.

echo [INFO] Next steps:
echo 1. Update your application.yml with the database configuration
echo 2. Run: mvn clean install
echo 3. Run: mvn spring-boot:run
echo 4. Test the application endpoints
echo.

echo [INFO] Database configuration:
echo - Host: %DB_HOST%
echo - Port: %DB_PORT%
echo - Database: %DB_NAME%
echo - Username: %DB_USERNAME%
echo - Schema: climasys_dev
echo.

echo [INFO] Configuration files created:
echo - .env (application environment variables)
echo - %MIGRATION_DIR%\.env (migration environment variables)
echo.

pause

