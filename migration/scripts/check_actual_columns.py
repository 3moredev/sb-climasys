#!/usr/bin/env python3
"""
Check Actual Column Names in PostgreSQL Tables
"""

import psycopg2
import logging

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

def check_table_columns(conn, table_name):
    """Check actual column names in a table"""
    try:
        cursor = conn.cursor()
        cursor.execute("""
            SELECT column_name, data_type
            FROM information_schema.columns
            WHERE table_schema = 'climasys_dev' AND table_name = %s
            ORDER BY ordinal_position
        """, (table_name,))
        
        columns = cursor.fetchall()
        cursor.close()
        return columns
    except Exception as e:
        logger.error(f"❌ Error getting columns for {table_name}: {e}")
        return []

def main():
    """Main function"""
    logger.info("🔍 Checking Actual Column Names in PostgreSQL Tables")
    logger.info("=" * 70)
    
    # Get PostgreSQL connection
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Could not connect to PostgreSQL")
        return False
    
    # Check key tables
    tables_to_check = [
        'clinic_master',
        'doctor_master', 
        'patient_master',
        'patient_visits',
        'user_master',
        'system_params'
    ]
    
    for table_name in tables_to_check:
        logger.info(f"📊 Checking {table_name}...")
        columns = check_table_columns(conn, table_name)
        
        if columns:
            logger.info(f"  Found {len(columns)} columns:")
            for col_name, data_type in columns:
                logger.info(f"    - {col_name} ({data_type})")
        else:
            logger.warning(f"  No columns found for {table_name}")
        
        logger.info("")
    
    conn.close()
    return True

if __name__ == "__main__":
    main()
