#!/usr/bin/env python3
"""
Check Data Status - Verify Real vs Sample Data
This script helps you verify whether you have real SQL Server data or sample data.
"""

import psycopg2
import logging
import os

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_postgres_connection():
    """Get PostgreSQL connection"""
    try:
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        return conn
    except Exception as e:
        logger.error(f"❌ Error connecting to PostgreSQL: {e}")
        return None

def check_table_data_quality(cursor, table_name):
    """Check if table contains real data or sample data"""
    try:
        # Get total count
        cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table_name}")
        total_count = cursor.fetchone()[0]
        
        if total_count == 0:
            return "EMPTY", 0, "No data"
        
        # Get sample of data to analyze
        cursor.execute(f"SELECT * FROM climasys_dev.{table_name} LIMIT 5")
        sample_data = cursor.fetchall()
        
        # Get column names
        cursor.execute(f"""
            SELECT column_name 
            FROM information_schema.columns 
            WHERE table_schema = 'climasys_dev' 
            AND table_name = '{table_name}'
            ORDER BY ordinal_position
        """)
        columns = [row[0] for row in cursor.fetchall()]
        
        # Analyze data quality
        sample_text = str(sample_data).lower()
        
        # Check for sample data indicators
        sample_indicators = [
            'sample', 'test', 'demo', 'example', 'dummy',
            'user1', 'user2', 'admin@example.com', 'test@test.com',
            'john doe', 'jane smith', 'sample patient', 'test doctor'
        ]
        
        is_sample_data = any(indicator in sample_text for indicator in sample_indicators)
        
        if is_sample_data:
            return "SAMPLE", total_count, "Contains sample/test data"
        else:
            return "REAL", total_count, "Appears to be real data"
            
    except Exception as e:
        return "ERROR", 0, f"Error: {e}"

def check_csv_files():
    """Check if real SQL Server CSV files exist"""
    csv_dir = "real_sql_server_data"
    if not os.path.exists(csv_dir):
        return False, "Directory does not exist"
    
    expected_files = [
        'user_master.csv', 'patient_master.csv', 'doctor_master.csv',
        'clinic_master.csv', 'patient_visits.csv', 'role_master.csv',
        'gender_master.csv', 'blood_group_master.csv', 'language_master.csv',
        'shift_master.csv', 'model.csv', 'model_config_params.csv'
    ]
    
    existing_files = []
    missing_files = []
    
    for file in expected_files:
        file_path = os.path.join(csv_dir, file)
        if os.path.exists(file_path):
            existing_files.append(file)
        else:
            missing_files.append(file)
    
    return len(existing_files) > 0, f"Found {len(existing_files)}/{len(expected_files)} CSV files"

def main():
    """Main function"""
    logger.info("🔍 Checking Data Status - Real vs Sample Data")
    logger.info("=" * 60)
    
    # Check CSV files
    csv_exists, csv_status = check_csv_files()
    logger.info(f"📁 CSV Files Status: {csv_status}")
    
    if csv_exists:
        logger.info("✅ Real SQL Server CSV files found!")
        logger.info("📋 To import real data, run: python import_real_sql_server_data.py")
    else:
        logger.info("⚠️  No real SQL Server CSV files found")
        logger.info("📋 To extract real data, follow the SQL_SERVER_DATA_EXTRACTION_GUIDE.md")
    
    logger.info("")
    
    # Check PostgreSQL data
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Cannot connect to PostgreSQL")
        return
    
    cursor = conn.cursor()
    
    # Critical tables to check
    critical_tables = [
        'user_master', 'patient_master', 'doctor_master', 'clinic_master',
        'patient_visits', 'role_master', 'gender_master', 'blood_group_master',
        'language_master', 'shift_master', 'model', 'model_config_params'
    ]
    
    logger.info("📊 PostgreSQL Data Analysis:")
    logger.info("-" * 60)
    
    real_data_tables = 0
    sample_data_tables = 0
    empty_tables = 0
    error_tables = 0
    total_records = 0
    
    for table in critical_tables:
        status, count, description = check_table_data_quality(cursor, table)
        
        status_emoji = {
            'REAL': '✅',
            'SAMPLE': '⚠️ ',
            'EMPTY': '❌',
            'ERROR': '💥'
        }
        
        logger.info(f"{status_emoji.get(status, '❓')} {table}: {count:,} records - {description}")
        
        if status == 'REAL':
            real_data_tables += 1
        elif status == 'SAMPLE':
            sample_data_tables += 1
        elif status == 'EMPTY':
            empty_tables += 1
        else:
            error_tables += 1
        
        total_records += count
    
    cursor.close()
    conn.close()
    
    logger.info("")
    logger.info("📈 Summary:")
    logger.info(f"  - Tables with real data: {real_data_tables}")
    logger.info(f"  - Tables with sample data: {sample_data_tables}")
    logger.info(f"  - Empty tables: {empty_tables}")
    logger.info(f"  - Tables with errors: {error_tables}")
    logger.info(f"  - Total records: {total_records:,}")
    
    logger.info("")
    if sample_data_tables > 0:
        logger.info("⚠️  You have sample data in your database!")
        logger.info("📋 To replace with real data:")
        logger.info("1. Extract data from SQL Server (see SQL_SERVER_DATA_EXTRACTION_GUIDE.md)")
        logger.info("2. Run: python import_real_sql_server_data.py")
    elif real_data_tables > 0:
        logger.info("✅ You have real data in your database!")
    else:
        logger.info("❌ No data found in critical tables")
        logger.info("📋 Import data using the migration scripts")

if __name__ == "__main__":
    main()