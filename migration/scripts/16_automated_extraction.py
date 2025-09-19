#!/usr/bin/env python3
"""
Automated SQL Server Data Extraction for Climasys

This script provides an interactive setup for automated data extraction.
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
        logging.FileHandler('automated_extraction.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

def get_sqlserver_connection():
    """Get SQL Server connection details from user."""
    print("\n" + "="*60)
    print("SQL SERVER CONNECTION SETUP")
    print("="*60)
    
    # Get server details
    server = input("Enter SQL Server name/IP (e.g., localhost, 192.168.1.100): ").strip()
    if not server:
        server = "localhost"
    
    database = input("Enter database name (default: Climasys-00010): ").strip()
    if not database:
        database = "Climasys-00010"
    
    # Authentication method
    print("\nAuthentication method:")
    print("1. SQL Server Authentication (username/password)")
    print("2. Windows Authentication")
    
    auth_choice = input("Choose (1 or 2, default: 1): ").strip()
    
    if auth_choice == "2":
        # Windows Authentication
        connection_string = f"DRIVER={{ODBC Driver 17 for SQL Server}};SERVER={server};DATABASE={database};Trusted_Connection=yes;"
    else:
        # SQL Server Authentication
        username = input("Enter username (default: sa): ").strip()
        if not username:
            username = "sa"
        
        password = input("Enter password: ").strip()
        if not password:
            print("Password is required!")
            return None
        
        connection_string = f"DRIVER={{ODBC Driver 17 for SQL Server}};SERVER={server};DATABASE={database};UID={username};PWD={password};"
    
    return connection_string

def test_connection(connection_string):
    """Test the SQL Server connection."""
    try:
        conn = pyodbc.connect(connection_string)
        cursor = conn.cursor()
        cursor.execute("SELECT @@VERSION")
        version = cursor.fetchone()[0]
        conn.close()
        
        print(f"\n✅ Connection successful!")
        print(f"SQL Server Version: {version[:100]}...")
        return True
        
    except Exception as e:
        print(f"\n❌ Connection failed: {e}")
        return False

def extract_core_tables(connection_string):
    """Extract data from core Climasys tables."""
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
    """Main function for automated extraction."""
    print("🚀 AUTOMATED SQL SERVER DATA EXTRACTION")
    print("=" * 60)
    print("This script will extract data from your SQL Server database")
    print("and prepare it for import into PostgreSQL.")
    
    # Get connection details
    connection_string = get_sqlserver_connection()
    if not connection_string:
        print("❌ Connection setup failed. Exiting.")
        sys.exit(1)
    
    # Test connection
    print(f"\n🔍 Testing connection...")
    if not test_connection(connection_string):
        print("❌ Connection test failed. Please check your details and try again.")
        sys.exit(1)
    
    # Extract data
    print(f"\n📤 Starting data extraction...")
    summary = extract_core_tables(connection_string)
    
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
