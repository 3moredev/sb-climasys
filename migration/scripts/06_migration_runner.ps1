# =====================================================
# Climasys Migration Runner Script (PowerShell)
# =====================================================
# 
# This script automates the complete migration process
# from SQL Server to PostgreSQL on Windows systems
#
# Usage: .\06_migration_runner.ps1
#
# =====================================================

param(
    [switch]$SkipSchema,
    [switch]$SkipReference,
    [switch]$SkipMigration,
    [switch]$SkipCsv,
    [switch]$VerifyOnly,
    [switch]$Help
)

# Configuration
$DB_HOST = if ($env:DB_HOST) { $env:DB_HOST } else { "localhost" }
$DB_PORT = if ($env:DB_PORT) { $env:DB_PORT } else { "5432" }
$DB_NAME = if ($env:DB_NAME) { $env:DB_NAME } else { "climasys_dev" }
$DB_USER = if ($env:DB_USER) { $env:DB_USER } else { "postgres" }
$DB_PASSWORD = if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "root" }
$CSV_DIRECTORY = if ($env:CSV_DIRECTORY) { $env:CSV_DIRECTORY } else { ".\csv_data" }

$LOG_FILE = "migration_$(Get-Date -Format 'yyyyMMdd_HHmmss').log"

# Colors for output
$Colors = @{
    Red = "Red"
    Green = "Green"
    Yellow = "Yellow"
    Blue = "Blue"
    White = "White"
}

function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Colors.Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor $Colors.Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor $Colors.Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $Colors.Red
}

function Show-Usage {
    Write-Host "Climasys Migration Runner (PowerShell)" -ForegroundColor $Colors.White
    Write-Host ""
    Write-Host "Usage: .\06_migration_runner.ps1 [OPTIONS]"
    Write-Host ""
    Write-Host "Options:"
    Write-Host "  -SkipSchema           Skip schema creation"
    Write-Host "  -SkipReference        Skip reference data insertion"
    Write-Host "  -SkipMigration        Skip data migration"
    Write-Host "  -SkipCsv              Skip CSV import"
    Write-Host "  -VerifyOnly           Only run verification"
    Write-Host "  -Help                 Show this help message"
    Write-Host ""
    Write-Host "Environment Variables:"
    Write-Host "  `$env:DB_HOST          PostgreSQL host (default: localhost)"
    Write-Host "  `$env:DB_PORT          PostgreSQL port (default: 5432)"
    Write-Host "  `$env:DB_NAME          Database name (default: climasys_dev)"
    Write-Host "  `$env:DB_USER          Database user (default: postgres)"
    Write-Host "  `$env:DB_PASSWORD      Database password (default: root)"
    Write-Host "  `$env:CSV_DIRECTORY    CSV files directory (default: .\csv_data)"
    Write-Host ""
    Write-Host "Examples:"
    Write-Host "  .\06_migration_runner.ps1                    # Run complete migration"
    Write-Host "  .\06_migration_runner.ps1 -SkipCsv          # Skip CSV import"
    Write-Host "  .\06_migration_runner.ps1 -VerifyOnly       # Only verify existing data"
    Write-Host "  `$env:DB_PASSWORD='mypass'; .\06_migration_runner.ps1  # Use custom password"
}

function Test-PostgreSQLConnection {
    Write-Status "Checking PostgreSQL connection..."
    
    # Check if psql is available
    try {
        $null = Get-Command psql -ErrorAction Stop
    }
    catch {
        Write-Error "psql command not found. Please install PostgreSQL client tools."
        return $false
    }
    
    # Test connection
    $env:PGPASSWORD = $DB_PASSWORD
    try {
        $result = psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT 1;" 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Connection failed"
        }
        Write-Success "PostgreSQL connection successful"
        return $true
    }
    catch {
        Write-Error "Cannot connect to PostgreSQL database."
        Write-Error "Please check your database configuration:"
        Write-Error "  Host: $DB_HOST"
        Write-Error "  Port: $DB_PORT"
        Write-Error "  Database: $DB_NAME"
        Write-Error "  User: $DB_USER"
        return $false
    }
}

function New-CsvDirectory {
    Write-Status "Creating CSV directory..."
    
    if (-not (Test-Path $CSV_DIRECTORY)) {
        New-Item -ItemType Directory -Path $CSV_DIRECTORY -Force | Out-Null
        Write-Success "Created CSV directory: $CSV_DIRECTORY"
    } else {
        Write-Status "CSV directory already exists: $CSV_DIRECTORY"
    }
}

function Invoke-SqlScript {
    param(
        [string]$ScriptFile,
        [string]$Description
    )
    
    if (-not (Test-Path $ScriptFile)) {
        Write-Error "Script file not found: $ScriptFile"
        return $false
    }
    
    Write-Status "Running $Description..."
    
    $env:PGPASSWORD = $DB_PASSWORD
    try {
        $result = psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f $ScriptFile 2>&1 | Tee-Object -FilePath $LOG_FILE -Append
        if ($LASTEXITCODE -ne 0) {
            throw "Script execution failed"
        }
        Write-Success "$Description completed successfully"
        return $true
    }
    catch {
        Write-Error "$Description failed. Check log file: $LOG_FILE"
        return $false
    }
}

function Invoke-PythonMigration {
    $scriptFile = "05_postgresql_import_script.py"
    
    if (-not (Test-Path $scriptFile)) {
        Write-Warning "Python migration script not found: $scriptFile"
        Write-Warning "Skipping CSV import. You can run it manually later."
        return $true
    }
    
    try {
        $null = Get-Command python -ErrorAction Stop
    }
    catch {
        Write-Warning "Python not found. Skipping CSV import."
        Write-Warning "You can run the Python script manually: python $scriptFile"
        return $true
    }
    
    Write-Status "Running Python migration script..."
    
    # Check if required Python packages are installed
    try {
        python -c "import psycopg2, pandas" 2>$null
        if ($LASTEXITCODE -ne 0) {
            throw "Required packages not found"
        }
    }
    catch {
        Write-Warning "Required Python packages not found."
        Write-Warning "Please install them with: pip install psycopg2-binary pandas python-dotenv"
        Write-Warning "Skipping CSV import."
        return $true
    }
    
    try {
        $result = python $scriptFile 2>&1 | Tee-Object -FilePath $LOG_FILE -Append
        if ($LASTEXITCODE -ne 0) {
            throw "Python script execution failed"
        }
        Write-Success "Python migration completed successfully"
        return $true
    }
    catch {
        Write-Error "Python migration failed. Check log file: $LOG_FILE"
        return $false
    }
}

function Test-Migration {
    Write-Status "Verifying migration..."
    
    $verificationSql = @"
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
"@
    
    $env:PGPASSWORD = $DB_PASSWORD
    try {
        $result = psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c $verificationSql
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Migration verification completed"
        } else {
            Write-Warning "Migration verification failed"
        }
    }
    catch {
        Write-Warning "Migration verification failed"
    }
}

# Main execution
if ($Help) {
    Show-Usage
    exit 0
}

Write-Status "Starting Climasys Migration Process"
Write-Status "Log file: $LOG_FILE"

# Check PostgreSQL connection
if (-not (Test-PostgreSQLConnection)) {
    Write-Error "Migration failed due to database connection issues."
    exit 1
}

if ($VerifyOnly) {
    Test-Migration
    exit 0
}

# Create CSV directory
New-CsvDirectory

# Run migration steps
if (-not $SkipSchema) {
    if (-not (Invoke-SqlScript "01_create_schema.sql" "Schema Creation")) {
        Write-Error "Migration failed during schema creation."
        exit 1
    }
}

if (-not $SkipReference) {
    if (-not (Invoke-SqlScript "02_insert_reference_data.sql" "Reference Data Insertion")) {
        Write-Error "Migration failed during reference data insertion."
        exit 1
    }
}

if (-not $SkipMigration) {
    if (-not (Invoke-SqlScript "03_migrate_from_sqlserver.sql" "Data Migration Functions")) {
        Write-Error "Migration failed during data migration."
        exit 1
    }
}

if (-not $SkipCsv) {
    if (-not (Invoke-PythonMigration)) {
        Write-Error "Migration failed during CSV import."
        exit 1
    }
}

# Verify migration
Test-Migration

Write-Success "Migration process completed!"
Write-Status "Check the log file for detailed information: $LOG_FILE"
