#!/usr/bin/env python3
"""
Import All 150 Tables from CSV Files to PostgreSQL
"""

import psycopg2
import pandas as pd
import os
from datetime import datetime
import logging
import json

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_postgresql_connection():
    """Get PostgreSQL connection"""
    return psycopg2.connect(
        host='localhost',
        port='5432',
        database='climasys_dev',
        user='postgres',
        password='root'
    )

def create_table_if_not_exists(table_name, columns, sample_data):
    """Create table if it doesn't exist"""
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Generate column definitions
        column_definitions = []
        for col in columns:
            col_lower = col.lower()
            
            # Infer data type from sample data
            sample_value = sample_data.get(col)
            if pd.isna(sample_value) or sample_value == '':
                col_type = 'VARCHAR(255)'
            elif isinstance(sample_value, (int, float)):
                if isinstance(sample_value, float):
                    col_type = 'NUMERIC'
                else:
                    col_type = 'INTEGER'
            elif isinstance(sample_value, str):
                if len(str(sample_value)) > 255:
                    col_type = 'TEXT'
                else:
                    col_type = 'VARCHAR(255)'
            else:
                col_type = 'VARCHAR(255)'
            
            # Add primary key for ID columns
            if col_lower in ['id', 'doctor_id', 'clinic_id', 'patient_id', 'visit_id', 'user_id', 'role_id', 'shift_id', 'model_id']:
                if col_lower == 'id':
                    column_definitions.append(f"    {col_lower} SERIAL PRIMARY KEY")
                else:
                    column_definitions.append(f"    {col_lower} {col_type} NOT NULL")
            else:
                column_definitions.append(f"    {col_lower} {col_type}")
        
        # Create table
        create_table_sql = f"""
        CREATE TABLE IF NOT EXISTS climasys_dev.{table_name} (
{','.join(column_definitions)}
        );
        """
        
        cur.execute(create_table_sql)
        
        # Add indexes for common columns
        if 'doctor_id' in [col.lower() for col in columns]:
            cur.execute(f"CREATE INDEX IF NOT EXISTS idx_{table_name}_doctor_id ON climasys_dev.{table_name}(doctor_id);")
        if 'clinic_id' in [col.lower() for col in columns]:
            cur.execute(f"CREATE INDEX IF NOT EXISTS idx_{table_name}_clinic_id ON climasys_dev.{table_name}(clinic_id);")
        if 'patient_id' in [col.lower() for col in columns]:
            cur.execute(f"CREATE INDEX IF NOT EXISTS idx_{table_name}_patient_id ON climasys_dev.{table_name}(patient_id);")
        
        conn.commit()
        cur.close()
        conn.close()
        
        logger.info(f"✅ Created/verified table: {table_name}")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error creating table {table_name}: {e}")
        return False

def import_table_data(table_name, csv_file):
    """Import data from CSV file to table"""
    try:
        # Check if CSV file exists
        if not os.path.exists(f'extracted_data/{csv_file}'):
            logger.warning(f"⚠️  CSV file not found: {csv_file}")
            return False
        
        # Read CSV to get structure
        df = pd.read_csv(f'extracted_data/{csv_file}', low_memory=False, nrows=5)
        columns = df.columns.tolist()
        sample_data = df.iloc[0].to_dict() if len(df) > 0 else {}
        
        # Create table if not exists
        if not create_table_if_not_exists(table_name, columns, sample_data):
            return False
        
        # Read full CSV
        df = pd.read_csv(f'extracted_data/{csv_file}', low_memory=False)
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Clear existing data
        cur.execute(f"DELETE FROM climasys_dev.{table_name}")
        
        # Import data in batches
        batch_size = 1000
        imported_count = 0
        error_count = 0
        
        for i in range(0, len(df), batch_size):
            batch = df.iloc[i:i+batch_size]
            
            for _, row in batch.iterrows():
                try:
                    # Prepare values
                    values = []
                    for col in columns:
                        value = row.get(col, None)
                        if pd.isna(value):
                            values.append(None)
                        else:
                            values.append(value)
                    
                    # Generate INSERT statement
                    placeholders = ', '.join(['%s'] * len(columns))
                    column_names = ', '.join([col.lower() for col in columns])
                    
                    cur.execute(f"""
                        INSERT INTO climasys_dev.{table_name} ({column_names})
                        VALUES ({placeholders})
                    """, values)
                    imported_count += 1
                    
                except Exception as e:
                    logger.warning(f"Skipping record in {table_name}: {e}")
                    error_count += 1
                    continue
            
            # Commit batch
            conn.commit()
            logger.info(f"  Processed batch {i//batch_size + 1}: {imported_count} imported, {error_count} errors")
        
        cur.close()
        conn.close()
        
        logger.info(f"✅ Imported {imported_count:,} records into {table_name} ({error_count} errors)")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing {table_name}: {e}")
        return False

def main():
    """Main function"""
    logger.info("🚀 Importing All 150 Tables from CSV Files")
    logger.info("=" * 60)
    
    # Get all CSV files
    csv_files = []
    if os.path.exists('extracted_data'):
        for file in os.listdir('extracted_data'):
            if file.endswith('.csv'):
                csv_files.append(file)
    
    logger.info(f"Found {len(csv_files)} CSV files to import")
    
    success_count = 0
    total_records = 0
    
    for csv_file in sorted(csv_files):
        table_name = csv_file.replace('.csv', '').lower()
        logger.info(f"🔄 Importing {table_name} ({csv_file})...")
        
        try:
            success = import_table_data(table_name, csv_file)
            if success:
                success_count += 1
                # Count records
                try:
                    df = pd.read_csv(f'extracted_data/{csv_file}', low_memory=False)
                    total_records += len(df)
                except:
                    pass
            else:
                logger.error(f"❌ Failed to import {table_name}")
        except Exception as e:
            logger.error(f"❌ Error importing {table_name}: {e}")
    
    logger.info(f"📊 Import Summary: {success_count}/{len(csv_files)} tables imported successfully")
    logger.info(f"📊 Total Records Imported: {total_records:,}")
    
    if success_count == len(csv_files):
        logger.info("🎉 All tables imported successfully!")
        return True
    else:
        logger.warning("⚠️  Some imports failed. Check the error messages above.")
        return False

if __name__ == "__main__":
    main()
