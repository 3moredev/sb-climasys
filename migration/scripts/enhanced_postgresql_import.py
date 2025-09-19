#!/usr/bin/env python3
"""
Enhanced Climasys Data Migration Script
SQL Server to PostgreSQL Data Import

This script provides comprehensive data migration from CSV files
exported from SQL Server to PostgreSQL climasys_dev schema.

Features:
- Automatic CSV file detection
- Data type validation and conversion
- Error handling and logging
- Progress tracking
- Data integrity verification

Requirements:
- psycopg2-binary
- pandas
- python-dotenv

Install with: pip install psycopg2-binary pandas python-dotenv
"""

import os
import sys
import pandas as pd
import psycopg2
from psycopg2.extras import execute_values
from datetime import datetime
import logging
import json
from pathlib import Path

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler(f'enhanced_migration_{datetime.now().strftime("%Y%m%d_%H%M%S")}.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class EnhancedClimasysMigrator:
    def __init__(self, db_config):
        """Initialize the migrator with database configuration."""
        self.db_config = db_config
        self.connection = None
        self.cursor = None
        self.migration_summary = {
            'start_time': datetime.now().isoformat(),
            'tables_processed': 0,
            'records_imported': 0,
            'errors': [],
            'warnings': []
        }
        
    def connect(self):
        """Connect to PostgreSQL database."""
        try:
            self.connection = psycopg2.connect(**self.db_config)
            self.cursor = self.connection.cursor()
            logger.info("Connected to PostgreSQL database successfully")
            return True
        except Exception as e:
            logger.error(f"Failed to connect to database: {e}")
            return False
    
    def disconnect(self):
        """Disconnect from database."""
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        logger.info("Disconnected from database")
    
    def get_table_columns(self, table_name):
        """Get column information for a table."""
        try:
            query = """
            SELECT column_name, data_type, is_nullable, column_default
            FROM information_schema.columns
            WHERE table_schema = 'climasys_dev' AND table_name = %s
            ORDER BY ordinal_position;
            """
            self.cursor.execute(query, (table_name,))
            return self.cursor.fetchall()
        except Exception as e:
            logger.error(f"Failed to get column information for {table_name}: {e}")
            return []
    
    def clean_data(self, df, table_name):
        """Clean and prepare data for import."""
        try:
            # Get table column information
            columns = self.get_table_columns(table_name)
            if not columns:
                logger.warning(f"No column information found for {table_name}")
                return df
            
            # Create column mapping
            column_mapping = {col[0]: col[1] for col in columns}
            
            # Clean data based on column types
            for column, data_type in column_mapping.items():
                if column in df.columns:
                    # Handle boolean columns
                    if data_type == 'boolean':
                        df[column] = df[column].map({'true': True, 'false': False, '1': True, '0': False})
                    
                    # Handle date/time columns
                    elif 'timestamp' in data_type or 'date' in data_type:
                        df[column] = pd.to_datetime(df[column], errors='coerce')
                    
                    # Handle numeric columns
                    elif 'integer' in data_type or 'numeric' in data_type:
                        df[column] = pd.to_numeric(df[column], errors='coerce')
                    
                    # Clean string columns
                    elif 'character' in data_type or 'text' in data_type:
                        df[column] = df[column].astype(str).str.strip()
                        df[column] = df[column].replace('nan', None)
            
            # Remove rows where all values are null
            df = df.dropna(how='all')
            
            logger.info(f"Data cleaned for {table_name}: {len(df)} records")
            return df
            
        except Exception as e:
            logger.error(f"Failed to clean data for {table_name}: {e}")
            return df
    
    def import_csv_file(self, csv_path, table_name):
        """Import a single CSV file to PostgreSQL table."""
        try:
            logger.info(f"Processing {csv_path} for table {table_name}")
            
            # Check if CSV file exists
            if not os.path.exists(csv_path):
                logger.warning(f"CSV file not found: {csv_path}")
                return False
            
            # Read CSV file
            df = pd.read_csv(csv_path)
            if df.empty:
                logger.warning(f"CSV file is empty: {csv_path}")
                return True
            
            # Clean data
            df = self.clean_data(df, table_name)
            
            # Get table columns
            columns = self.get_table_columns(table_name)
            if not columns:
                logger.error(f"Cannot import to {table_name}: no column information")
                return False
            
            # Prepare data for insertion
            table_columns = [col[0] for col in columns]
            available_columns = [col for col in table_columns if col in df.columns]
            
            if not available_columns:
                logger.error(f"No matching columns found between CSV and table {table_name}")
                return False
            
            # Filter DataFrame to only include available columns
            df_filtered = df[available_columns]
            
            # Convert DataFrame to list of tuples
            data_tuples = [tuple(row) for row in df_filtered.values]
            
            # Create insert query
            placeholders = ', '.join(['%s'] * len(available_columns))
            columns_str = ', '.join(available_columns)
            insert_query = f"""
            INSERT INTO climasys_dev.{table_name} ({columns_str})
            VALUES ({placeholders})
            ON CONFLICT DO NOTHING;
            """
            
            # Execute batch insert
            execute_values(
                self.cursor,
                insert_query,
                data_tuples,
                template=None,
                page_size=1000
            )
            
            self.connection.commit()
            
            records_imported = len(data_tuples)
            logger.info(f"Successfully imported {records_imported} records to {table_name}")
            
            # Update migration summary
            self.migration_summary['tables_processed'] += 1
            self.migration_summary['records_imported'] += records_imported
            
            return True
            
        except Exception as e:
            logger.error(f"Failed to import {csv_path} to {table_name}: {e}")
            self.migration_summary['errors'].append({
                'file': csv_path,
                'table': table_name,
                'error': str(e)
            })
            self.connection.rollback()
            return False
    
    def verify_import(self):
        """Verify the imported data."""
        try:
            logger.info("Verifying imported data...")
            
            # Get list of tables in climasys_dev schema
            self.cursor.execute("""
                SELECT table_name 
                FROM information_schema.tables 
                WHERE table_schema = 'climasys_dev'
                ORDER BY table_name;
            """)
            tables = [row[0] for row in self.cursor.fetchall()]
            
            verification_results = {}
            
            for table in tables:
                self.cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table};")
                count = self.cursor.fetchone()[0]
                verification_results[table] = count
                logger.info(f"Table {table}: {count} records")
            
            self.migration_summary['verification_results'] = verification_results
            logger.info("Data verification completed")
            
        except Exception as e:
            logger.error(f"Failed to verify imported data: {e}")
            self.migration_summary['errors'].append({
                'operation': 'verification',
                'error': str(e)
            })
    
    def run_migration(self, csv_directory):
        """Run the complete migration process."""
        try:
            logger.info("Starting enhanced migration process...")
            
            # Connect to database
            if not self.connect():
                return False
            
            # Define table mappings
            table_mappings = {
                'language_master.csv': 'language_master',
                'role_master.csv': 'role_master',
                'gender_master.csv': 'gender_master',
                'blood_group_master.csv': 'blood_group_master',
                'doctor_master.csv': 'doctor_master',
                'clinic_master.csv': 'clinic_master',
                'user_master.csv': 'user_master',
                'user_role.csv': 'user_role',
                'patient_master.csv': 'patient_master',
                'patient_visits.csv': 'patient_visits',
                'medicine_master.csv': 'medicine_master',
                'prescription_master.csv': 'prescription_master',
                'visit_prescription_overwrite.csv': 'visit_prescription_overwrite',
                'system_params.csv': 'system_params',
                'license_key.csv': 'license_key',
                'shift_master.csv': 'shift_master',
                'doctor_clinic_shift.csv': 'doctor_clinic_shift',
                'model.csv': 'model',
                'doctor_model.csv': 'doctor_model',
                'model_config_params.csv': 'model_config_params',
                'billing_master.csv': 'billing_master',
                'billing_details.csv': 'billing_details',
                'lab_test_master.csv': 'lab_test_master',
                'patient_lab_tests.csv': 'patient_lab_tests',
                'insurance_company_master.csv': 'insurance_company_master',
                'patient_insurance.csv': 'patient_insurance'
            }
            
            # Process each CSV file
            csv_files = [f for f in os.listdir(csv_directory) if f.endswith('.csv')]
            
            if not csv_files:
                logger.warning(f"No CSV files found in {csv_directory}")
                return True
            
            logger.info(f"Found {len(csv_files)} CSV files to process")
            
            for csv_file in csv_files:
                if csv_file in table_mappings:
                    csv_path = os.path.join(csv_directory, csv_file)
                    table_name = table_mappings[csv_file]
                    self.import_csv_file(csv_path, table_name)
                else:
                    logger.warning(f"No table mapping found for {csv_file}")
                    self.migration_summary['warnings'].append(f"No table mapping for {csv_file}")
            
            # Verify import
            self.verify_import()
            
            # Complete migration summary
            self.migration_summary['end_time'] = datetime.now().isoformat()
            self.migration_summary['duration'] = (
                datetime.fromisoformat(self.migration_summary['end_time']) - 
                datetime.fromisoformat(self.migration_summary['start_time'])
            ).total_seconds()
            
            # Save migration summary
            summary_file = f'migration_summary_{datetime.now().strftime("%Y%m%d_%H%M%S")}.json'
            with open(summary_file, 'w') as f:
                json.dump(self.migration_summary, f, indent=2, default=str)
            
            logger.info(f"Migration summary saved to {summary_file}")
            logger.info("Enhanced migration process completed successfully!")
            
            return True
            
        except Exception as e:
            logger.error(f"Migration process failed: {e}")
            self.migration_summary['errors'].append({
                'operation': 'migration',
                'error': str(e)
            })
            return False
        
        finally:
            self.disconnect()

def load_config():
    """Load configuration from environment variables or .env file."""
    try:
        from dotenv import load_dotenv
        load_dotenv()
    except ImportError:
        logger.warning("python-dotenv not installed, using environment variables only")
    
    config = {
        'host': os.getenv('DB_HOST', 'localhost'),
        'port': os.getenv('DB_PORT', '5432'),
        'database': os.getenv('DB_NAME', 'climasys_dev'),
        'user': os.getenv('DB_USER', 'postgres'),
        'password': os.getenv('DB_PASSWORD', '')
    }
    
    return config

def main():
    """Main function."""
    try:
        logger.info("Enhanced Climasys Migration Script Starting...")
        
        # Load configuration
        config = load_config()
        logger.info(f"Database configuration: {config['host']}:{config['port']}/{config['database']}")
        
        # Get CSV directory
        csv_directory = os.getenv('CSV_DIRECTORY', './csv_data')
        if not os.path.exists(csv_directory):
            logger.error(f"CSV directory not found: {csv_directory}")
            return 1
        
        # Create migrator and run migration
        migrator = EnhancedClimasysMigrator(config)
        success = migrator.run_migration(csv_directory)
        
        if success:
            logger.info("Migration completed successfully!")
            return 0
        else:
            logger.error("Migration failed!")
            return 1
            
    except Exception as e:
        logger.error(f"Script execution failed: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())

