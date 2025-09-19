#!/usr/bin/env python3
"""
Analyze Schema Mismatches Between CSV Files and PostgreSQL Tables
"""

import os
import psycopg2
import pandas as pd
import logging
from datetime import datetime

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

def get_table_columns(conn, table_name):
    """Get column names and types for a table"""
    try:
        cursor = conn.cursor()
        cursor.execute("""
            SELECT column_name, data_type, is_nullable, column_default
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

def analyze_csv_structure(csv_file):
    """Analyze CSV file structure"""
    try:
        df = pd.read_csv(csv_file)
        return {
            'columns': list(df.columns),
            'row_count': len(df),
            'sample_data': df.head(2).to_dict('records') if len(df) > 0 else []
        }
    except Exception as e:
        logger.error(f"❌ Error analyzing {csv_file}: {e}")
        return None

def main():
    """Main function"""
    logger.info("🔍 Analyzing Schema Mismatches Between CSV Files and PostgreSQL Tables")
    logger.info("=" * 80)
    
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
    
    analysis_results = {}
    
    for csv_file, table_name in csv_mappings:
        csv_path = f'extracted_data/{csv_file}'
        
        if not os.path.exists(csv_path):
            logger.warning(f"⚠️  CSV file not found: {csv_path}")
            continue
        
        logger.info(f"📊 Analyzing {csv_file} vs {table_name}...")
        
        # Get CSV structure
        csv_structure = analyze_csv_structure(csv_path)
        if not csv_structure:
            continue
        
        # Get PostgreSQL table structure
        pg_columns = get_table_columns(conn, table_name)
        
        # Compare structures
        csv_cols = set([col.lower() for col in csv_structure['columns']])
        pg_cols = set([col[0].lower() for col in pg_columns])
        
        # Find mismatches
        csv_only = csv_cols - pg_cols
        pg_only = pg_cols - csv_cols
        common = csv_cols & pg_cols
        
        analysis_results[table_name] = {
            'csv_file': csv_file,
            'csv_columns': csv_structure['columns'],
            'csv_row_count': csv_structure['row_count'],
            'pg_columns': [col[0] for col in pg_columns],
            'csv_only_columns': list(csv_only),
            'pg_only_columns': list(pg_only),
            'common_columns': list(common),
            'mismatch_count': len(csv_only) + len(pg_only)
        }
        
        logger.info(f"  📈 CSV: {len(csv_structure['columns'])} columns, {csv_structure['row_count']} rows")
        logger.info(f"  📈 PG:  {len(pg_columns)} columns")
        logger.info(f"  🔄 Common: {len(common)} columns")
        logger.info(f"  ❌ CSV only: {len(csv_only)} columns")
        logger.info(f"  ❌ PG only: {len(pg_only)} columns")
        
        if csv_only:
            logger.info(f"    CSV-only columns: {list(csv_only)[:5]}{'...' if len(csv_only) > 5 else ''}")
        if pg_only:
            logger.info(f"    PG-only columns: {list(pg_only)[:5]}{'...' if len(pg_only) > 5 else ''}")
        
        logger.info("")
    
    # Save analysis results
    results_file = f'schema_analysis_{datetime.now().strftime("%Y%m%d_%H%M%S")}.json'
    import json
    with open(results_file, 'w') as f:
        json.dump(analysis_results, f, indent=2, default=str)
    
    # Summary
    logger.info("🎯 Schema Analysis Summary")
    logger.info("=" * 80)
    
    total_mismatches = 0
    tables_with_data = 0
    
    for table_name, result in analysis_results.items():
        if result['csv_row_count'] > 0:
            tables_with_data += 1
        total_mismatches += result['mismatch_count']
        
        if result['mismatch_count'] > 0:
            logger.info(f"❌ {table_name}: {result['mismatch_count']} mismatches")
        else:
            logger.info(f"✅ {table_name}: No mismatches")
    
    logger.info("")
    logger.info(f"📊 Overall Summary:")
    logger.info(f"  - Tables analyzed: {len(analysis_results)}")
    logger.info(f"  - Tables with data: {tables_with_data}")
    logger.info(f"  - Total mismatches: {total_mismatches}")
    logger.info(f"  - Analysis saved to: {results_file}")
    
    conn.close()
    return True

if __name__ == "__main__":
    main()
