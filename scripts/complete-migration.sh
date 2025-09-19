#!/bin/bash

# =====================================================
# Climasys Complete Migration Script
# SQL Server to PostgreSQL Migration
# =====================================================

set -e  # Exit on any error

echo "=========================================="
echo "Climasys Complete Migration Script"
echo "SQL Server to PostgreSQL Migration"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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
    local host=$1
    local port=$2
    local username=$3
    local password=$4
    local database=$5
    
    print_status "Testing PostgreSQL connection..."
    
    if PGPASSWORD="$password" psql -h "$host" -p "$port" -U "$username" -d "$database" -c "SELECT 1;" >/dev/null 2>&1; then
        print_success "PostgreSQL connection successful!"
        return 0
    else
        print_error "PostgreSQL connection failed!"
        return 1
    fi
}

# Function to create database if it doesn't exist
create_database() {
    local host=$1
    local port=$2
    local username=$3
    local password=$4
    local database=$5
    
    print_status "Creating database '$database' if it doesn't exist..."
    
    if PGPASSWORD="$password" psql -h "$host" -p "$port" -U "$username" -d postgres -c "CREATE DATABASE $database;" 2>/dev/null; then
        print_success "Database '$database' created successfully!"
    else
        print_warning "Database '$database' might already exist or there was an error."
    fi
}

# Function to run SQL script
run_sql_script() {
    local script_path=$1
    local host=$2
    local port=$3
    local username=$4
    local password=$5
    local database=$6
    
    if [ ! -f "$script_path" ]; then
        print_error "Script file not found: $script_path"
        return 1
    fi
    
    print_status "Running script: $(basename "$script_path")"
    
    if PGPASSWORD="$password" psql -h "$host" -p "$port" -U "$username" -d "$database" -f "$script_path" >/dev/null 2>&1; then
        print_success "Script executed successfully: $(basename "$script_path")"
        return 0
    else
        print_error "Script execution failed: $(basename "$script_path")"
        return 1
    fi
}

# Function to run Python script
run_python_script() {
    local script_path=$1
    local args=$2
    
    if [ ! -f "$script_path" ]; then
        print_error "Python script not found: $script_path"
        return 1
    fi
    
    print_status "Running Python script: $(basename "$script_path")"
    
    if python3 "$script_path" $args; then
        print_success "Python script executed successfully: $(basename "$script_path")"
        return 0
    else
        print_error "Python script execution failed: $(basename "$script_path")"
        return 1
    fi
}

# Main migration function
main() {
    echo "Starting Climasys migration process..."
    echo ""
    
    # Check prerequisites
    print_status "Checking prerequisites..."
    
    if ! command_exists psql; then
        print_error "PostgreSQL client (psql) is not installed or not in PATH."
        print_error "Please install PostgreSQL client tools."
        exit 1
    fi
    
    if ! command_exists python3; then
        print_error "Python 3 is not installed or not in PATH."
        print_error "Please install Python 3."
        exit 1
    fi
    
    print_success "Prerequisites check passed!"
    echo ""
    
    # Get database configuration
    print_status "Please provide PostgreSQL configuration:"
    echo ""
    
    read -p "PostgreSQL host (default: localhost): " DB_HOST
    DB_HOST=${DB_HOST:-localhost}
    
    read -p "PostgreSQL port (default: 5432): " DB_PORT
    DB_PORT=${DB_PORT:-5432}
    
    read -p "PostgreSQL username (default: postgres): " DB_USERNAME
    DB_USERNAME=${DB_USERNAME:-postgres}
    
    read -s -p "PostgreSQL password: " DB_PASSWORD
    echo ""
    
    read -p "Database name (default: climasys_dev): " DB_NAME
    DB_NAME=${DB_NAME:-climasys_dev}
    
    echo ""
    
    # Create database
    create_database "$DB_HOST" "$DB_PORT" "$DB_USERNAME" "$DB_PASSWORD" "$DB_NAME"
    
    # Test connection
    if ! check_postgres_connection "$DB_HOST" "$DB_PORT" "$DB_USERNAME" "$DB_PASSWORD" "$DB_NAME"; then
        print_error "Cannot proceed without database connection."
        exit 1
    fi
    
    echo ""
    print_status "Starting migration process..."
    echo ""
    
    # Set migration directory
    MIGRATION_DIR="../../migration/scripts"
    
    # Step 1: Create schema
    print_status "Step 1: Creating database schema..."
    if run_sql_script "$MIGRATION_DIR/01_create_schema.sql" "$DB_HOST" "$DB_PORT" "$DB_USERNAME" "$DB_PASSWORD" "$DB_NAME"; then
        print_success "Schema creation completed!"
    else
        print_error "Schema creation failed!"
        exit 1
    fi
    echo ""
    
    # Step 2: Insert reference data
    print_status "Step 2: Inserting reference data..."
    if run_sql_script "$MIGRATION_DIR/02_insert_reference_data.sql" "$DB_HOST" "$DB_PORT" "$DB_USERNAME" "$DB_PASSWORD" "$DB_NAME"; then
        print_success "Reference data insertion completed!"
    else
        print_error "Reference data insertion failed!"
        exit 1
    fi
    echo ""
    
    # Step 3: Create stored procedures/functions
    print_status "Step 3: Creating stored procedures and functions..."
    if run_sql_script "$MIGRATION_DIR/09_migrate_stored_procedures.sql" "$DB_HOST" "$DB_PORT" "$DB_USERNAME" "$DB_PASSWORD" "$DB_NAME"; then
        print_success "Stored procedures creation completed!"
    else
        print_warning "Stored procedures creation had issues (this might be expected if they already exist)."
    fi
    echo ""
    
    # Step 4: Check for CSV data migration
    CSV_DIR="$MIGRATION_DIR/csv_data"
    if [ -d "$CSV_DIR" ] && [ "$(ls -A "$CSV_DIR" 2>/dev/null)" ]; then
        print_status "Step 4: Found CSV data files. Running data migration..."
        
        # Create .env file for Python scripts
        cat > "$MIGRATION_DIR/.env" << EOF
DB_HOST=$DB_HOST
DB_PORT=$DB_PORT
DB_NAME=$DB_NAME
DB_USER=$DB_USERNAME
DB_PASSWORD=$DB_PASSWORD
CSV_DIRECTORY=$CSV_DIR
EOF
        
        # Run Python import script
        if run_python_script "$MIGRATION_DIR/05_postgresql_import_script.py"; then
            print_success "CSV data migration completed!"
        else
            print_warning "CSV data migration had issues. Check the logs for details."
        fi
    else
        print_warning "Step 4: No CSV data files found. Skipping data migration."
        print_warning "To migrate data from SQL Server:"
        print_warning "1. Run the queries from 04_sqlserver_extraction_queries.sql on your SQL Server"
        print_warning "2. Export results to CSV files in the csv_data directory"
        print_warning "3. Re-run this script"
    fi
    echo ""
    
    # Step 5: Create application configuration
    print_status "Step 5: Creating application configuration..."
    
    # Create .env file for the application
    cat > .env << EOF
# PostgreSQL Configuration
DB_PASSWORD=$DB_PASSWORD
DB_HOST=$DB_HOST
DB_PORT=$DB_PORT
DB_NAME=$DB_NAME
DB_USERNAME=$DB_USERNAME

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-in-production-$(date +%s)

# Climasys Configuration
CLIMASYS_ENCRYPTION_KEY=PA1ANDE61INI6

# Spring Profile
SPRING_PROFILES_ACTIVE=dev

# API Configuration
APP_API_BASE_URL=http://localhost:8080/api
EOF
    
    print_success "Application configuration created!"
    echo ""
    
    # Step 6: Verification
    print_status "Step 6: Verifying migration..."
    
    # Check if tables exist
    TABLE_COUNT=$(PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'climasys_dev';" 2>/dev/null | tr -d ' ')
    
    if [ "$TABLE_COUNT" -gt 0 ]; then
        print_success "Migration verification passed! Found $TABLE_COUNT tables in climasys_dev schema."
    else
        print_error "Migration verification failed! No tables found in climasys_dev schema."
        exit 1
    fi
    
    echo ""
    print_success "=========================================="
    print_success "Migration completed successfully!"
    print_success "=========================================="
    echo ""
    
    print_status "Next steps:"
    echo "1. Update your application.yml with the database configuration"
    echo "2. Run: mvn clean install"
    echo "3. Run: mvn spring-boot:run"
    echo "4. Test the application endpoints"
    echo ""
    
    print_status "Database configuration:"
    echo "- Host: $DB_HOST"
    echo "- Port: $DB_PORT"
    echo "- Database: $DB_NAME"
    echo "- Username: $DB_USERNAME"
    echo "- Schema: climasys_dev"
    echo ""
    
    print_status "Configuration files created:"
    echo "- .env (application environment variables)"
    echo "- ../../migration/scripts/.env (migration environment variables)"
    echo ""
}

# Run main function
main "$@"

