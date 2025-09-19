#!/usr/bin/env python3
"""
Import All Available CSV Data into PostgreSQL
"""

import os
import sys
import psycopg2
import pandas as pd
import logging
from datetime import datetime
import json

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

def clear_table_data(conn, table_name):
    """Clear existing data from a table"""
    try:
        cursor = conn.cursor()
        cursor.execute(f"DELETE FROM climasys_dev.{table_name}")
        conn.commit()
        cursor.close()
        logger.info(f"✅ Cleared existing data from {table_name}")
        return True
    except Exception as e:
        logger.error(f"❌ Error clearing {table_name}: {e}")
        conn.rollback()
        return False

def get_table_columns(conn, table_name):
    """Get column names and types for a table"""
    try:
        cursor = conn.cursor()
        cursor.execute("""
            SELECT column_name, data_type, is_nullable
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

def import_csv_data(conn, csv_file, table_name):
    """Import data from CSV file to PostgreSQL table"""
    try:
        # Read CSV file
        df = pd.read_csv(csv_file)
        logger.info(f"📊 Loaded {len(df)} records from {csv_file}")
        
        if len(df) == 0:
            logger.warning(f"⚠️  No data in {csv_file}")
            return True
        
        # Get table columns
        table_columns = get_table_columns(conn, table_name)
        if not table_columns:
            logger.error(f"❌ Could not get columns for {table_name}")
            return False
        
        # Create column mapping
        column_mapping = {}
        for col_name, data_type, is_nullable in table_columns:
            column_mapping[col_name.lower()] = col_name
        
        # Map CSV columns to table columns
        mapped_df = df.copy()
        for csv_col in df.columns:
            csv_col_lower = csv_col.lower()
            if csv_col_lower in column_mapping:
                table_col = column_mapping[csv_col_lower]
                if csv_col != table_col:
                    mapped_df[table_col] = mapped_df[csv_col]
                    mapped_df.drop(csv_col, axis=1, inplace=True)
            else:
                logger.warning(f"⚠️  Column {csv_col} not found in table {table_name}")
        
        # Handle data quality issues
        for col in mapped_df.columns:
            # Replace empty strings with None
            mapped_df[col] = mapped_df[col].replace('', None)
            # Replace 'nan' strings with None
            mapped_df[col] = mapped_df[col].replace('nan', None)
        
        # Insert data
        cursor = conn.cursor()
        
        # Build INSERT statement
        columns = list(mapped_df.columns)
        placeholders = ', '.join(['%s'] * len(columns))
        insert_sql = f"""
            INSERT INTO climasys_dev.{table_name} ({', '.join(columns)})
            VALUES ({placeholders})
            ON CONFLICT DO NOTHING
        """
        
        # Insert data in batches
        batch_size = 1000
        total_inserted = 0
        
        for i in range(0, len(mapped_df), batch_size):
            batch = mapped_df.iloc[i:i+batch_size]
            
            # Convert to list of tuples
            data_tuples = [tuple(row) for row in batch.values]
            
            try:
                cursor.executemany(insert_sql, data_tuples)
                conn.commit()
                total_inserted += len(batch)
                logger.info(f"✅ Inserted batch {i//batch_size + 1}: {len(batch)} records")
            except Exception as e:
                logger.error(f"❌ Error inserting batch {i//batch_size + 1}: {e}")
                conn.rollback()
                continue
        
        cursor.close()
        logger.info(f"✅ Successfully imported {total_inserted} records into {table_name}")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing {csv_file} to {table_name}: {e}")
        return False

def get_record_count(conn, table_name):
    """Get record count for a table"""
    try:
        cursor = conn.cursor()
        cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table_name}")
        count = cursor.fetchone()[0]
        cursor.close()
        return count
    except Exception as e:
        logger.error(f"❌ Error getting count for {table_name}: {e}")
        return 0

def main():
    """Main function"""
    logger.info("🚀 Importing All Available CSV Data into PostgreSQL")
    logger.info("=" * 70)
    
    # Get PostgreSQL connection
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Could not connect to PostgreSQL")
        return False
    
    # Define CSV files and their corresponding table names
    csv_mappings = [
        ('Clinic_Master.csv', 'clinic_master'),
        ('Doctor_Master.csv', 'doctor_master'),
        ('Patient_Master.csv', 'patient_master'),
        ('Patient_Visits.csv', 'patient_visits'),
        ('User_Master.csv', 'user_master'),
        ('System_Params.csv', 'system_params'),
        ('Language_Master.csv', 'language_master'),
        ('Role_Master.csv', 'role_master'),
        ('Gender_Master.csv', 'gender_master'),
        ('User_Role.csv', 'user_role'),
        ('Shift_Master.csv', 'shift_master'),
        ('Model.csv', 'model'),
        ('Model_Config_Params.csv', 'model_config_params'),
        ('License_Key.csv', 'license_key'),
        ('Doctor_Clinic_Shift.csv', 'doctor_clinic_shift'),
        ('Doctor_Model.csv', 'doctor_model'),
        ('Visit_Prescription_Overwrite.csv', 'visit_prescription_overwrite'),
    ]
    
    # Import each CSV file
    import_results = {}
    total_imported = 0
    
    for csv_file, table_name in csv_mappings:
        csv_path = f'extracted_data/{csv_file}'
        
        if not os.path.exists(csv_path):
            logger.warning(f"⚠️  CSV file not found: {csv_path}")
            import_results[table_name] = {'status': 'skipped', 'reason': 'file_not_found'}
            continue
        
        logger.info(f"📥 Importing {csv_file} to {table_name}...")
        
        # Clear existing data
        if not clear_table_data(conn, table_name):
            import_results[table_name] = {'status': 'failed', 'reason': 'clear_failed'}
            continue
        
        # Import data
        if import_csv_data(conn, csv_path, table_name):
            record_count = get_record_count(conn, table_name)
            import_results[table_name] = {'status': 'success', 'records': record_count}
            total_imported += record_count
            logger.info(f"✅ {table_name}: {record_count:,} records")
        else:
            import_results[table_name] = {'status': 'failed', 'reason': 'import_failed'}
    
    # Save import results
    results_file = f'import_results_{datetime.now().strftime("%Y%m%d_%H%M%S")}.json'
    with open(results_file, 'w') as f:
        json.dump(import_results, f, indent=2)
    
    # Final summary
    logger.info("🎉 CSV Data Import Complete!")
    logger.info("=" * 70)
    logger.info("📊 Import Results:")
    
    successful_imports = 0
    failed_imports = 0
    
    for table_name, result in import_results.items():
        if result['status'] == 'success':
            logger.info(f"✅ {table_name}: {result['records']:,} records")
            successful_imports += 1
        elif result['status'] == 'failed':
            logger.error(f"❌ {table_name}: {result['reason']}")
            failed_imports += 1
        else:
            logger.warning(f"⚠️  {table_name}: {result['reason']}")
    
    logger.info("")
    logger.info(f"📈 Summary:")
    logger.info(f"  - Successful imports: {successful_imports}")
    logger.info(f"  - Failed imports: {failed_imports}")
    logger.info(f"  - Total records imported: {total_imported:,}")
    logger.info(f"  - Results saved to: {results_file}")
    
    conn.close()
    return successful_imports > 0

if __name__ == "__main__":
    main()
