#!/usr/bin/env python3
"""
Quick SQL Server Data Extraction for Climasys

This script attempts to connect to SQL Server with common configurations
and extract data automatically.
"""

import os
import sys
import pandas as pd
import pyodbc
import logging
from datetime import datetime
import json

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('quick_extraction.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

def try_connection_strings():
    """Try common SQL Server connection configurations."""
    
    # Common connection configurations to try
    connection_configs = [
        # Windows Authentication with SQL Server Express
        {
            'name': 'Windows Auth - SQL Express (localhost)',
            'string': 'DRIVER={SQL Server};SERVER=localhost\\SQLEXPRESS;DATABASE=Climasys-00010;Trusted_Connection=yes;'
        },
        {
            'name': 'Windows Auth - SQL Express (Native Client)',
            'string': 'DRIVER={SQL Server Native Client 11.0};SERVER=localhost\\SQLEXPRESS;DATABASE=Climasys-00010;Trusted_Connection=yes;'
        },
        {
            'name': 'Windows Auth - SQL Express (local)',
            'string': 'DRIVER={SQL Server};SERVER=.\\SQLEXPRESS;DATABASE=Climasys-00010;Trusted_Connection=yes;'
        },
        # SQL Server Authentication with SQL Express
        {
            'name': 'SQL Auth - sa/root (SQL Express)',
            'string': 'DRIVER={SQL Server};SERVER=localhost\\SQLEXPRESS;DATABASE=Climasys-00010;UID=sa;PWD=root;'
        },
        {
            'name': 'SQL Auth - sa/sa (SQL Express)',
            'string': 'DRIVER={SQL Server};SERVER=localhost\\SQLEXPRESS;DATABASE=Climasys-00010;UID=sa;PWD=sa;'
        },
        {
            'name': 'SQL Auth - sa/password (SQL Express)',
            'string': 'DRIVER={SQL Server};SERVER=localhost\\SQLEXPRESS;DATABASE=Climasys-00010;UID=sa;PWD=password;'
        },
        {
            'name': 'SQL Auth - sa/123456 (SQL Express)',
            'string': 'DRIVER={SQL Server};SERVER=localhost\\SQLEXPRESS;DATABASE=Climasys-00010;UID=sa;PWD=123456;'
        },
        {
            'name': 'SQL Auth - sa/root (Native Client Express)',
            'string': 'DRIVER={SQL Server Native Client 11.0};SERVER=localhost\\SQLEXPRESS;DATABASE=Climasys-00010;UID=sa;PWD=root;'
        },
        {
            'name': 'SQL Auth - sa/sa (Native Client Express)',
            'string': 'DRIVER={SQL Server Native Client 11.0};SERVER=localhost\\SQLEXPRESS;DATABASE=Climasys-00010;UID=sa;PWD=sa;'
        }
    ]
    
    print("🔍 Trying common SQL Server connection configurations...")
    print("-" * 60)
    
    for config in connection_configs:
        try:
            print(f"Trying: {config['name']}")
            conn = pyodbc.connect(config['string'])
            cursor = conn.cursor()
            cursor.execute("SELECT @@VERSION")
            version = cursor.fetchone()[0]
            conn.close()
            
            print(f"✅ SUCCESS: {config['name']}")
            print(f"SQL Server Version: {version[:100]}...")
            return config['string']
            
        except Exception as e:
            print(f"❌ Failed: {config['name']} - {str(e)[:50]}...")
    
    return None

def extract_data(connection_string):
    """Extract data from SQL Server tables."""
    try:
        # Create output directory
        output_dir = "extracted_data"
        os.makedirs(output_dir, exist_ok=True)
        
        # Connect to SQL Server
        conn = pyodbc.connect(connection_string)
        
        # Core tables to extract
        core_tables = [
            'Doctor_Master',
            'Clinic_Master', 
            'User_Master',
            'Patient_Master',
            'Patient_Visits',
            'System_Params',
            'User_Role',
            'Blood_Group_Master',
            'Gender_Master',
            'Language_Master',
            'License_Key',
            'Role_Master',
            'Shift_Master',
            'Doctor_Clinic_Shift',
            'Doctor_Model',
            'Model',
            'Model_Config_Params',
            'Visit_Prescription_Overwrite'
        ]
        
        extraction_summary = {}
        total_records = 0
        
        print(f"\n📤 Extracting data from {len(core_tables)} tables...")
        print("-" * 50)
        
        for table in core_tables:
            try:
                # Build query
                query = f"SELECT * FROM [dbo].[{table}]"
                
                # Execute query and save to CSV
                df = pd.read_sql(query, conn)
                
                if not df.empty:
                    # Clean the data for PostgreSQL compatibility
                    df = df.where(pd.notnull(df), None)
                    
                    # Convert datetime columns to string format
                    for col in df.columns:
                        if df[col].dtype == 'datetime64[ns]':
                            df[col] = df[col].dt.strftime('%Y-%m-%d %H:%M:%S')
                        elif df[col].dtype == 'object':
                            # Handle string columns - remove any problematic characters
                            df[col] = df[col].astype(str).str.replace('\x00', '', regex=False)
                    
                    # Save to CSV
                    output_file = os.path.join(output_dir, f"{table}.csv")
                    df.to_csv(output_file, index=False, encoding='utf-8')
                    
                    record_count = len(df)
                    extraction_summary[table] = record_count
                    total_records += record_count
                    
                    print(f"✅ {table}: {record_count} records")
                else:
                    extraction_summary[table] = 0
                    print(f"⚠️  {table}: No data found")
                    
            except Exception as e:
                print(f"❌ {table}: Failed - {e}")
                extraction_summary[table] = 0
        
        conn.close()
        
        # Save extraction summary
        summary_file = os.path.join(output_dir, "extraction_summary.json")
        with open(summary_file, 'w') as f:
            json.dump(extraction_summary, f, indent=2)
        
        print(f"\n📊 Extraction Summary:")
        print("-" * 30)
        print(f"Total records extracted: {total_records}")
        print(f"Summary saved to: {summary_file}")
        
        return extraction_summary
        
    except Exception as e:
        print(f"❌ Extraction failed: {e}")
        return {}

def main():
    """Main function for quick extraction."""
    print("🚀 QUICK SQL SERVER DATA EXTRACTION")
    print("=" * 60)
    print("This script will try common SQL Server configurations")
    print("and extract data automatically.")
    
    # Try to find a working connection
    connection_string = try_connection_strings()
    
    if not connection_string:
        print(f"\n❌ Could not connect to SQL Server with any common configuration.")
        print("Please check:")
        print("1. SQL Server is running")
        print("2. Database 'Climasys-00010' exists")
        print("3. ODBC Driver 17 for SQL Server is installed")
        print("4. Your SQL Server credentials")
        print("\nYou can also run the interactive script:")
        print("python 16_automated_extraction.py")
        sys.exit(1)
    
    # Extract data
    print(f"\n📤 Starting data extraction...")
    summary = extract_data(connection_string)
    
    if summary:
        print(f"\n🎉 EXTRACTION COMPLETED SUCCESSFULLY!")
        print("=" * 60)
        print("Next steps:")
        print("1. Run: python 14_import_to_postgresql.py")
        print("2. Start your Spring Boot backend")
        print("3. Test the dashboard with real data")
    else:
        print(f"\n❌ EXTRACTION FAILED!")
        print("Please check the error messages above and try again.")

if __name__ == "__main__":
    main()
