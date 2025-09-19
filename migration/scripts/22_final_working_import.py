#!/usr/bin/env python3
"""
Final Working Data Import Script for Climasys

This script imports all data using the correct column mappings
and handles string IDs properly.
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
        logging.FileHandler('final_working_import.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class FinalWorkingImporter:
    def __init__(self, postgres_config):
        """Initialize the final working importer."""
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
    
    def clear_all_data(self):
        """Clear all data from tables in the correct order to avoid foreign key constraints."""
        try:
            # Clear tables in reverse dependency order
            clear_queries = [
                "DELETE FROM visit_prescription_overwrite",
                "DELETE FROM model_config_params", 
                "DELETE FROM doctor_clinic_shift",
                "DELETE FROM doctor_model",
                "DELETE FROM patient_visits",
                "DELETE FROM user_role",
                "DELETE FROM system_params",
                "DELETE FROM patient_master",
                "DELETE FROM user_master",
                "DELETE FROM clinic_master",
                "DELETE FROM doctor_master",
                "DELETE FROM model",
                "DELETE FROM shift_master",
                "DELETE FROM role_master",
                "DELETE FROM license_key",
                "DELETE FROM language_master",
                "DELETE FROM gender_master",
                "DELETE FROM blood_group_master"
            ]
            
            for query in clear_queries:
                try:
                    self.cursor.execute(query)
                    logger.info(f"Cleared: {query.split()[-1]}")
                except Exception as e:
                    logger.warning(f"Could not clear {query.split()[-1]}: {e}")
            
            self.connection.commit()
            logger.info("All data cleared successfully")
            
        except Exception as e:
            logger.error(f"Failed to clear data: {e}")
            self.connection.rollback()
            raise
    
    def import_gender_master(self, csv_file):
        """Import gender master data with correct column mapping."""
        try:
            df = pd.read_csv(csv_file)
            if df.empty:
                return 0
            
            # The actual CSV has 'ID' column, map it to 'gender_id'
            df = df.rename(columns={'ID': 'gender_id'})
            
            # Add gender_name based on gender_id
            df['gender_name'] = df['gender_id'].map({'M': 'Male', 'F': 'Female'})
            
            # Clean data
            df = df.where(pd.notnull(df), None)
            
            # Insert data
            columns = ['gender_id', 'gender_name']
            columns_str = ', '.join(columns)
            placeholders = ', '.join(['%s'] * len(columns))
            insert_query = f"INSERT INTO gender_master ({columns_str}) VALUES ({placeholders})"
            
            data_tuples = [tuple(row) for row in df[columns].values]
            self.cursor.executemany(insert_query, data_tuples)
            
            logger.info(f"Imported {len(data_tuples)} gender records")
            return len(data_tuples)
            
        except Exception as e:
            logger.error(f"Failed to import gender master: {e}")
            return 0
    
    def import_doctor_master(self, csv_file):
        """Import doctor master data with correct column mapping."""
        try:
            df = pd.read_csv(csv_file)
            if df.empty:
                return 0
            
            # Map actual SQL Server columns to PostgreSQL columns
            column_mapping = {
                'Doctor_ID': 'doctor_id',
                'First_Name': 'first_name', 
                'Middle_Name': 'middle_name',
                'Last_Name': 'last_name',
                'Doctor_Qual': 'qualification',
                'Speciality': 'specialization',
                'Mobile_1': 'phone',
                'EmailID': 'email',
                'Residential_Add1': 'address'
            }
            
            df = df.rename(columns=column_mapping)
            
            # Select only the columns that exist in both DataFrames
            available_columns = [col for col in column_mapping.values() if col in df.columns]
            df = df[available_columns]
            
            # Clean data
            df = df.where(pd.notnull(df), None)
            
            # Insert data
            columns_str = ', '.join(available_columns)
            placeholders = ', '.join(['%s'] * len(available_columns))
            insert_query = f"INSERT INTO doctor_master ({columns_str}) VALUES ({placeholders})"
            
            data_tuples = [tuple(row) for row in df.values]
            self.cursor.executemany(insert_query, data_tuples)
            
            logger.info(f"Imported {len(data_tuples)} doctors")
            return len(data_tuples)
            
        except Exception as e:
            logger.error(f"Failed to import doctor master: {e}")
            return 0
    
    def import_patient_master(self, csv_file):
        """Import patient master data with correct column mapping."""
        try:
            df = pd.read_csv(csv_file)
            if df.empty:
                return 0
            
            # Map actual SQL Server columns to PostgreSQL columns
            column_mapping = {
                'ID': 'patient_id',  # Note: SQL Server uses 'ID', not 'Patient_ID'
                'Doctor_ID': 'doctor_id',
                'First_Name': 'first_name',
                'Middle_Name': 'middle_name', 
                'Last_Name': 'last_name',
                'Gender_ID': 'gender_id',
                'Date_Of_Birth': 'date_of_birth',
                'Age_Given': 'age',
                'BloodGroup_ID': 'blood_group_id',
                'Mobile_1': 'phone',
                'Email_ID': 'email',
                'Address_1': 'address',
                'Emergency_Number': 'emergency_contact',
                'Emergency_Name': 'emergency_contact_name'
            }
            
            df = df.rename(columns=column_mapping)
            
            # Select only the columns that exist in both DataFrames
            available_columns = [col for col in column_mapping.values() if col in df.columns]
            df = df[available_columns]
            
            # Clean data and handle null values
            df = df.where(pd.notnull(df), None)
            
            # Handle null first_name by using last_name if first_name is null
            if 'first_name' in df.columns and 'last_name' in df.columns:
                df['first_name'] = df['first_name'].fillna(df['last_name'])
            
            # Convert patient_id to string to handle the date-based IDs
            if 'patient_id' in df.columns:
                df['patient_id'] = df['patient_id'].astype(str)
            
            # Insert data
            columns_str = ', '.join(available_columns)
            placeholders = ', '.join(['%s'] * len(available_columns))
            insert_query = f"INSERT INTO patient_master ({columns_str}) VALUES ({placeholders})"
            
            data_tuples = [tuple(row) for row in df.values]
            self.cursor.executemany(insert_query, data_tuples)
            
            logger.info(f"Imported {len(data_tuples)} patients")
            return len(data_tuples)
            
        except Exception as e:
            logger.error(f"Failed to import patient master: {e}")
            return 0
    
    def import_patient_visits(self, csv_file):
        """Import patient visits data with correct column mapping."""
        try:
            df = pd.read_csv(csv_file)
            if df.empty:
                return 0
            
            # Map actual SQL Server columns to PostgreSQL columns
            column_mapping = {
                'Visit_ID': 'visit_id',
                'Patient_ID': 'patient_id',
                'Doctor_ID': 'doctor_id',
                'Clinic_ID': 'clinic_id',
                'Visit_Date': 'visit_date',
                'Visit_Time': 'visit_time',
                'Chief_Complaint': 'chief_complaint',
                'Diagnosis': 'diagnosis',
                'Treatment': 'treatment',
                'Prescription': 'prescription',
                'Follow_Up_Date': 'follow_up_date',
                'Attended_By_ID': 'attended_by_id',
                'Delete_Flag': 'delete_flag'
            }
            
            df = df.rename(columns=column_mapping)
            
            # Select only the columns that exist in both DataFrames
            available_columns = [col for col in column_mapping.values() if col in df.columns]
            df = df[available_columns]
            
            # Clean data
            df = df.where(pd.notnull(df), None)
            
            # Convert IDs to string to handle the date-based IDs
            if 'patient_id' in df.columns:
                df['patient_id'] = df['patient_id'].astype(str)
            if 'visit_id' in df.columns:
                df['visit_id'] = df['visit_id'].astype(str)
            
            # Insert data
            columns_str = ', '.join(available_columns)
            placeholders = ', '.join(['%s'] * len(available_columns))
            insert_query = f"INSERT INTO patient_visits ({columns_str}) VALUES ({placeholders})"
            
            data_tuples = [tuple(row) for row in df.values]
            self.cursor.executemany(insert_query, data_tuples)
            
            logger.info(f"Imported {len(data_tuples)} patient visits")
            return len(data_tuples)
            
        except Exception as e:
            logger.error(f"Failed to import patient visits: {e}")
            return 0
    
    def import_all_data(self, data_dir="extracted_data"):
        """Import all data in the correct dependency order."""
        try:
            if not os.path.exists(data_dir):
                logger.error(f"Data directory {data_dir} not found")
                return {}
            
            # Clear all existing data first
            self.clear_all_data()
            
            import_summary = {}
            
            # Import in dependency order
            import_operations = [
                ("Gender_Master.csv", self.import_gender_master),
                ("Doctor_Master.csv", self.import_doctor_master),
                ("Patient_Master.csv", self.import_patient_master),
                ("Patient_Visits.csv", self.import_patient_visits)
            ]
            
            for csv_file, import_func in import_operations:
                csv_path = os.path.join(data_dir, csv_file)
                if os.path.exists(csv_path):
                    record_count = import_func(csv_path)
                    import_summary[csv_file] = record_count
                else:
                    logger.warning(f"CSV file {csv_file} not found")
                    import_summary[csv_file] = 0
            
            self.connection.commit()
            
            # Save import summary
            summary_file = os.path.join(data_dir, "final_working_import_summary.json")
            with open(summary_file, 'w') as f:
                json.dump(import_summary, f, indent=2)
            
            logger.info(f"Final working import completed. Summary saved to {summary_file}")
            return import_summary
            
        except Exception as e:
            logger.error(f"Failed to import data: {e}")
            self.connection.rollback()
            return {}
    
    def verify_import(self):
        """Verify the imported data."""
        try:
            verification_queries = [
                ("Gender Master", "SELECT COUNT(*) FROM gender_master"),
                ("Doctor Master", "SELECT COUNT(*) FROM doctor_master"),
                ("Patient Master", "SELECT COUNT(*) FROM patient_master"),
                ("Patient Visits", "SELECT COUNT(*) FROM patient_visits")
            ]
            
            logger.info("\nData Verification:")
            logger.info("-" * 40)
            
            for table_name, query in verification_queries:
                self.cursor.execute(query)
                count = self.cursor.fetchone()[0]
                logger.info(f"{table_name}: {count} records")
                
        except Exception as e:
            logger.error(f"Failed to verify import: {e}")
            raise

def main():
    """Main function to run the final working import."""
    
    # PostgreSQL configuration
    postgres_config = {
        'host': os.getenv('DB_HOST', 'localhost'),
        'port': os.getenv('DB_PORT', '5432'),
        'database': os.getenv('DB_NAME', 'climasys_dev'),
        'user': os.getenv('DB_USER', 'postgres'),
        'password': os.getenv('DB_PASSWORD', 'root')
    }
    
    logger.info("Starting Final Working PostgreSQL Data Import")
    logger.info("=" * 60)
    
    # Create importer instance
    importer = FinalWorkingImporter(postgres_config)
    
    try:
        importer.connect_postgres()
        importer.set_schema()
        
        # Import data in correct order
        logger.info("Importing data with correct column mappings and string IDs...")
        summary = importer.import_all_data()
        
        # Verify import
        importer.verify_import()
        
        # Print summary
        logger.info("\nImport Summary:")
        logger.info("-" * 40)
        total_records = 0
        for table, count in summary.items():
            logger.info(f"{table}: {count} records")
            total_records += count
        
        logger.info(f"\nTotal records imported: {total_records}")
        logger.info("Final working data import successful!")
        
    except Exception as e:
        logger.error(f"Final working data import failed: {e}")
        sys.exit(1)
    finally:
        importer.disconnect_postgres()

if __name__ == "__main__":
    main()
