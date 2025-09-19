#!/usr/bin/env python3
"""
Extract Remaining Tables from SQL Server
"""

import pyodbc
import pandas as pd
import os
from datetime import datetime
import logging

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_sqlserver_connection():
    """Get SQL Server connection"""
    try:
        # Try different connection strings
        connection_strings = [
            # SQL Server with Windows Authentication
            "DRIVER={SQL Server};SERVER=localhost;DATABASE=Climasys-00010;Trusted_Connection=yes;",
            "DRIVER={SQL Server};SERVER=.;DATABASE=Climasys-00010;Trusted_Connection=yes;",
            "DRIVER={SQL Server};SERVER=127.0.0.1;DATABASE=Climasys-00010;Trusted_Connection=yes;",
            # SQL Server Native Client
            "DRIVER={SQL Server Native Client 11.0};SERVER=localhost;DATABASE=Climasys-00010;Trusted_Connection=yes;",
            "DRIVER={SQL Server Native Client 11.0};SERVER=.;DATABASE=Climasys-00010;Trusted_Connection=yes;",
        ]
        
        for conn_str in connection_strings:
            try:
                logger.info(f"Trying connection: {conn_str[:60]}...")
                conn = pyodbc.connect(conn_str)
                logger.info("✅ Connected to SQL Server successfully!")
                return conn
            except Exception as e:
                logger.warning(f"Connection failed: {e}")
                continue
        
        raise Exception("Could not connect to SQL Server")
        
    except Exception as e:
        logger.error(f"❌ Error connecting to SQL Server: {e}")
        return None

def discover_all_tables():
    """Discover all tables in SQL Server database"""
    logger.info("🔍 Discovering all tables in SQL Server...")
    
    conn = get_sqlserver_connection()
    if not conn:
        return []
    
    try:
        cursor = conn.cursor()
        
        # Get all tables from the database
        cursor.execute("""
            SELECT TABLE_NAME 
            FROM INFORMATION_SCHEMA.TABLES 
            WHERE TABLE_TYPE = 'BASE TABLE'
            ORDER BY TABLE_NAME;
        """)
        
        tables = [row[0] for row in cursor.fetchall()]
        
        cursor.close()
        conn.close()
        
        logger.info(f"✅ Found {len(tables)} tables in SQL Server")
        return tables
        
    except Exception as e:
        logger.error(f"❌ Error discovering tables: {e}")
        if conn:
            conn.close()
        return []

def extract_table_data(table_name, limit=1000):
    """Extract data from a table"""
    conn = get_sqlserver_connection()
    if not conn:
        return None
    
    try:
        # Build query with limit
        query = f"SELECT TOP {limit} * FROM [dbo].[{table_name}]"
        
        logger.info(f"Extracting data from {table_name}...")
        df = pd.read_sql(query, conn)
        
        conn.close()
        
        logger.info(f"✅ Extracted {len(df)} records from {table_name}")
        return df
        
    except Exception as e:
        logger.error(f"❌ Error extracting data from {table_name}: {e}")
        if conn:
            conn.close()
        return None

def get_table_record_count(table_name):
    """Get total record count for a table"""
    conn = get_sqlserver_connection()
    if not conn:
        return 0
    
    try:
        cursor = conn.cursor()
        cursor.execute(f"SELECT COUNT(*) FROM [dbo].[{table_name}]")
        count = cursor.fetchone()[0]
        cursor.close()
        conn.close()
        return count
    except Exception as e:
        logger.error(f"❌ Error getting count for {table_name}: {e}")
        if conn:
            conn.close()
        return 0

def main():
    """Main function"""
    logger.info("🚀 Discovering and Extracting All Tables from SQL Server")
    logger.info("=" * 70)
    
    # Step 1: Discover all tables
    all_tables = discover_all_tables()
    if not all_tables:
        logger.error("❌ Could not discover tables from SQL Server")
        logger.info("💡 Please check:")
        logger.info("  1. SQL Server is running")
        logger.info("  2. Database 'Climasys-00010' exists")
        logger.info("  3. You have access permissions")
        return False
    
    logger.info(f"📊 Found {len(all_tables)} tables in SQL Server")
    
    # Create extracted_data directory if it doesn't exist
    os.makedirs('extracted_data', exist_ok=True)
    
    # Step 2: Extract all tables to CSV
    logger.info("📥 Extracting all tables to CSV files...")
    
    extracted_count = 0
    total_records = 0
    
    for table_name in all_tables:
        try:
            # Get record count
            record_count = get_table_record_count(table_name)
            
            # Extract sample data
            df = extract_table_data(table_name, limit=1000)
            if df is not None and len(df) > 0:
                # Save to CSV
                csv_file = f'extracted_data/{table_name}.csv'
                df.to_csv(csv_file, index=False)
                
                logger.info(f"✅ Extracted {table_name}: {len(df)} sample records (Total: {record_count:,})")
                total_records += record_count
                extracted_count += 1
            else:
                logger.warning(f"⚠️  No data found in {table_name}")
                
        except Exception as e:
            logger.error(f"❌ Error extracting {table_name}: {e}")
            continue
    
    # Step 3: Create extraction queries for full data
    logger.info("📝 Creating extraction queries for full data...")
    
    queries_content = f"""-- =====================================================
-- Complete SQL Server Data Extraction Queries
-- Generated by extract_remaining_tables.py
-- Date: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
-- Total Tables: {len(all_tables)}
-- =====================================================

-- Instructions:
-- 1. Run these queries on your SQL Server instance
-- 2. Export results to CSV files in the extracted_data/ directory
-- 3. Use the import script to load data into PostgreSQL

"""
    
    for table_name in all_tables:
        queries_content += f"""
-- Table: {table_name}
-- Export to: extracted_data/{table_name}.csv
SELECT *
FROM [dbo].[{table_name}]
ORDER BY 1;
"""
    
    # Save queries to file
    with open('complete_sqlserver_extraction.sql', 'w', encoding='utf-8') as f:
        f.write(queries_content)
    
    # Step 4: Create table list
    table_list_content = f"""# All Tables Found in SQL Server

## Total Tables: {len(all_tables)}

### Table List:
"""
    
    for table_name in all_tables:
        record_count = get_table_record_count(table_name)
        table_list_content += f"- {table_name} ({record_count:,} records)\n"
    
    with open('ALL_SQLSERVER_TABLES.md', 'w', encoding='utf-8') as f:
        f.write(table_list_content)
    
    # Final summary
    logger.info("🎉 SQL Server Table Discovery and Extraction Complete!")
    logger.info("=" * 70)
    logger.info("📁 Generated Files:")
    logger.info("  - complete_sqlserver_extraction.sql")
    logger.info("  - ALL_SQLSERVER_TABLES.md")
    logger.info("  - extracted_data/*.csv (CSV files)")
    logger.info("")
    logger.info("📊 Summary:")
    logger.info(f"  - Total Tables Found: {len(all_tables)}")
    logger.info(f"  - Tables Extracted: {extracted_count}")
    logger.info(f"  - Total Records: {total_records:,}")
    logger.info("")
    logger.info("🎯 Next Steps:")
    logger.info("1. Review the extracted CSV files")
    logger.info("2. Run the complete extraction queries for full data")
    logger.info("3. Import all data into PostgreSQL")
    logger.info("4. Add foreign key constraints")
    
    return True

if __name__ == "__main__":
    main()

