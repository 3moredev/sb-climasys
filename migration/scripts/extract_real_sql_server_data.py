#!/usr/bin/env python3
"""
Extract Real SQL Server Data
This script provides the tools to extract actual data from SQL Server tables.
"""

import pyodbc
import pandas as pd
import logging
import os
from datetime import datetime

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_sql_server_connection_string():
    """Get SQL Server connection string - UPDATE THESE VALUES"""
    # TODO: Update these connection details to match your SQL Server
    DB_CONFIG = {
        'driver': '{SQL Server}',  # Or '{ODBC Driver 17 for SQL Server}'
        'server': 'localhost',     # Your SQL Server hostname/IP
        'database': 'Climasys',    # Your SQL Server database name
        'uid': 'sa',              # Your SQL Server username
        'pwd': 'root'             # Your SQL Server password
    }
    
    return (
        f"DRIVER={DB_CONFIG['driver']};"
        f"SERVER={DB_CONFIG['server']};"
        f"DATABASE={DB_CONFIG['database']};"
        f"UID={DB_CONFIG['uid']};"
        f"PWD={DB_CONFIG['pwd']}"
    )

def test_sql_server_connection():
    """Test SQL Server connection"""
    try:
        conn_str = get_sql_server_connection_string()
        conn = pyodbc.connect(conn_str)
        cursor = conn.cursor()
        cursor.execute("SELECT @@VERSION")
        version = cursor.fetchone()[0]
        logger.info(f"✅ Connected to SQL Server: {version[:50]}...")
        cursor.close()
        conn.close()
        return True
    except Exception as e:
        logger.error(f"❌ SQL Server connection failed: {e}")
        logger.info("📋 Please update the connection details in the script:")
        logger.info("   - server: Your SQL Server hostname/IP")
        logger.info("   - database: Your SQL Server database name")
        logger.info("   - uid: Your SQL Server username")
        logger.info("   - pwd: Your SQL Server password")
        return False

def extract_table_data(table_name, output_dir="real_sql_server_data"):
    """Extract data from a specific SQL Server table"""
    os.makedirs(output_dir, exist_ok=True)
    csv_file_path = os.path.join(output_dir, f"{table_name}.csv")
    
    try:
        conn_str = get_sql_server_connection_string()
        conn = pyodbc.connect(conn_str)
        
        # Get table structure first
        cursor = conn.cursor()
        cursor.execute(f"""
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, CHARACTER_MAXIMUM_LENGTH
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = '{table_name}' AND TABLE_SCHEMA = 'dbo'
            ORDER BY ORDINAL_POSITION
        """)
        
        columns_info = cursor.fetchall()
        logger.info(f"📋 Table {table_name} structure:")
        for col in columns_info:
            logger.info(f"  - {col[0]}: {col[1]} (nullable: {col[2]})")
        
        # Extract data
        query = f"SELECT * FROM dbo.{table_name}"
        df = pd.read_sql(query, conn)
        
        # Save to CSV
        df.to_csv(csv_file_path, index=False, encoding='utf-8-sig')
        
        logger.info(f"✅ Extracted {len(df)} rows from {table_name} to {csv_file_path}")
        
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to extract {table_name}: {e}")
        return False

def extract_critical_tables():
    """Extract data from critical tables"""
    critical_tables = [
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
    
    logger.info("🚀 Extracting Critical Tables from SQL Server")
    logger.info("=" * 60)
    
    if not test_sql_server_connection():
        return False
    
    successful_extractions = 0
    for table in critical_tables:
        logger.info(f"📥 Extracting {table}...")
        if extract_table_data(table):
            successful_extractions += 1
    
    logger.info(f"🎉 Extraction Complete: {successful_extractions}/{len(critical_tables)} tables extracted")
    return successful_extractions > 0

def generate_import_script():
    """Generate Python script to import the extracted data"""
    script_content = '''#!/usr/bin/env python3
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
        logger.info("\\n📊 Final Data Counts:")
        for table in tables_to_import:
            cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cursor.fetchone()[0]
            logger.info(f"  - {table}: {count:,} records")
        cursor.close()
        conn.close()

if __name__ == "__main__":
    main()
'''
    
    with open('import_real_sql_server_data.py', 'w', encoding='utf-8') as f:
        f.write(script_content)
    
    logger.info("✅ Generated import_real_sql_server_data.py script")

def main():
    """Main function"""
    logger.info("🚀 SQL Server Data Extraction Tool")
    logger.info("=" * 60)
    logger.info("This tool will help you extract real data from SQL Server")
    logger.info("")
    
    # Check if we can connect to SQL Server
    if test_sql_server_connection():
        logger.info("✅ SQL Server connection successful!")
        
        # Extract critical tables
        if extract_critical_tables():
            # Generate import script
            generate_import_script()
            logger.info("")
            logger.info("🎉 Data extraction complete!")
            logger.info("📋 Next steps:")
            logger.info("1. Review the extracted CSV files in 'real_sql_server_data/' folder")
            logger.info("2. Run: python import_real_sql_server_data.py")
            logger.info("3. Verify the data import in PostgreSQL")
        else:
            logger.error("❌ Data extraction failed")
    else:
        logger.info("")
        logger.info("📋 Manual Steps to Extract Data:")
        logger.info("1. Update the connection details in this script")
        logger.info("2. Run the script again")
        logger.info("3. Or manually export data from SQL Server Management Studio")
        logger.info("")
        logger.info("📋 SQL Queries to Export Data:")
        logger.info("Run these queries in SQL Server Management Studio and save as CSV:")
        
        critical_tables = [
            'user_master', 'patient_master', 'doctor_master', 'clinic_master',
            'patient_visits', 'role_master', 'gender_master', 'blood_group_master',
            'language_master', 'shift_master', 'model', 'model_config_params'
        ]
        
        for table in critical_tables:
            logger.info(f"   SELECT * FROM dbo.{table};")

if __name__ == "__main__":
    main()
