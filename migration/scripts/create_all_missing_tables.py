#!/usr/bin/env python3
"""
Create All Missing Tables and Import All Data
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

def create_missing_tables():
    """Create all missing tables based on CSV analysis"""
    logger.info("🏗️ Creating all missing tables...")
    
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Load table analysis
        with open('migration_summary.json', 'r') as f:
            data = json.load(f)
        
        tables_created = 0
        
        for table_info in data['tables']:
            table_name = table_info['file'].replace('.csv', '').lower()
            columns = table_info['columns']
            sample_data = table_info['sample_data']
            
            logger.info(f"Creating table: {table_name}")
            
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
            
            tables_created += 1
            logger.info(f"✅ Created table: {table_name}")
        
        conn.commit()
        cur.close()
        conn.close()
        
        logger.info(f"🎉 Created {tables_created} tables successfully!")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error creating tables: {e}")
        return False

def import_all_data():
    """Import all data from CSV files"""
    logger.info("📥 Importing all data from CSV files...")
    
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Load table analysis
        with open('migration_summary.json', 'r') as f:
            data = json.load(f)
        
        total_imported = 0
        tables_imported = 0
        
        for table_info in data['tables']:
            table_name = table_info['file'].replace('.csv', '').lower()
            csv_file = table_info['file']
            columns = table_info['columns']
            total_rows = table_info['total_rows']
            
            logger.info(f"Importing {table_name} ({csv_file}) - {total_rows:,} records...")
            
            try:
                # Clear existing data
                cur.execute(f"DELETE FROM climasys_dev.{table_name}")
                
                # Read CSV
                df = pd.read_csv(f'extracted_data/{csv_file}', low_memory=False)
                
                # Import data in batches
                batch_size = 1000
                imported_count = 0
                
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
                            continue
                    
                    # Commit batch
                    conn.commit()
                    logger.info(f"  Processed batch {i//batch_size + 1}: {imported_count} imported")
                
                total_imported += imported_count
                tables_imported += 1
                logger.info(f"✅ Imported {imported_count:,} records into {table_name}")
                
            except Exception as e:
                logger.error(f"❌ Error importing {table_name}: {e}")
                continue
        
        cur.close()
        conn.close()
        
        logger.info(f"🎉 Imported {total_imported:,} records across {tables_imported} tables!")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing data: {e}")
        return False

def verify_final_counts():
    """Verify final record counts"""
    logger.info("=== Final Record Counts ===")
    
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Get all tables
        cur.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'climasys_dev' 
            ORDER BY table_name;
        """)
        tables = [row[0] for row in cur.fetchall()]
        
        total_records = 0
        for table in tables:
            cur.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cur.fetchone()[0]
            total_records += count
            logger.info(f"  {table}: {count:,} records")
        
        logger.info(f"📊 Total Records: {total_records:,}")
        logger.info(f"📊 Total Tables: {len(tables)}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        logger.error(f"❌ Error verifying data: {e}")

def main():
    """Main function"""
    logger.info("🚀 Creating All Missing Tables and Importing All Data")
    logger.info("=" * 70)
    
    # Step 1: Create all missing tables
    if not create_missing_tables():
        logger.error("❌ Failed to create tables")
        return False
    
    # Step 2: Import all data
    if not import_all_data():
        logger.error("❌ Failed to import data")
        return False
    
    # Step 3: Verify final counts
    verify_final_counts()
    
    logger.info("🎉 All tables created and data imported successfully!")
    return True

if __name__ == "__main__":
    main()

