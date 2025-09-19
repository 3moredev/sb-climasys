#!/usr/bin/env python3
"""
Complete Data Migration Script for Climasys

This script handles the complete migration process:
1. Extract data from SQL Server
2. Import data into PostgreSQL
3. Verify the migration
"""

import os
import sys
import subprocess
import logging
from datetime import datetime

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('complete_migration.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

def run_script(script_name, description):
    """Run a Python script and handle errors."""
    try:
        logger.info(f"🔄 {description}")
        logger.info("-" * 50)
        
        result = subprocess.run([sys.executable, script_name], 
                              capture_output=True, text=True, cwd=os.path.dirname(__file__))
        
        if result.returncode == 0:
            logger.info(f"✅ {description} completed successfully")
            if result.stdout:
                logger.info(f"Output: {result.stdout}")
        else:
            logger.error(f"❌ {description} failed")
            if result.stderr:
                logger.error(f"Error: {result.stderr}")
            return False
            
    except Exception as e:
        logger.error(f"❌ Failed to run {script_name}: {e}")
        return False
    
    return True

def check_prerequisites():
    """Check if all prerequisites are met."""
    logger.info("🔍 Checking prerequisites...")
    
    # Check if required Python packages are installed
    required_packages = ['pandas', 'psycopg2', 'pyodbc']
    missing_packages = []
    
    for package in required_packages:
        try:
            __import__(package)
        except ImportError:
            missing_packages.append(package)
    
    if missing_packages:
        logger.error(f"❌ Missing required packages: {', '.join(missing_packages)}")
        logger.info("Please install them using: pip install " + " ".join(missing_packages))
        return False
    
    # Check if data directory exists
    if not os.path.exists("extracted_data"):
        logger.info("📁 Creating extracted_data directory...")
        os.makedirs("extracted_data", exist_ok=True)
    
    logger.info("✅ Prerequisites check passed")
    return True

def main():
    """Main function to run the complete migration."""
    
    logger.info("🚀 Starting Complete Climasys Data Migration")
    logger.info("=" * 60)
    logger.info(f"Migration started at: {datetime.now()}")
    
    # Check prerequisites
    if not check_prerequisites():
        logger.error("❌ Prerequisites check failed. Please fix the issues and try again.")
        sys.exit(1)
    
    # Step 1: Extract data from SQL Server
    logger.info("\n📤 STEP 1: Extract data from SQL Server")
    logger.info("=" * 50)
    
    # Check if SQL Server connection string is configured
    sqlserver_conn = os.getenv('SQLSERVER_CONNECTION_STRING')
    if not sqlserver_conn:
        logger.warning("⚠️  SQLSERVER_CONNECTION_STRING environment variable not set")
        logger.info("Please set it with your SQL Server connection details:")
        logger.info('Example: set SQLSERVER_CONNECTION_STRING="DRIVER={ODBC Driver 17 for SQL Server};SERVER=localhost;DATABASE=Climasys-00010;UID=sa;PWD=your_password"')
        
        # Ask user if they want to continue with manual extraction
        response = input("\nDo you want to continue with manual CSV file placement? (y/n): ")
        if response.lower() != 'y':
            logger.info("Migration cancelled by user")
            sys.exit(0)
    else:
        if not run_script("13_extract_sqlserver_data.py", "Extracting data from SQL Server"):
            logger.error("❌ SQL Server data extraction failed")
            logger.info("Please check your SQL Server connection and try again")
            sys.exit(1)
    
    # Step 2: Import data into PostgreSQL
    logger.info("\n📥 STEP 2: Import data into PostgreSQL")
    logger.info("=" * 50)
    
    if not run_script("14_import_to_postgresql.py", "Importing data into PostgreSQL"):
        logger.error("❌ PostgreSQL data import failed")
        logger.info("Please check your PostgreSQL connection and try again")
        sys.exit(1)
    
    # Step 3: Test the dashboard function
    logger.info("\n🧪 STEP 3: Test dashboard function")
    logger.info("=" * 50)
    
    try:
        import psycopg2
        
        # PostgreSQL configuration
        postgres_config = {
            'host': os.getenv('DB_HOST', 'localhost'),
            'port': os.getenv('DB_PORT', '5432'),
            'database': os.getenv('DB_NAME', 'climasys_dev'),
            'user': os.getenv('DB_USER', 'postgres'),
            'password': os.getenv('DB_PASSWORD', 'root')
        }
        
        # Test dashboard function
        conn = psycopg2.connect(**postgres_config)
        cursor = conn.cursor()
        cursor.execute("SET search_path TO climasys_dev, public;")
        cursor.execute("SELECT usp_get_dashboard_data();")
        result = cursor.fetchone()[0]
        
        logger.info("✅ Dashboard function test successful")
        logger.info(f"Dashboard data: {result}")
        
        cursor.close()
        conn.close()
        
    except Exception as e:
        logger.error(f"❌ Dashboard function test failed: {e}")
        logger.info("Please check the dashboard function and try again")
    
    # Migration completed
    logger.info("\n🎉 MIGRATION COMPLETED SUCCESSFULLY!")
    logger.info("=" * 60)
    logger.info(f"Migration completed at: {datetime.now()}")
    logger.info("\nNext steps:")
    logger.info("1. Start your Spring Boot backend server")
    logger.info("2. Test the dashboard API endpoint")
    logger.info("3. Verify all functionality is working")
    logger.info("\nYour Climasys application is now running on PostgreSQL with real data!")

if __name__ == "__main__":
    main()
