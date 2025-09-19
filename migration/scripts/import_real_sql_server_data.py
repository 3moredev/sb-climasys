#!/usr/bin/env python3
"""
Import Real SQL Server Data to PostgreSQL
This script imports the actual data extracted from SQL Server.
"""

import psycopg2
import pandas as pd
import logging
from datetime import datetime
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

def import_csv_data(table_name, csv_file_path):
    """Import data from CSV file to PostgreSQL table"""
    if not os.path.exists(csv_file_path):
        logger.warning(f"⚠️  CSV file not found: {csv_file_path}")
        return False
    
    try:
        # Read CSV
        df = pd.read_csv(csv_file_path)
        logger.info(f"📋 Found {len(df)} rows in {csv_file_path}")
        
        # Get PostgreSQL connection
        conn = get_postgres_connection()
        if not conn:
            return False
        
        cursor = conn.cursor()
        
        # Clear existing data
        cursor.execute(f"DELETE FROM climasys_dev.{table_name}")
        logger.info(f"🗑️  Cleared existing data from {table_name}")
        
        # Insert data row by row
        successful_inserts = 0
        failed_inserts = 0
        
        for index, row in df.iterrows():
            try:
                # Convert row to list, handling NaN values
                values = []
                for value in row:
                    if pd.isna(value):
                        values.append(None)
                    else:
                        values.append(value)
                
                # Build INSERT statement
                placeholders = ', '.join(['%s'] * len(values))
                insert_sql = f"""
                    INSERT INTO climasys_dev.{table_name} 
                    VALUES ({placeholders})
                """
                
                cursor.execute(insert_sql, values)
                successful_inserts += 1
                
            except Exception as e:
                logger.warning(f"⚠️  Failed to insert row {index + 1}: {e}")
                failed_inserts += 1
                continue
        
        conn.commit()
        cursor.close()
        conn.close()
        
        logger.info(f"✅ Imported {successful_inserts} rows to {table_name} (failed: {failed_inserts})")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to import {table_name}: {e}")
        return False

def main():
    """Main function"""
    logger.info("🚀 Starting Real SQL Server Data Import")
    logger.info("=" * 60)
    
    # List of tables to import
    tables_to_import = [
        'user_master',
        'patient_master',
        'doctor_master', 
        'clinic_master',
        'patient_visits',
        'role_master',
        'gender_master',
        'blood_group_master',
        'language_master',
        'shift_master',
        'model',
        'model_config_params'
    ]
    
    successful_imports = 0
    
    for table in tables_to_import:
        csv_file = f"real_sql_server_data/{table}.csv"
        logger.info(f"📥 Importing {table}...")
        
        if import_csv_data(table, csv_file):
            successful_imports += 1
    
    logger.info(f"🎉 Import Complete: {successful_imports}/{len(tables_to_import)} tables imported")
    
    # Verify the import
    conn = get_postgres_connection()
    if conn:
        cursor = conn.cursor()
        logger.info("\n📊 Final Data Counts:")
        for table in tables_to_import:
            cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cursor.fetchone()[0]
            logger.info(f"  - {table}: {count:,} records")
        cursor.close()
        conn.close()

if __name__ == "__main__":
    main()
