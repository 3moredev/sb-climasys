#!/usr/bin/env python3
"""
Inspect Actual Data - Examine the real data in PostgreSQL tables
This script shows you exactly what data is in your tables.
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

def inspect_table_data(cursor, table_name, limit=5):
    """Inspect actual data in a table"""
    try:
        # Get column names
        cursor.execute(f"""
            SELECT column_name, data_type
            FROM information_schema.columns 
            WHERE table_schema = 'climasys_dev' 
            AND table_name = '{table_name}'
            ORDER BY ordinal_position
        """)
        columns = cursor.fetchall()
        
        # Get sample data
        cursor.execute(f"SELECT * FROM climasys_dev.{table_name} LIMIT {limit}")
        rows = cursor.fetchall()
        
        # Get total count
        cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table_name}")
        total_count = cursor.fetchone()[0]
        
        logger.info(f"📋 Table: {table_name} ({total_count:,} total records)")
        logger.info("-" * 50)
        
        if columns:
            logger.info("Columns:")
            for col_name, col_type in columns:
                logger.info(f"  - {col_name}: {col_type}")
        
        logger.info("")
        logger.info("Sample Data:")
        if rows:
            for i, row in enumerate(rows, 1):
                logger.info(f"  Row {i}: {row}")
        else:
            logger.info("  No data found")
        
        logger.info("")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error inspecting {table_name}: {e}")
        return False

def main():
    """Main function"""
    logger.info("🔍 Inspecting Actual Data in PostgreSQL Tables")
    logger.info("=" * 60)
    
    conn = get_postgres_connection()
    if not conn:
        return
    
    cursor = conn.cursor()
    
    # Critical tables to inspect
    critical_tables = [
        'user_master', 'patient_master', 'doctor_master', 'clinic_master',
        'patient_visits', 'role_master', 'gender_master', 'blood_group_master'
    ]
    
    for table in critical_tables:
        inspect_table_data(cursor, table)
    
    cursor.close()
    conn.close()
    
    logger.info("🎉 Data inspection complete!")

if __name__ == "__main__":
    main()
