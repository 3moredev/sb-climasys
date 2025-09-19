#!/usr/bin/env python3
"""
PostgreSQL Data Import Script for Climasys Migration

This script imports CSV data extracted from SQL Server into PostgreSQL.
"""

import os
import sys
import pandas as pd
import psycopg2
from psycopg2.extras import execute_values
import logging
from datetime import datetime
import json

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('postgresql_import.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class PostgreSQLDataImporter:
    def __init__(self, postgres_config):
        """Initialize the PostgreSQL data importer."""
        self.postgres_config = postgres_config
        self.connection = None
        self.cursor = None
        
    def connect_postgres(self):
        """Connect to PostgreSQL database."""
        try:
            self.connection = psycopg2.connect(**self.postgres_config)
            self.cursor = self.connection.cursor()
            logger.info("Connected to PostgreSQL database successfully")
        except Exception as e:
            logger.error(f"Failed to connect to PostgreSQL: {e}")
            raise
    
    def disconnect_postgres(self):
        """Disconnect from PostgreSQL database."""
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        logger.info("Disconnected from PostgreSQL database")
    
    def set_schema(self):
        """Set the search path to climasys_dev schema."""
        try:
            self.cursor.execute("SET search_path TO climasys_dev, public;")
            self.connection.commit()
            logger.info("Schema path set to climasys_dev")
        except Exception as e:
            logger.error(f"Failed to set schema: {e}")
            raise
    
    def clear_existing_data(self, table_name):
        """Clear existing data from a table."""
        try:
            self.cursor.execute(f"DELETE FROM {table_name}")
            self.connection.commit()
            logger.info(f"Cleared existing data from {table_name}")
        except Exception as e:
            logger.warning(f"Could not clear data from {table_name}: {e}")
    
    def import_csv_to_table(self, csv_file, table_name, clear_existing=True):
        """Import CSV data into a PostgreSQL table."""
        try:
            if not os.path.exists(csv_file):
                logger.warning(f"CSV file {csv_file} not found")
                return 0
            
            # Read CSV file
            df = pd.read_csv(csv_file)
            
            if df.empty:
                logger.warning(f"CSV file {csv_file} is empty")
                return 0
            
            # Clear existing data if requested
            if clear_existing:
                self.clear_existing_data(table_name)
            
            # Get column names
            columns = df.columns.tolist()
            
            # Prepare data for insertion
            data_tuples = [tuple(row) for row in df.values]
            
            # Build INSERT query
            placeholders = ', '.join(['%s'] * len(columns))
            columns_str = ', '.join(columns)
            insert_query = f"INSERT INTO {table_name} ({columns_str}) VALUES ({placeholders})"
            
            # Insert data
            self.cursor.executemany(insert_query, data_tuples)
            self.connection.commit()
            
            logger.info(f"Imported {len(data_tuples)} records into {table_name}")
            return len(data_tuples)
            
        except Exception as e:
            logger.error(f"Failed to import {csv_file} to {table_name}: {e}")
            self.connection.rollback()
            return 0
    
    def import_all_csv_files(self, data_dir="extracted_data"):
        """Import all CSV files from the data directory."""
        try:
            if not os.path.exists(data_dir):
                logger.error(f"Data directory {data_dir} not found")
                return {}
            
            # Table mapping from SQL Server to PostgreSQL
            table_mapping = {
                'Doctor_Master.csv': 'doctor_master',
                'Clinic_Master.csv': 'clinic_master',
                'User_Master.csv': 'user_master',
                'Patient_Master.csv': 'patient_master',
                'Patient_Visits.csv': 'patient_visits',
                'System_Params.csv': 'system_params',
                'User_Role.csv': 'user_role',
                'Blood_Group_Master.csv': 'blood_group_master',
                'Gender_Master.csv': 'gender_master',
                'Language_Master.csv': 'language_master',
                'License_Key.csv': 'license_key',
                'Role_Master.csv': 'role_master',
                'Shift_Master.csv': 'shift_master',
                'Doctor_Clinic_Shift.csv': 'doctor_clinic_shift',
                'Doctor_Model.csv': 'doctor_model',
                'Model.csv': 'model',
                'Model_Config_Params.csv': 'model_config_params',
                'Visit_Prescription_Overwrite.csv': 'visit_prescription_overwrite'
            }
            
            import_summary = {}
            
            # Import each CSV file
            for csv_file, table_name in table_mapping.items():
                csv_path = os.path.join(data_dir, csv_file)
                record_count = self.import_csv_to_table(csv_path, table_name)
                import_summary[table_name] = record_count
            
            # Save import summary
            summary_file = os.path.join(data_dir, "import_summary.json")
            with open(summary_file, 'w') as f:
                json.dump(import_summary, f, indent=2)
            
            logger.info(f"Import completed. Summary saved to {summary_file}")
            return import_summary
            
        except Exception as e:
            logger.error(f"Failed to import all CSV files: {e}")
            return {}
    
    def verify_import(self):
        """Verify the imported data."""
        try:
            verification_queries = [
                ("Doctor Master", "SELECT COUNT(*) FROM doctor_master"),
                ("Clinic Master", "SELECT COUNT(*) FROM clinic_master"),
                ("User Master", "SELECT COUNT(*) FROM user_master"),
                ("Patient Master", "SELECT COUNT(*) FROM patient_master"),
                ("Patient Visits", "SELECT COUNT(*) FROM patient_visits"),
                ("System Params", "SELECT COUNT(*) FROM system_params"),
                ("User Role", "SELECT COUNT(*) FROM user_role"),
                ("Blood Group Master", "SELECT COUNT(*) FROM blood_group_master"),
                ("Gender Master", "SELECT COUNT(*) FROM gender_master"),
                ("Language Master", "SELECT COUNT(*) FROM language_master"),
                ("License Key", "SELECT COUNT(*) FROM license_key"),
                ("Role Master", "SELECT COUNT(*) FROM role_master"),
                ("Shift Master", "SELECT COUNT(*) FROM shift_master"),
                ("Doctor Clinic Shift", "SELECT COUNT(*) FROM doctor_clinic_shift"),
                ("Doctor Model", "SELECT COUNT(*) FROM doctor_model"),
                ("Model", "SELECT COUNT(*) FROM model"),
                ("Model Config Params", "SELECT COUNT(*) FROM model_config_params"),
                ("Visit Prescription Overwrite", "SELECT COUNT(*) FROM visit_prescription_overwrite")
            ]
            
            logger.info("\n📊 Data Verification:")
            logger.info("-" * 40)
            
            for table_name, query in verification_queries:
                self.cursor.execute(query)
                count = self.cursor.fetchone()[0]
                logger.info(f"{table_name}: {count} records")
                
        except Exception as e:
            logger.error(f"Failed to verify import: {e}")
            raise

def main():
    """Main function to run the data import."""
    
    # PostgreSQL configuration
    postgres_config = {
        'host': os.getenv('DB_HOST', 'localhost'),
        'port': os.getenv('DB_PORT', '5432'),
        'database': os.getenv('DB_NAME', 'climasys_dev'),
        'user': os.getenv('DB_USER', 'postgres'),
        'password': os.getenv('DB_PASSWORD', 'root')
    }
    
    logger.info("🚀 Starting PostgreSQL Data Import")
    logger.info("=" * 60)
    
    # Create importer instance
    importer = PostgreSQLDataImporter(postgres_config)
    
    try:
        importer.connect_postgres()
        importer.set_schema()
        
        # Import all CSV files
        logger.info("Importing data from CSV files...")
        summary = importer.import_all_csv_files()
        
        # Verify import
        importer.verify_import()
        
        # Print summary
        logger.info("\n📊 Import Summary:")
        logger.info("-" * 40)
        total_records = 0
        for table, count in summary.items():
            logger.info(f"{table}: {count} records")
            total_records += count
        
        logger.info(f"\nTotal records imported: {total_records}")
        logger.info("🎉 Data import completed successfully!")
        
    except Exception as e:
        logger.error(f"❌ Data import failed: {e}")
        sys.exit(1)
    finally:
        importer.disconnect_postgres()

if __name__ == "__main__":
    main()
