#!/usr/bin/env python3
"""
Update PostgreSQL Schema to Match CSV Files
"""

import os
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

def analyze_csv_structure(csv_file):
    """Analyze CSV file structure"""
    try:
        df = pd.read_csv(csv_file, low_memory=False)
        return {
            'columns': list(df.columns),
            'row_count': len(df),
            'sample_data': df.head(1).to_dict('records')[0] if len(df) > 0 else {}
        }
    except Exception as e:
        logger.error(f"❌ Error analyzing {csv_file}: {e}")
        return None

def get_postgresql_data_type(value, column_name):
    """Determine PostgreSQL data type based on sample data"""
    if pd.isna(value) or value == '' or value == 'nan':
        return 'TEXT'
    
    # Convert to string for analysis
    str_value = str(value).strip()
    
    # Check for common patterns
    if column_name.lower().endswith('_id') or column_name.lower().endswith('id'):
        if str_value.isdigit():
            return 'INTEGER'
        else:
            return 'VARCHAR(50)'
    
    if column_name.lower().endswith('_date') or column_name.lower().endswith('_time'):
        return 'TIMESTAMP'
    
    if column_name.lower().endswith('_flag') or column_name.lower().endswith('_indicator'):
        return 'BOOLEAN'
    
    if column_name.lower().endswith('_amount') or column_name.lower().endswith('_price') or column_name.lower().endswith('_fees'):
        return 'DECIMAL(10,2)'
    
    # Try to determine type from content
    try:
        # Try integer
        int(str_value)
        return 'INTEGER'
    except:
        try:
            # Try float
            float(str_value)
            return 'DECIMAL(10,2)'
        except:
            # Check for date patterns
            if any(pattern in str_value.lower() for pattern in ['2024', '2023', '2022', '2021', '2020']):
                return 'TIMESTAMP'
            
            # Check length for VARCHAR
            if len(str_value) > 255:
                return 'TEXT'
            elif len(str_value) > 100:
                return 'VARCHAR(255)'
            elif len(str_value) > 50:
                return 'VARCHAR(100)'
            else:
                return 'VARCHAR(50)'

def create_table_sql(table_name, csv_structure):
    """Generate CREATE TABLE SQL based on CSV structure"""
    columns = csv_structure['columns']
    sample_data = csv_structure['sample_data']
    
    # Start with basic columns
    sql_parts = []
    
    # Add ID column if not present
    if 'id' not in [col.lower() for col in columns]:
        sql_parts.append('id SERIAL PRIMARY KEY')
    
    # Add other columns
    for col in columns:
        col_lower = col.lower()
        if col_lower == 'id':
            continue  # Skip if already added
        
        # Get data type
        sample_value = sample_data.get(col, '')
        data_type = get_postgresql_data_type(sample_value, col)
        
        # Add column
        sql_parts.append(f'"{col}" {data_type}')
    
    # Add audit columns
    if 'created_on' not in [col.lower() for col in columns]:
        sql_parts.append('created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP')
    if 'modified_on' not in [col.lower() for col in columns]:
        sql_parts.append('modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP')
    if 'is_active' not in [col.lower() for col in columns]:
        sql_parts.append('is_active BOOLEAN DEFAULT TRUE')
    
    # Create the SQL
    columns_sql = ',\n    '.join(sql_parts)
    sql = f"""
CREATE TABLE IF NOT EXISTS climasys_dev.{table_name} (
    {columns_sql}
);
"""
    return sql

def update_table_schema(conn, table_name, csv_structure):
    """Update table schema to match CSV structure"""
    try:
        cursor = conn.cursor()
        
        # Get existing columns
        cursor.execute("""
            SELECT column_name 
            FROM information_schema.columns
            WHERE table_schema = 'climasys_dev' AND table_name = %s
        """, (table_name,))
        
        existing_columns = [row[0].lower() for row in cursor.fetchall()]
        
        # Add missing columns
        csv_columns = csv_structure['columns']
        sample_data = csv_structure['sample_data']
        
        for col in csv_columns:
            col_lower = col.lower()
            if col_lower not in existing_columns:
                # Get data type
                sample_value = sample_data.get(col, '')
                data_type = get_postgresql_data_type(sample_value, col)
                
                # Add column
                alter_sql = f'ALTER TABLE climasys_dev.{table_name} ADD COLUMN "{col}" {data_type}'
                logger.info(f"  Adding column: {col} {data_type}")
                cursor.execute(alter_sql)
        
        conn.commit()
        cursor.close()
        logger.info(f"✅ Updated schema for {table_name}")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error updating schema for {table_name}: {e}")
        conn.rollback()
        return False

def main():
    """Main function"""
    logger.info("🔧 Updating PostgreSQL Schema to Match CSV Files")
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
    
    update_results = {}
    
    for csv_file, table_name in csv_mappings:
        csv_path = f'extracted_data/{csv_file}'
        
        if not os.path.exists(csv_path):
            logger.warning(f"⚠️  CSV file not found: {csv_path}")
            continue
        
        logger.info(f"🔧 Updating schema for {table_name} based on {csv_file}...")
        
        # Analyze CSV structure
        csv_structure = analyze_csv_structure(csv_path)
        if not csv_structure:
            continue
        
        logger.info(f"  📊 CSV has {len(csv_structure['columns'])} columns, {csv_structure['row_count']} rows")
        
        # Update table schema
        if update_table_schema(conn, table_name, csv_structure):
            update_results[table_name] = {
                'status': 'success',
                'columns_added': len(csv_structure['columns']),
                'row_count': csv_structure['row_count']
            }
        else:
            update_results[table_name] = {
                'status': 'failed',
                'columns_added': 0,
                'row_count': csv_structure['row_count']
            }
    
    # Save update results
    results_file = f'schema_update_results_{datetime.now().strftime("%Y%m%d_%H%M%S")}.json'
    with open(results_file, 'w') as f:
        json.dump(update_results, f, indent=2)
    
    # Final summary
    logger.info("🎉 Schema Update Complete!")
    logger.info("=" * 70)
    logger.info("📊 Update Results:")
    
    successful_updates = 0
    failed_updates = 0
    total_columns_added = 0
    
    for table_name, result in update_results.items():
        if result['status'] == 'success':
            logger.info(f"✅ {table_name}: {result['columns_added']} columns, {result['row_count']} rows")
            successful_updates += 1
            total_columns_added += result['columns_added']
        else:
            logger.error(f"❌ {table_name}: Update failed")
            failed_updates += 1
    
    logger.info("")
    logger.info(f"📈 Summary:")
    logger.info(f"  - Successful updates: {successful_updates}")
    logger.info(f"  - Failed updates: {failed_updates}")
    logger.info(f"  - Total columns added: {total_columns_added}")
    logger.info(f"  - Results saved to: {results_file}")
    
    conn.close()
    return successful_updates > 0

if __name__ == "__main__":
    main()
