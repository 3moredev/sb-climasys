#!/usr/bin/env python3
"""
Climasys Data Migration Script
SQL Server to PostgreSQL Data Import

This script reads CSV files exported from SQL Server and imports them
into the PostgreSQL climasys_dev schema.

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

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('migration.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class ClimasysMigrator:
    def __init__(self, db_config):
        """Initialize the migrator with database configuration."""
        self.db_config = db_config
        self.connection = None
        self.cursor = None
        
    def connect(self):
        """Connect to PostgreSQL database."""
        try:
            self.connection = psycopg2.connect(**self.db_config)
            self.cursor = self.connection.cursor()
            logger.info("Connected to PostgreSQL database successfully")
        except Exception as e:
            logger.error(f"Failed to connect to database: {e}")
            raise
    
    def disconnect(self):
        """Disconnect from PostgreSQL database."""
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        logger.info("Disconnected from database")
    
    def set_schema(self):
        """Set the search path to climasys_dev schema."""
        try:
            self.cursor.execute("SET search_path TO climasys_dev, public;")
            self.connection.commit()
            logger.info("Schema path set to climasys_dev")
        except Exception as e:
            logger.error(f"Failed to set schema: {e}")
            raise
    
    def import_csv_data(self, csv_file, table_name, columns_mapping=None):
        """
        Import data from CSV file to PostgreSQL table.
        
        Args:
            csv_file (str): Path to CSV file
            table_name (str): Target table name
            columns_mapping (dict): Optional column mapping from CSV to DB
        """
        try:
            if not os.path.exists(csv_file):
                logger.warning(f"CSV file not found: {csv_file}")
                return False
            
            # Read CSV file
            df = pd.read_csv(csv_file)
            logger.info(f"Read {len(df)} rows from {csv_file}")
            
            # Apply column mapping if provided
            if columns_mapping:
                df = df.rename(columns=columns_mapping)
            
            # Convert boolean columns
            boolean_columns = [col for col in df.columns if 'is_active' in col.lower() or 'is_default' in col.lower() or 'delete_flag' in col.lower()]
            for col in boolean_columns:
                df[col] = df[col].map({'true': True, 'false': False, '1': True, '0': False})
            
            # Convert date columns
            date_columns = [col for col in df.columns if 'date' in col.lower() or 'created_on' in col.lower() or 'updated_on' in col.lower()]
            for col in date_columns:
                if col in df.columns:
                    df[col] = pd.to_datetime(df[col], errors='coerce')
            
            # Convert time columns
            time_columns = [col for col in df.columns if 'time' in col.lower()]
            for col in time_columns:
                if col in df.columns:
                    df[col] = pd.to_datetime(df[col], format='%H:%M:%S', errors='coerce').dt.time
            
            # Prepare data for insertion
            columns = list(df.columns)
            values = [tuple(row) for row in df.values]
            
            # Create INSERT statement with ON CONFLICT
            placeholders = ', '.join(['%s'] * len(columns))
            columns_str = ', '.join(columns)
            
            # Get primary key for ON CONFLICT clause
            primary_key = self.get_primary_key(table_name)
            if primary_key:
                conflict_clause = f"ON CONFLICT ({primary_key}) DO UPDATE SET "
                update_clauses = []
                for col in columns:
                    if col != primary_key:
                        update_clauses.append(f"{col} = EXCLUDED.{col}")
                update_clauses.append("updated_on = CURRENT_TIMESTAMP")
                conflict_clause += ', '.join(update_clauses)
            else:
                conflict_clause = "ON CONFLICT DO NOTHING"
            
            insert_sql = f"""
                INSERT INTO {table_name} ({columns_str})
                VALUES ({placeholders})
                {conflict_clause}
            """
            
            # Execute batch insert
            execute_values(
                self.cursor,
                insert_sql,
                values,
                template=None,
                page_size=1000
            )
            
            self.connection.commit()
            logger.info(f"Successfully imported {len(df)} rows to {table_name}")
            return True
            
        except Exception as e:
            logger.error(f"Failed to import {csv_file} to {table_name}: {e}")
            self.connection.rollback()
            return False
    
    def get_primary_key(self, table_name):
        """Get the primary key column name for a table."""
        try:
            self.cursor.execute("""
                SELECT column_name
                FROM information_schema.table_constraints tc
                JOIN information_schema.key_column_usage kcu
                    ON tc.constraint_name = kcu.constraint_name
                WHERE tc.table_schema = 'climasys_dev'
                    AND tc.table_name = %s
                    AND tc.constraint_type = 'PRIMARY KEY'
            """, (table_name,))
            
            result = self.cursor.fetchone()
            return result[0] if result else None
        except Exception as e:
            logger.error(f"Failed to get primary key for {table_name}: {e}")
            return None
    
    def run_migration(self, csv_directory):
        """Run the complete migration process."""
        try:
            self.connect()
            self.set_schema()
            
            # Define migration order and file mappings
            migration_files = [
                # Reference data (should already be populated)
                ('language_master.csv', 'language_master'),
                ('role_master.csv', 'role_master'),
                ('gender_master.csv', 'gender_master'),
                ('blood_group_master.csv', 'blood_group_master'),
                ('shift_master.csv', 'shift_master'),
                ('model_master.csv', 'model'),
                ('model_config_params.csv', 'model_config_params'),
                ('medicine_master.csv', 'medicine_master'),
                ('system_params.csv', 'system_params'),
                ('license_key.csv', 'license_key'),
                
                # Core data
                ('doctor_master.csv', 'doctor_master'),
                ('clinic_master.csv', 'clinic_master'),
                ('user_master.csv', 'user_master'),
                ('user_role.csv', 'user_role'),
                ('patient_master.csv', 'patient_master'),
                ('patient_visits.csv', 'patient_visits'),
                ('doctor_clinic_shift.csv', 'doctor_clinic_shift'),
                ('visit_prescription_overwrite.csv', 'visit_prescription_overwrite'),
                ('doctor_model.csv', 'doctor_model'),
            ]
            
            success_count = 0
            total_count = len(migration_files)
            
            for csv_file, table_name in migration_files:
                csv_path = os.path.join(csv_directory, csv_file)
                logger.info(f"Processing {csv_file} -> {table_name}")
                
                if self.import_csv_data(csv_path, table_name):
                    success_count += 1
                else:
                    logger.warning(f"Skipped {csv_file}")
            
            logger.info(f"Migration completed: {success_count}/{total_count} files processed successfully")
            
            # Verify migration
            self.verify_migration()
            
        except Exception as e:
            logger.error(f"Migration failed: {e}")
            raise
        finally:
            self.disconnect()
    
    def verify_migration(self):
        """Verify the migration by checking record counts."""
        try:
            tables_to_check = [
                'doctor_master', 'clinic_master', 'user_master', 'user_role',
                'patient_master', 'patient_visits', 'medicine_master',
                'system_params', 'license_key'
            ]
            
            logger.info("Verifying migration...")
            for table in tables_to_check:
                self.cursor.execute(f"SELECT COUNT(*) FROM {table}")
                count = self.cursor.fetchone()[0]
                logger.info(f"{table}: {count} records")
                
        except Exception as e:
            logger.error(f"Verification failed: {e}")

def main():
    """Main function to run the migration."""
    
    # Database configuration
    db_config = {
        'host': os.getenv('DB_HOST', 'localhost'),
        'port': os.getenv('DB_PORT', '5432'),
        'database': os.getenv('DB_NAME', 'climasys_dev'),
        'user': os.getenv('DB_USER', 'postgres'),
        'password': os.getenv('DB_PASSWORD', 'root')
    }
    
    # CSV directory (where exported CSV files are located)
    csv_directory = os.getenv('CSV_DIRECTORY', './csv_data')
    
    if not os.path.exists(csv_directory):
        logger.error(f"CSV directory not found: {csv_directory}")
        logger.info("Please create the directory and place your exported CSV files there")
        return
    
    # Create migrator instance
    migrator = ClimasysMigrator(db_config)
    
    try:
        logger.info("Starting Climasys data migration...")
        migrator.run_migration(csv_directory)
        logger.info("Migration completed successfully!")
        
    except Exception as e:
        logger.error(f"Migration failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
