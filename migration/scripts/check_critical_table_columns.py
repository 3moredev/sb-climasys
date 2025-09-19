#!/usr/bin/env python3
"""
Check Critical Table Column Structure
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
    """Check column structure of a table"""
    cursor = conn.cursor()
    
    cursor.execute("""
        SELECT column_name, data_type, is_nullable, column_default
        FROM information_schema.columns
        WHERE table_schema = 'climasys_dev' AND table_name = %s
        ORDER BY ordinal_position
    """, (table_name,))
    
    columns = cursor.fetchall()
    cursor.close()
    
    logger.info(f"📋 Table: {table_name}")
    for col in columns:
        logger.info(f"  - {col[0]}: {col[1]} (nullable: {col[2]}, default: {col[3]})")
    
    return columns

def main():
    """Main function"""
    conn = get_postgres_connection()
    if not conn:
        return
    
    # Check critical tables
    critical_tables = [
        'clinic_master',
        'doctor_master', 
        'user_master',
        'patient_master',
        'patient_visits'
    ]
    
    for table in critical_tables:
        check_table_columns(conn, table)
        logger.info("")
    
    conn.close()

if __name__ == "__main__":
    main()
