#!/usr/bin/env python3
"""
Clear Sample Data from PostgreSQL Database
This script removes all sample data and prepares the database for real SQL Server data.
"""

import psycopg2
import logging
from psycopg2 import sql

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

def clear_table_data(cursor, table_name):
    """Clear all data from a table"""
    try:
        # Disable foreign key checks temporarily
        cursor.execute("SET session_replication_role = replica;")
        
        # Clear the table
        cursor.execute(f"TRUNCATE TABLE climasys_dev.{table_name} RESTART IDENTITY CASCADE;")
        
        # Re-enable foreign key checks
        cursor.execute("SET session_replication_role = DEFAULT;")
        
        logger.info(f"✅ Cleared all data from {table_name}")
        return True
    except Exception as e:
        logger.error(f"❌ Failed to clear {table_name}: {e}")
        return False

def clear_all_sample_data():
    """Clear all sample data from the database"""
    logger.info("🗑️  Clearing All Sample Data from PostgreSQL Database")
    logger.info("=" * 60)
    
    conn = get_postgres_connection()
    if not conn:
        return False
    
    cursor = conn.cursor()
    
    # List of tables to clear (in dependency order)
    tables_to_clear = [
        # Child tables first (to avoid foreign key violations)
        'patient_visits',
        'user_role',
        'doctor_clinic_shift',
        'doctor_model',
        'model_config_params',
        
        # Main tables
        'user_master',
        'patient_master',
        'doctor_master',
        'clinic_master',
        
        # Reference tables
        'role_master',
        'gender_master',
        'blood_group_master',
        'language_master',
        'shift_master',
        'model',
        
        # Other tables
        'system_params',
        'license_key',
        'medicine_master'
    ]
    
    successful_clears = 0
    failed_clears = 0
    
    for table in tables_to_clear:
        logger.info(f"🗑️  Clearing {table}...")
        if clear_table_data(cursor, table):
            successful_clears += 1
        else:
            failed_clears += 1
    
    # Commit all changes
    conn.commit()
    
    cursor.close()
    conn.close()
    
    logger.info("")
    logger.info("📊 Clear Data Summary:")
    logger.info(f"  - Successfully cleared: {successful_clears} tables")
    logger.info(f"  - Failed to clear: {failed_clears} tables")
    
    if failed_clears == 0:
        logger.info("✅ All sample data cleared successfully!")
        return True
    else:
        logger.warning(f"⚠️  {failed_clears} tables failed to clear")
        return False

def verify_empty_database():
    """Verify that the database is now empty"""
    logger.info("")
    logger.info("🔍 Verifying Database is Empty")
    logger.info("=" * 40)
    
    conn = get_postgres_connection()
    if not conn:
        return False
    
    cursor = conn.cursor()
    
    # Check key tables
    key_tables = [
        'user_master', 'patient_master', 'doctor_master', 'clinic_master',
        'patient_visits', 'role_master', 'gender_master', 'blood_group_master'
    ]
    
    total_records = 0
    empty_tables = 0
    
    for table in key_tables:
        try:
            cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cursor.fetchone()[0]
            total_records += count
            
            if count == 0:
                logger.info(f"✅ {table}: {count} records (empty)")
                empty_tables += 1
            else:
                logger.warning(f"⚠️  {table}: {count} records (not empty)")
        except Exception as e:
            logger.error(f"❌ Error checking {table}: {e}")
    
    cursor.close()
    conn.close()
    
    logger.info("")
    logger.info(f"📊 Verification Summary:")
    logger.info(f"  - Empty tables: {empty_tables}/{len(key_tables)}")
    logger.info(f"  - Total records remaining: {total_records}")
    
    if total_records == 0:
        logger.info("✅ Database is completely empty - ready for real data!")
        return True
    else:
        logger.warning(f"⚠️  {total_records} records still remain in the database")
        return False

def main():
    """Main function"""
    logger.info("🚀 Starting Sample Data Cleanup")
    logger.info("=" * 60)
    logger.info("This will remove ALL sample data from your PostgreSQL database.")
    logger.info("Make sure you have backed up any important data!")
    logger.info("")
    
    # Clear all sample data
    if clear_all_sample_data():
        # Verify the database is empty
        if verify_empty_database():
            logger.info("")
            logger.info("🎉 Sample Data Cleanup Complete!")
            logger.info("=" * 60)
            logger.info("✅ Your database is now empty and ready for real SQL Server data.")
            logger.info("")
            logger.info("📋 Next Steps:")
            logger.info("1. Extract data from your SQL Server database")
            logger.info("2. Run: python import_real_sql_server_data.py")
            logger.info("3. Verify the import with: python check_data_status.py")
        else:
            logger.warning("⚠️  Some data may still remain in the database")
    else:
        logger.error("❌ Failed to clear sample data completely")

if __name__ == "__main__":
    main()
