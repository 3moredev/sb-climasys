#!/usr/bin/env python3
"""
Discover all tables in SQL Server and create comprehensive migration plan
"""

import pyodbc
import psycopg2
import pandas as pd
import json
from datetime import datetime

def get_sqlserver_connection():
    """Get SQL Server connection - you'll need to update these parameters"""
    # Update these connection parameters for your SQL Server
    connection_string = (
        "DRIVER={ODBC Driver 17 for SQL Server};"
        "SERVER=your_sql_server;"
        "DATABASE=your_database;"
        "UID=your_username;"
        "PWD=your_password;"
        "Trusted_Connection=no;"
    )
    return pyodbc.connect(connection_string)

def get_postgresql_connection():
    """Get PostgreSQL connection"""
    return psycopg2.connect(
        host='localhost',
        port='5432',
        database='climasys_dev',
        user='postgres',
        password='root'
    )

def discover_sqlserver_tables():
    """Discover all tables in SQL Server database"""
    print("🔍 Discovering all tables in SQL Server...")
    
    try:
        conn = get_sqlserver_connection()
        cursor = conn.cursor()
        
        # Get all tables from the database
        query = """
        SELECT 
            TABLE_SCHEMA,
            TABLE_NAME,
            TABLE_TYPE
        FROM INFORMATION_SCHEMA.TABLES 
        WHERE TABLE_TYPE = 'BASE TABLE'
        ORDER BY TABLE_SCHEMA, TABLE_NAME
        """
        
        cursor.execute(query)
        tables = cursor.fetchall()
        
        print(f"✅ Found {len(tables)} tables in SQL Server")
        
        # Group tables by schema
        schemas = {}
        for table in tables:
            schema_name = table[0]
            table_name = table[1]
            table_type = table[2]
            
            if schema_name not in schemas:
                schemas[schema_name] = []
            
            schemas[schema_name].append({
                'table_name': table_name,
                'table_type': table_type
            })
        
        # Display results
        for schema_name, tables_list in schemas.items():
            print(f"\n📁 Schema: {schema_name}")
            for table in tables_list:
                print(f"  - {table['table_name']}")
        
        cursor.close()
        conn.close()
        
        return schemas
        
    except Exception as e:
        print(f"❌ Error discovering SQL Server tables: {e}")
        print("💡 Please update the connection parameters in the script")
        return None

def get_table_structure(schema_name, table_name):
    """Get detailed structure of a specific table"""
    try:
        conn = get_sqlserver_connection()
        cursor = conn.cursor()
        
        query = """
        SELECT 
            COLUMN_NAME,
            DATA_TYPE,
            CHARACTER_MAXIMUM_LENGTH,
            IS_NULLABLE,
            COLUMN_DEFAULT,
            ORDINAL_POSITION
        FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?
        ORDER BY ORDINAL_POSITION
        """
        
        cursor.execute(query, (schema_name, table_name))
        columns = cursor.fetchall()
        
        cursor.close()
        conn.close()
        
        return columns
        
    except Exception as e:
        print(f"❌ Error getting structure for {schema_name}.{table_name}: {e}")
        return None

def create_comprehensive_extraction_queries(schemas):
    """Create extraction queries for all tables"""
    print("\n📝 Creating comprehensive extraction queries...")
    
    extraction_queries = []
    
    for schema_name, tables in schemas.items():
        for table in tables:
            table_name = table['table_name']
            
            # Get table structure
            columns = get_table_structure(schema_name, table_name)
            if not columns:
                continue
            
            # Build SELECT query
            column_list = []
            for col in columns:
                col_name = col[0]
                data_type = col[1]
                
                # Handle special cases
                if data_type in ['bit']:
                    column_list.append(f"CASE WHEN {col_name} = 1 THEN 'true' ELSE 'false' END as {col_name}")
                else:
                    column_list.append(col_name)
            
            query = f"""
-- =====================================================
-- {schema_name}.{table_name} EXTRACTION
-- =====================================================

SELECT 
    {', '.join(column_list)}
FROM [{schema_name}].[dbo].[{table_name}]
ORDER BY 1;
"""
            
            extraction_queries.append({
                'schema': schema_name,
                'table': table_name,
                'query': query,
                'columns': [col[0] for col in columns]
            })
    
    return extraction_queries

def create_postgresql_schema_script(extraction_queries):
    """Create PostgreSQL schema creation script for all tables"""
    print("\n🏗️ Creating PostgreSQL schema script...")
    
    schema_script = """
-- =====================================================
-- Complete PostgreSQL Schema Creation Script
-- Generated for all SQL Server tables
-- =====================================================

-- Create the climasys_dev schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS climasys_dev;

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

"""
    
    for table_info in extraction_queries:
        table_name = table_info['table'].lower()
        columns = table_info['columns']
        
        schema_script += f"""
-- =====================================================
-- {table_name.upper()} TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS {table_name} (
    -- Add columns here based on SQL Server structure
    -- This is a template - you'll need to map data types
    id SERIAL PRIMARY KEY,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

"""
    
    return schema_script

def create_migration_plan(schemas, extraction_queries):
    """Create comprehensive migration plan"""
    print("\n📋 Creating migration plan...")
    
    plan = {
        'migration_date': datetime.now().isoformat(),
        'total_tables': sum(len(tables) for tables in schemas.values()),
        'schemas': schemas,
        'extraction_queries': len(extraction_queries),
        'steps': [
            "1. Update SQL Server connection parameters in discover_all_tables.py",
            "2. Run this script to discover all tables",
            "3. Review and update extraction queries",
            "4. Create PostgreSQL schema for all tables",
            "5. Extract data from SQL Server to CSV files",
            "6. Import data to PostgreSQL",
            "7. Migrate stored procedures and functions",
            "8. Test and verify migration"
        ],
        'estimated_time': "2-4 hours depending on data volume"
    }
    
    return plan

def main():
    """Main function"""
    print("🚀 SQL Server to PostgreSQL Complete Migration Discovery")
    print("=" * 60)
    
    # Step 1: Discover all tables
    schemas = discover_sqlserver_tables()
    if not schemas:
        print("\n❌ Could not discover tables. Please update connection parameters.")
        return
    
    # Step 2: Create extraction queries
    extraction_queries = create_comprehensive_extraction_queries(schemas)
    
    # Step 3: Create migration plan
    plan = create_migration_plan(schemas, extraction_queries)
    
    # Step 4: Save results
    print("\n💾 Saving migration plan...")
    
    # Save extraction queries
    with open('complete_extraction_queries.sql', 'w', encoding='utf-8') as f:
        f.write("-- Complete SQL Server Data Extraction Queries\n")
        f.write("-- Generated for all tables\n\n")
        for table_info in extraction_queries:
            f.write(table_info['query'])
            f.write("\n")
    
    # Save schema script
    schema_script = create_postgresql_schema_script(extraction_queries)
    with open('complete_postgresql_schema.sql', 'w', encoding='utf-8') as f:
        f.write(schema_script)
    
    # Save migration plan
    with open('complete_migration_plan.json', 'w', encoding='utf-8') as f:
        json.dump(plan, f, indent=2)
    
    print(f"\n✅ Migration discovery completed!")
    print(f"📊 Found {plan['total_tables']} tables across {len(schemas)} schemas")
    print(f"📝 Created {len(extraction_queries)} extraction queries")
    print(f"📁 Files created:")
    print(f"  - complete_extraction_queries.sql")
    print(f"  - complete_postgresql_schema.sql")
    print(f"  - complete_migration_plan.json")
    
    print(f"\n🎯 Next steps:")
    for step in plan['steps']:
        print(f"  {step}")

if __name__ == "__main__":
    main()
