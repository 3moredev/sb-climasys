@echo off
REM =====================================================
REM Climasys Migration Runner Script (Windows)
REM =====================================================
REM 
REM This script automates the complete migration process
REM from SQL Server to PostgreSQL on Windows systems
REM
REM Usage: 06_migration_runner.bat
REM
REM =====================================================

setlocal enabledelayedexpansion

REM Configuration
if "%DB_HOST%"=="" set DB_HOST=localhost
if "%DB_PORT%"=="" set DB_PORT=5432
if "%DB_NAME%"=="" set DB_NAME=climasys_dev
if "%DB_USER%"=="" set DB_USER=postgres
if "%DB_PASSWORD%"=="" set DB_PASSWORD=root
if "%CSV_DIRECTORY%"=="" set CSV_DIRECTORY=.\csv_data

set LOG_FILE=migration_%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%.log
set LOG_FILE=%LOG_FILE: =0%

REM Parse command line arguments
set SKIP_SCHEMA=false
set SKIP_REFERENCE=false
set SKIP_MIGRATION=false
set SKIP_CSV=false
set VERIFY_ONLY=false

:parse_args
if "%~1"=="" goto :main
if "%~1"=="--help" goto :show_help
if "%~1"=="-h" goto :show_help
if "%~1"=="--skip-schema" set SKIP_SCHEMA=true
if "%~1"=="--skip-reference" set SKIP_REFERENCE=true
if "%~1"=="--skip-migration" set SKIP_MIGRATION=true
if "%~1"=="--skip-csv" set SKIP_CSV=true
if "%~1"=="--verify-only" set VERIFY_ONLY=true
shift
goto :parse_args

:show_help
echo Climasys Migration Runner (Windows)
echo.
echo Usage: %0 [OPTIONS]
echo.
echo Options:
echo   -h, --help              Show this help message
echo   --skip-schema           Skip schema creation
echo   --skip-reference        Skip reference data insertion
echo   --skip-migration        Skip data migration
echo   --skip-csv              Skip CSV import
echo   --verify-only           Only run verification
echo.
echo Environment Variables:
echo   DB_HOST                 PostgreSQL host (default: localhost)
echo   DB_PORT                 PostgreSQL port (default: 5432)
echo   DB_NAME                 Database name (default: climasys_dev)
echo   DB_USER                 Database user (default: postgres)
echo   DB_PASSWORD             Database password (default: root)
echo   CSV_DIRECTORY           CSV files directory (default: .\csv_data)
echo.
echo Examples:
echo   %0                                    # Run complete migration
echo   %0 --skip-csv                        # Skip CSV import
echo   %0 --verify-only                     # Only verify existing data
echo   set DB_PASSWORD=mypass ^& %0         # Use custom password
goto :eof

:main
echo [INFO] Starting Climasys Migration Process
echo [INFO] Log file: %LOG_FILE%

REM Check PostgreSQL connection
call :check_postgres_connection
if errorlevel 1 goto :error_exit

if "%VERIFY_ONLY%"=="true" (
    call :verify_migration
    goto :end
)

REM Create CSV directory
call :create_csv_directory

REM Run migration steps
if "%SKIP_SCHEMA%"=="false" (
    call :run_sql_script "01_create_schema.sql" "Schema Creation"
    if errorlevel 1 goto :error_exit
)

if "%SKIP_REFERENCE%"=="false" (
    call :run_sql_script "02_insert_reference_data.sql" "Reference Data Insertion"
    if errorlevel 1 goto :error_exit
)

if "%SKIP_MIGRATION%"=="false" (
    call :run_sql_script "03_migrate_from_sqlserver.sql" "Data Migration Functions"
    if errorlevel 1 goto :error_exit
)

if "%SKIP_CSV%"=="false" (
    call :run_python_migration
)

REM Verify migration
call :verify_migration

echo [SUCCESS] Migration process completed!
echo [INFO] Check the log file for detailed information: %LOG_FILE%
goto :end

:check_postgres_connection
echo [INFO] Checking PostgreSQL connection...

REM Check if psql is available
psql --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] psql command not found. Please install PostgreSQL client tools.
    exit /b 1
)

REM Test connection
set PGPASSWORD=%DB_PASSWORD%
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -c "SELECT 1;" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Cannot connect to PostgreSQL database.
    echo [ERROR] Please check your database configuration:
    echo [ERROR]   Host: %DB_HOST%
    echo [ERROR]   Port: %DB_PORT%
    echo [ERROR]   Database: %DB_NAME%
    echo [ERROR]   User: %DB_USER%
    exit /b 1
)

echo [SUCCESS] PostgreSQL connection successful
exit /b 0

:create_csv_directory
echo [INFO] Creating CSV directory...

if not exist "%CSV_DIRECTORY%" (
    mkdir "%CSV_DIRECTORY%"
    echo [SUCCESS] Created CSV directory: %CSV_DIRECTORY%
) else (
    echo [INFO] CSV directory already exists: %CSV_DIRECTORY%
)
exit /b 0

:run_sql_script
set script_file=%~1
set description=%~2

if not exist "%script_file%" (
    echo [ERROR] Script file not found: %script_file%
    exit /b 1
)

echo [INFO] Running %description%...

set PGPASSWORD=%DB_PASSWORD%
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "%script_file%" >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    echo [ERROR] %description% failed. Check log file: %LOG_FILE%
    exit /b 1
)

echo [SUCCESS] %description% completed successfully
exit /b 0

:run_python_migration
set script_file=05_postgresql_import_script.py

if not exist "%script_file%" (
    echo [WARNING] Python migration script not found: %script_file%
    echo [WARNING] Skipping CSV import. You can run it manually later.
    exit /b 0
)

python --version >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Python not found. Skipping CSV import.
    echo [WARNING] You can run the Python script manually: python %script_file%
    exit /b 0
)

echo [INFO] Running Python migration script...

REM Check if required Python packages are installed
python -c "import psycopg2, pandas" >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Required Python packages not found.
    echo [WARNING] Please install them with: pip install psycopg2-binary pandas python-dotenv
    echo [WARNING] Skipping CSV import.
    exit /b 0
)

python "%script_file%" >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    echo [ERROR] Python migration failed. Check log file: %LOG_FILE%
    exit /b 1
)

echo [SUCCESS] Python migration completed successfully
exit /b 0

:verify_migration
echo [INFO] Verifying migration...

set PGPASSWORD=%DB_PASSWORD%
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -c "
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
"

if errorlevel 1 (
    echo [WARNING] Migration verification failed
) else (
    echo [SUCCESS] Migration verification completed
)
exit /b 0

:error_exit
echo [ERROR] Migration failed. Check the log file for details: %LOG_FILE%
exit /b 1

:end
endlocal
