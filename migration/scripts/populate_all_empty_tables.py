#!/usr/bin/env python3
"""
Populate All Empty Tables with Sample/Reference Data
"""

import psycopg2
import logging
from datetime import datetime, date, time
import random
import string

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

def get_empty_tables(conn):
    """Get all tables with 0 records"""
    try:
        cursor = conn.cursor()
        cursor.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'climasys_dev'
            ORDER BY table_name
        """)
        
        all_tables = [row[0] for row in cursor.fetchall()]
        empty_tables = []
        
        for table in all_tables:
            cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cursor.fetchone()[0]
            if count == 0:
                empty_tables.append(table)
        
        cursor.close()
        return empty_tables
    except Exception as e:
        logger.error(f"❌ Error getting empty tables: {e}")
        return []

def get_table_columns(conn, table_name):
    """Get column information for a table"""
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

def generate_sample_data(column_name, data_type, is_nullable):
    """Generate sample data based on column name and type"""
    col_lower = column_name.lower()
    
    # Handle ID columns
    if col_lower.endswith('_id') or col_lower == 'id':
        if 'patient' in col_lower:
            return f"P{random.randint(10000, 99999)}"
        elif 'doctor' in col_lower:
            return f"DR-{random.randint(10000, 99999)}"
        elif 'clinic' in col_lower:
            return f"CL-{random.randint(10000, 99999)}"
        elif 'visit' in col_lower:
            return f"V{random.randint(10000, 99999)}"
        elif 'appointment' in col_lower:
            return f"APT-{random.randint(10000, 99999)}"
        elif 'billing' in col_lower:
            return f"BILL-{random.randint(10000, 99999)}"
        else:
            return f"{col_lower.upper()}-{random.randint(10000, 99999)}"
    
    # Handle name columns
    if 'name' in col_lower:
        if 'first' in col_lower:
            return random.choice(['John', 'Jane', 'Michael', 'Sarah', 'David', 'Lisa', 'Robert', 'Emily'])
        elif 'last' in col_lower:
            return random.choice(['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis'])
        else:
            return f"Sample {column_name.replace('_', ' ').title()}"
    
    # Handle email columns
    if 'email' in col_lower:
        return f"sample{random.randint(1, 100)}@clinic.com"
    
    # Handle phone columns
    if 'phone' in col_lower or 'mobile' in col_lower:
        return f"+91-{random.randint(9000000000, 9999999999)}"
    
    # Handle address columns
    if 'address' in col_lower:
        return f"{random.randint(1, 999)} Sample Street, City {random.randint(1, 100)}"
    
    # Handle date columns
    if 'date' in col_lower:
        if 'birth' in col_lower:
            return date(1950 + random.randint(0, 50), random.randint(1, 12), random.randint(1, 28))
        else:
            return date.today()
    
    # Handle time columns
    if 'time' in col_lower:
        return time(random.randint(8, 18), random.randint(0, 59))
    
    # Handle boolean columns
    if data_type == 'boolean':
        return random.choice([True, False])
    
    # Handle numeric columns
    if 'numeric' in data_type or 'decimal' in data_type:
        if 'fee' in col_lower or 'price' in col_lower or 'amount' in col_lower:
            return round(random.uniform(100, 5000), 2)
        else:
            return round(random.uniform(1, 100), 2)
    
    if data_type == 'integer':
        if 'count' in col_lower or 'number' in col_lower:
            return random.randint(1, 100)
        else:
            return random.randint(1, 1000)
    
    # Handle text columns
    if 'text' in data_type or 'character varying' in data_type:
        if 'description' in col_lower:
            return f"Sample description for {column_name.replace('_', ' ')}"
        elif 'comment' in col_lower:
            return f"Sample comment for {column_name.replace('_', ' ')}"
        elif 'note' in col_lower:
            return f"Sample note for {column_name.replace('_', ' ')}"
        else:
            return f"Sample {column_name.replace('_', ' ')}"
    
    # Default fallback
    return f"Sample {column_name.replace('_', ' ')}"

def populate_table(conn, table_name, num_records=10):
    """Populate a table with sample data"""
    try:
        columns = get_table_columns(conn, table_name)
        if not columns:
            logger.warning(f"⚠️  No columns found for {table_name}")
            return False
        
        cursor = conn.cursor()
        
        # Generate sample data
        for i in range(num_records):
            values = []
            column_names = []
            
            for col_name, data_type, is_nullable, default in columns:
                # Skip auto-generated columns
                if col_name.lower() == 'id' and 'serial' in str(default):
                    continue
                
                column_names.append(f'"{col_name}"')
                
                # Generate sample data
                sample_value = generate_sample_data(col_name, data_type, is_nullable)
                values.append(sample_value)
            
            # Build INSERT statement
            placeholders = ', '.join(['%s'] * len(values))
            insert_sql = f"""
                INSERT INTO climasys_dev.{table_name} ({', '.join(column_names)})
                VALUES ({placeholders})
            """
            
            try:
                cursor.execute(insert_sql, values)
            except Exception as e:
                logger.warning(f"⚠️  Error inserting record {i+1} into {table_name}: {e}")
                continue
        
        conn.commit()
        cursor.close()
        
        # Verify insertion
        cursor = conn.cursor()
        cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table_name}")
        count = cursor.fetchone()[0]
        cursor.close()
        
        logger.info(f"✅ Populated {table_name}: {count} records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error populating {table_name}: {e}")
        conn.rollback()
        return False

def main():
    """Main function"""
    logger.info("🚀 Populating All Empty Tables with Sample Data")
    logger.info("=" * 70)
    
    # Get PostgreSQL connection
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Could not connect to PostgreSQL")
        return False
    
    # Get empty tables
    empty_tables = get_empty_tables(conn)
    logger.info(f"📊 Found {len(empty_tables)} empty tables to populate")
    
    if not empty_tables:
        logger.info("🎉 No empty tables found!")
        return True
    
    # Populate each empty table
    successful_populations = 0
    failed_populations = 0
    
    for table in empty_tables:
        logger.info(f"📥 Populating {table}...")
        
        # Determine number of records based on table type
        if 'master' in table.lower():
            num_records = 5  # Master tables get fewer records
        elif 'log' in table.lower() or 'audit' in table.lower():
            num_records = 20  # Log tables get more records
        else:
            num_records = 10  # Default number of records
        
        if populate_table(conn, table, num_records):
            successful_populations += 1
        else:
            failed_populations += 1
    
    # Final summary
    logger.info("🎉 Table Population Complete!")
    logger.info("=" * 70)
    logger.info(f"📊 Summary:")
    logger.info(f"  - Tables processed: {len(empty_tables)}")
    logger.info(f"  - Successful populations: {successful_populations}")
    logger.info(f"  - Failed populations: {failed_populations}")
    
    # Get final counts
    cursor = conn.cursor()
    cursor.execute("""
        SELECT table_name 
        FROM information_schema.tables 
        WHERE table_schema = 'climasys_dev'
        ORDER BY table_name
    """)
    
    all_tables = [row[0] for row in cursor.fetchall()]
    total_records = 0
    tables_with_data = 0
    
    for table in all_tables:
        cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
        count = cursor.fetchone()[0]
        total_records += count
        if count > 0:
            tables_with_data += 1
    
    cursor.close()
    conn.close()
    
    logger.info(f"  - Total tables: {len(all_tables)}")
    logger.info(f"  - Tables with data: {tables_with_data}")
    logger.info(f"  - Total records: {total_records:,}")
    logger.info(f"  - Data coverage: {(tables_with_data/len(all_tables)*100):.1f}%")
    
    return successful_populations > 0

if __name__ == "__main__":
    main()
