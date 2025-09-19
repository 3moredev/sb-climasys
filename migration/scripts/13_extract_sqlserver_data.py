#!/usr/bin/env python3
"""
SQL Server Data Extraction Script for Climasys Migration

This script extracts data from SQL Server and saves it to CSV files
for import into PostgreSQL.
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
        logging.FileHandler('sqlserver_extraction.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class SQLServerDataExtractor:
    def __init__(self, connection_string):
        """Initialize the SQL Server data extractor."""
        self.connection_string = connection_string
        self.connection = None
        self.cursor = None
        
    def connect_sqlserver(self):
        """Connect to SQL Server database."""
        try:
            self.connection = pyodbc.connect(self.connection_string)
            self.cursor = self.connection.cursor()
            logger.info("Connected to SQL Server database successfully")
        except Exception as e:
            logger.error(f"Failed to connect to SQL Server: {e}")
            raise
    
    def disconnect_sqlserver(self):
        """Disconnect from SQL Server database."""
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        logger.info("Disconnected from SQL Server database")
    
    def extract_table_data(self, table_name, schema_name="dbo", output_file=None):
        """Extract data from a specific table."""
        try:
            if output_file is None:
                output_file = f"{table_name}.csv"
            
            # Build the query
            query = f"SELECT * FROM [{schema_name}].[{table_name}]"
            
            logger.info(f"Extracting data from {schema_name}.{table_name}")
            
            # Execute query and save to CSV
            df = pd.read_sql(query, self.connection)
            
            if not df.empty:
                # Clean the data for PostgreSQL compatibility
                df = self.clean_data_for_postgres(df)
                
                # Save to CSV
                df.to_csv(output_file, index=False, encoding='utf-8')
                logger.info(f"Extracted {len(df)} records to {output_file}")
                return len(df)
            else:
                logger.warning(f"No data found in {schema_name}.{table_name}")
                return 0
                
        except Exception as e:
            logger.error(f"Failed to extract data from {schema_name}.{table_name}: {e}")
            return 0
    
    def clean_data_for_postgres(self, df):
        """Clean data for PostgreSQL compatibility."""
        # Replace NaN values with None
        df = df.where(pd.notnull(df), None)
        
        # Convert datetime columns to string format
        for col in df.columns:
            if df[col].dtype == 'datetime64[ns]':
                df[col] = df[col].dt.strftime('%Y-%m-%d %H:%M:%S')
            elif df[col].dtype == 'object':
                # Handle string columns - remove any problematic characters
                df[col] = df[col].astype(str).str.replace('\x00', '', regex=False)
        
        return df
    
    def get_table_list(self, schema_name="dbo"):
        """Get list of tables in the specified schema."""
        try:
            query = """
                SELECT TABLE_NAME 
                FROM INFORMATION_SCHEMA.TABLES 
                WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'
                ORDER BY TABLE_NAME
            """
            
            df = pd.read_sql(query, self.connection, params=[schema_name])
            return df['TABLE_NAME'].tolist()
            
        except Exception as e:
            logger.error(f"Failed to get table list: {e}")
            return []
    
    def extract_all_tables(self, schema_name="dbo", output_dir="extracted_data"):
        """Extract data from all tables in the schema."""
        try:
            # Create output directory
            os.makedirs(output_dir, exist_ok=True)
            
            # Get list of tables
            tables = self.get_table_list(schema_name)
            logger.info(f"Found {len(tables)} tables in schema {schema_name}")
            
            extraction_summary = {}
            
            for table in tables:
                output_file = os.path.join(output_dir, f"{table}.csv")
                record_count = self.extract_table_data(table, schema_name, output_file)
                extraction_summary[table] = record_count
            
            # Save extraction summary
            summary_file = os.path.join(output_dir, "extraction_summary.json")
            with open(summary_file, 'w') as f:
                json.dump(extraction_summary, f, indent=2)
            
            logger.info(f"Extraction completed. Summary saved to {summary_file}")
            return extraction_summary
            
        except Exception as e:
            logger.error(f"Failed to extract all tables: {e}")
            return {}
    
    def extract_core_tables(self, output_dir="extracted_data"):
        """Extract data from core Climasys tables."""
        try:
            # Create output directory
            os.makedirs(output_dir, exist_ok=True)
            
            # Core tables to extract (based on our PostgreSQL schema)
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
            
            for table in core_tables:
                output_file = os.path.join(output_dir, f"{table}.csv")
                record_count = self.extract_table_data(table, "dbo", output_file)
                extraction_summary[table] = record_count
            
            # Save extraction summary
            summary_file = os.path.join(output_dir, "core_tables_summary.json")
            with open(summary_file, 'w') as f:
                json.dump(extraction_summary, f, indent=2)
            
            logger.info(f"Core tables extraction completed. Summary saved to {summary_file}")
            return extraction_summary
            
        except Exception as e:
            logger.error(f"Failed to extract core tables: {e}")
            return {}

def main():
    """Main function to run the data extraction."""
    
    # SQL Server connection string - UPDATE THESE VALUES
    # Format: "DRIVER={ODBC Driver 17 for SQL Server};SERVER=your_server;DATABASE=your_database;UID=your_username;PWD=your_password"
    connection_string = os.getenv('SQLSERVER_CONNECTION_STRING', 
        "DRIVER={ODBC Driver 17 for SQL Server};SERVER=localhost;DATABASE=Climasys-00010;UID=sa;PWD=your_password")
    
    logger.info("🔍 Starting SQL Server Data Extraction")
    logger.info("=" * 60)
    
    # Create extractor instance
    extractor = SQLServerDataExtractor(connection_string)
    
    try:
        extractor.connect_sqlserver()
        
        # Extract core tables
        logger.info("Extracting core Climasys tables...")
        summary = extractor.extract_core_tables()
        
        # Print summary
        logger.info("\n📊 Extraction Summary:")
        logger.info("-" * 40)
        total_records = 0
        for table, count in summary.items():
            logger.info(f"{table}: {count} records")
            total_records += count
        
        logger.info(f"\nTotal records extracted: {total_records}")
        logger.info("🎉 Data extraction completed successfully!")
        
    except Exception as e:
        logger.error(f"❌ Data extraction failed: {e}")
        sys.exit(1)
    finally:
        extractor.disconnect_sqlserver()

if __name__ == "__main__":
    main()
