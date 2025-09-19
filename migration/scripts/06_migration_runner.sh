#!/bin/bash

# =====================================================
# Climasys Migration Runner Script
# =====================================================
# 
# This script automates the complete migration process
# from SQL Server to PostgreSQL
#
# Usage: ./06_migration_runner.sh
#
# =====================================================

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-climasys_dev}
DB_USER=${DB_USER:-postgres}
DB_PASSWORD=${DB_PASSWORD:-root}
CSV_DIRECTORY=${CSV_DIRECTORY:-./csv_data}
LOG_FILE="migration_$(date +%Y%m%d_%H%M%S).log"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check PostgreSQL connection
check_postgres_connection() {
    print_status "Checking PostgreSQL connection..."
    
    if ! command_exists psql; then
        print_error "psql command not found. Please install PostgreSQL client tools."
        exit 1
    fi
    
    if ! PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT 1;" >/dev/null 2>&1; then
        print_error "Cannot connect to PostgreSQL database."
        print_error "Please check your database configuration:"
        print_error "  Host: $DB_HOST"
        print_error "  Port: $DB_PORT"
        print_error "  Database: $DB_NAME"
        print_error "  User: $DB_USER"
        exit 1
    fi
    
    print_success "PostgreSQL connection successful"
}

# Function to create CSV directory
create_csv_directory() {
    print_status "Creating CSV directory..."
    
    if [ ! -d "$CSV_DIRECTORY" ]; then
        mkdir -p "$CSV_DIRECTORY"
        print_success "Created CSV directory: $CSV_DIRECTORY"
    else
        print_status "CSV directory already exists: $CSV_DIRECTORY"
    fi
}

# Function to run SQL scripts
run_sql_script() {
    local script_file="$1"
    local description="$2"
    
    if [ ! -f "$script_file" ]; then
        print_error "Script file not found: $script_file"
        return 1
    fi
    
    print_status "Running $description..."
    
    if PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$script_file" >> "$LOG_FILE" 2>&1; then
        print_success "$description completed successfully"
        return 0
    else
        print_error "$description failed. Check log file: $LOG_FILE"
        return 1
    fi
}

# Function to check if Python script exists and run it
run_python_migration() {
    local script_file="$1"
    
    if [ ! -f "$script_file" ]; then
        print_warning "Python migration script not found: $script_file"
        print_warning "Skipping CSV import. You can run it manually later."
        return 0
    fi
    
    if ! command_exists python3; then
        print_warning "Python3 not found. Skipping CSV import."
        print_warning "You can run the Python script manually: python3 $script_file"
        return 0
    fi
    
    print_status "Running Python migration script..."
    
    # Check if required Python packages are installed
    if ! python3 -c "import psycopg2, pandas" >/dev/null 2>&1; then
        print_warning "Required Python packages not found."
        print_warning "Please install them with: pip install psycopg2-binary pandas python-dotenv"
        print_warning "Skipping CSV import."
        return 0
    fi
    
    if python3 "$script_file" >> "$LOG_FILE" 2>&1; then
        print_success "Python migration completed successfully"
        return 0
    else
        print_error "Python migration failed. Check log file: $LOG_FILE"
        return 1
    fi
}

# Function to verify migration
verify_migration() {
    print_status "Verifying migration..."
    
    local verification_sql="
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
    
    if PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "$verification_sql"; then
        print_success "Migration verification completed"
    else
        print_warning "Migration verification failed"
    fi
}

# Function to display usage
show_usage() {
    echo "Climasys Migration Runner"
    echo ""
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help              Show this help message"
    echo "  --skip-schema           Skip schema creation"
    echo "  --skip-reference        Skip reference data insertion"
    echo "  --skip-migration        Skip data migration"
    echo "  --skip-csv              Skip CSV import"
    echo "  --verify-only           Only run verification"
    echo ""
    echo "Environment Variables:"
    echo "  DB_HOST                 PostgreSQL host (default: localhost)"
    echo "  DB_PORT                 PostgreSQL port (default: 5432)"
    echo "  DB_NAME                 Database name (default: climasys_dev)"
    echo "  DB_USER                 Database user (default: postgres)"
    echo "  DB_PASSWORD             Database password (default: root)"
    echo "  CSV_DIRECTORY           CSV files directory (default: ./csv_data)"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Run complete migration"
    echo "  $0 --skip-csv                        # Skip CSV import"
    echo "  $0 --verify-only                     # Only verify existing data"
    echo "  DB_PASSWORD=mypass $0                # Use custom password"
}

# Main function
main() {
    local skip_schema=false
    local skip_reference=false
    local skip_migration=false
    local skip_csv=false
    local verify_only=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_usage
                exit 0
                ;;
            --skip-schema)
                skip_schema=true
                shift
                ;;
            --skip-reference)
                skip_reference=true
                shift
                ;;
            --skip-migration)
                skip_migration=true
                shift
                ;;
            --skip-csv)
                skip_csv=true
                shift
                ;;
            --verify-only)
                verify_only=true
                shift
                ;;
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
    
    print_status "Starting Climasys Migration Process"
    print_status "Log file: $LOG_FILE"
    
    # Check PostgreSQL connection
    check_postgres_connection
    
    if [ "$verify_only" = true ]; then
        verify_migration
        exit 0
    fi
    
    # Create CSV directory
    create_csv_directory
    
    # Run migration steps
    if [ "$skip_schema" = false ]; then
        run_sql_script "01_create_schema.sql" "Schema Creation"
    fi
    
    if [ "$skip_reference" = false ]; then
        run_sql_script "02_insert_reference_data.sql" "Reference Data Insertion"
    fi
    
    if [ "$skip_migration" = false ]; then
        run_sql_script "03_migrate_from_sqlserver.sql" "Data Migration Functions"
    fi
    
    if [ "$skip_csv" = false ]; then
        run_python_migration "05_postgresql_import_script.py"
    fi
    
    # Verify migration
    verify_migration
    
    print_success "Migration process completed!"
    print_status "Check the log file for detailed information: $LOG_FILE"
}

# Run main function
main "$@"
