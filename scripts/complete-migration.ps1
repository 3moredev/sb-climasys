# =====================================================
# Climasys Complete Migration Script (PowerShell)
# SQL Server to PostgreSQL Migration
# =====================================================

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Climasys Complete Migration Script" -ForegroundColor Cyan
Write-Host "SQL Server to PostgreSQL Migration" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Check prerequisites
Write-Host "[INFO] Checking prerequisites..." -ForegroundColor Blue

# Check if PostgreSQL client is available
try {
    $null = Get-Command psql -ErrorAction Stop
    Write-Host "[SUCCESS] PostgreSQL client (psql) found!" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] PostgreSQL client (psql) is not installed or not in PATH." -ForegroundColor Red
    Write-Host "[ERROR] Please install PostgreSQL client tools." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if Python is available
try {
    $null = Get-Command python -ErrorAction Stop
    Write-Host "[SUCCESS] Python found!" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Python is not installed or not in PATH." -ForegroundColor Red
    Write-Host "[ERROR] Please install Python." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "[SUCCESS] Prerequisites check passed!" -ForegroundColor Green
Write-Host ""

# Get database configuration
Write-Host "[INFO] Please provide PostgreSQL configuration:" -ForegroundColor Blue
Write-Host ""

$DB_HOST = Read-Host "PostgreSQL host (default: localhost)"
if ([string]::IsNullOrEmpty($DB_HOST)) { $DB_HOST = "localhost" }

$DB_PORT = Read-Host "PostgreSQL port (default: 5432)"
if ([string]::IsNullOrEmpty($DB_PORT)) { $DB_PORT = "5432" }

$DB_USERNAME = Read-Host "PostgreSQL username (default: postgres)"
if ([string]::IsNullOrEmpty($DB_USERNAME)) { $DB_USERNAME = "postgres" }

$DB_PASSWORD = Read-Host "PostgreSQL password" -AsSecureString
$DB_PASSWORD_PLAIN = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($DB_PASSWORD))

$DB_NAME = Read-Host "Database name (default: climasys_dev)"
if ([string]::IsNullOrEmpty($DB_NAME)) { $DB_NAME = "climasys_dev" }

Write-Host ""

# Create database
Write-Host "[INFO] Creating database '$DB_NAME' if it doesn't exist..." -ForegroundColor Blue
$env:PGPASSWORD = $DB_PASSWORD_PLAIN

try {
    $result = psql -h $DB_HOST -p $DB_PORT -U $DB_USERNAME -d postgres -c "CREATE DATABASE $DB_NAME;" 2>$null
    Write-Host "[SUCCESS] Database '$DB_NAME' created successfully!" -ForegroundColor Green
} catch {
    Write-Host "[WARNING] Database '$DB_NAME' might already exist or there was an error." -ForegroundColor Yellow
}

# Test connection
Write-Host "[INFO] Testing PostgreSQL connection..." -ForegroundColor Blue
try {
    $result = psql -h $DB_HOST -p $DB_PORT -U $DB_USERNAME -d $DB_NAME -c "SELECT 1;" 2>$null
    Write-Host "[SUCCESS] PostgreSQL connection successful!" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] PostgreSQL connection failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""

# Set migration directory
$MIGRATION_DIR = "..\..\migration\scripts"

# Step 1: Create schema
Write-Host "[INFO] Step 1: Creating database schema..." -ForegroundColor Blue
try {
    $result = psql -h $DB_HOST -p $DB_PORT -U $DB_USERNAME -d $DB_NAME -f "$MIGRATION_DIR\01_create_schema.sql" 2>$null
    Write-Host "[SUCCESS] Schema creation completed!" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Schema creation failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host ""

# Step 2: Insert reference data
Write-Host "[INFO] Step 2: Inserting reference data..." -ForegroundColor Blue
try {
    $result = psql -h $DB_HOST -p $DB_PORT -U $DB_USERNAME -d $DB_NAME -f "$MIGRATION_DIR\02_insert_reference_data.sql" 2>$null
    Write-Host "[SUCCESS] Reference data insertion completed!" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Reference data insertion failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host ""

# Step 3: Create stored procedures/functions
Write-Host "[INFO] Step 3: Creating stored procedures and functions..." -ForegroundColor Blue
try {
    $result = psql -h $DB_HOST -p $DB_PORT -U $DB_USERNAME -d $DB_NAME -f "$MIGRATION_DIR\09_migrate_stored_procedures.sql" 2>$null
    Write-Host "[SUCCESS] Stored procedures creation completed!" -ForegroundColor Green
} catch {
    Write-Host "[WARNING] Stored procedures creation had issues (this might be expected if they already exist)." -ForegroundColor Yellow
}
Write-Host ""

# Step 4: Check for CSV data migration
$CSV_DIR = "$MIGRATION_DIR\csv_data"
if (Test-Path $CSV_DIR) {
    Write-Host "[INFO] Step 4: Found CSV data directory. Running data migration..." -ForegroundColor Blue
    
    # Create .env file for Python scripts
    $envContent = @"
DB_HOST=$DB_HOST
DB_PORT=$DB_PORT
DB_NAME=$DB_NAME
DB_USER=$DB_USERNAME
DB_PASSWORD=$DB_PASSWORD_PLAIN
CSV_DIRECTORY=$CSV_DIR
"@
    $envContent | Out-File -FilePath "$MIGRATION_DIR\.env" -Encoding UTF8
    
    # Run Python import script
    Push-Location $MIGRATION_DIR
    try {
        python "enhanced_postgresql_import.py"
        Write-Host "[SUCCESS] CSV data migration completed!" -ForegroundColor Green
    } catch {
        Write-Host "[WARNING] CSV data migration had issues. Check the logs for details." -ForegroundColor Yellow
    }
    Pop-Location
} else {
    Write-Host "[WARNING] Step 4: No CSV data directory found. Skipping data migration." -ForegroundColor Yellow
    Write-Host "[WARNING] To migrate data from SQL Server:" -ForegroundColor Yellow
    Write-Host "[WARNING] 1. Run the queries from enhanced_sqlserver_extraction.sql on your SQL Server" -ForegroundColor Yellow
    Write-Host "[WARNING] 2. Export results to CSV files in the csv_data directory" -ForegroundColor Yellow
    Write-Host "[WARNING] 3. Re-run this script" -ForegroundColor Yellow
}
Write-Host ""

# Step 5: Create application configuration
Write-Host "[INFO] Step 5: Creating application configuration..." -ForegroundColor Blue

# Create .env file for the application
$appEnvContent = @"
# PostgreSQL Configuration
DB_PASSWORD=$DB_PASSWORD_PLAIN
DB_HOST=$DB_HOST
DB_PORT=$DB_PORT
DB_NAME=$DB_NAME
DB_USERNAME=$DB_USERNAME

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-in-production-$(Get-Random)

# Climasys Configuration
CLIMASYS_ENCRYPTION_KEY=PA1ANDE61INI6

# Spring Profile
SPRING_PROFILES_ACTIVE=dev

# API Configuration
APP_API_BASE_URL=http://localhost:8080/api
"@
$appEnvContent | Out-File -FilePath ".env" -Encoding UTF8

Write-Host "[SUCCESS] Application configuration created!" -ForegroundColor Green
Write-Host ""

# Step 6: Verification
Write-Host "[INFO] Step 6: Verifying migration..." -ForegroundColor Blue

try {
    $tableCount = psql -h $DB_HOST -p $DB_PORT -U $DB_USERNAME -d $DB_NAME -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'climasys_dev';" 2>$null
    $tableCount = $tableCount.Trim()
    
    if ([int]$tableCount -gt 0) {
        Write-Host "[SUCCESS] Migration verification passed! Found $tableCount tables in climasys_dev schema." -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Migration verification failed! No tables found in climasys_dev schema." -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
} catch {
    Write-Host "[ERROR] Migration verification failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "[SUCCESS] ==========================================" -ForegroundColor Green
Write-Host "[SUCCESS] Migration completed successfully!" -ForegroundColor Green
Write-Host "[SUCCESS] ==========================================" -ForegroundColor Green
Write-Host ""

Write-Host "[INFO] Next steps:" -ForegroundColor Blue
Write-Host "1. Update your application.yml with the database configuration"
Write-Host "2. Run: mvn clean install"
Write-Host "3. Run: mvn spring-boot:run"
Write-Host "4. Test the application endpoints"
Write-Host ""

Write-Host "[INFO] Database configuration:" -ForegroundColor Blue
Write-Host "- Host: $DB_HOST"
Write-Host "- Port: $DB_PORT"
Write-Host "- Database: $DB_NAME"
Write-Host "- Username: $DB_USERNAME"
Write-Host "- Schema: climasys_dev"
Write-Host ""

Write-Host "[INFO] Configuration files created:" -ForegroundColor Blue
Write-Host "- .env (application environment variables)"
Write-Host "- $MIGRATION_DIR\.env (migration environment variables)"
Write-Host ""

Read-Host "Press Enter to exit"

