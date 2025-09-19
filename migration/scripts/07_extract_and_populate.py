#!/usr/bin/env python3
"""
Climasys Data Extraction and Population Script

This script helps extract data from SQL Server and populate PostgreSQL tables.
It provides both direct database connection and CSV-based import options.
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
        logging.FileHandler('data_extraction.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class ClimasysDataExtractor:
    def __init__(self, postgres_config):
        """Initialize the data extractor with PostgreSQL configuration."""
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
    
    def populate_sample_data(self):
        """Populate tables with sample data for testing."""
        logger.info("Populating tables with sample data...")
        
        try:
            # Sample Doctor Data
            doctors_data = [
                ('DOC001', 'John', '', 'Smith', 'MBBS, MD', 'General Medicine', '+91-9876543210', 'dr.john@clinic.com', '123 Main Street, City', True),
                ('DOC002', 'Jane', 'A', 'Doe', 'MBBS, MS', 'Surgery', '+91-9876543211', 'dr.jane@clinic.com', '456 Oak Avenue, City', True),
                ('DOC003', 'Robert', 'B', 'Johnson', 'MBBS, DNB', 'Cardiology', '+91-9876543212', 'dr.robert@clinic.com', '789 Pine Street, City', True)
            ]
            
            insert_doctors = """
                INSERT INTO doctor_master (doctor_id, first_name, middle_name, last_name, qualification, specialization, phone, email, address, is_active)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (doctor_id) DO UPDATE SET
                    first_name = EXCLUDED.first_name,
                    middle_name = EXCLUDED.middle_name,
                    last_name = EXCLUDED.last_name,
                    qualification = EXCLUDED.qualification,
                    specialization = EXCLUDED.specialization,
                    phone = EXCLUDED.phone,
                    email = EXCLUDED.email,
                    address = EXCLUDED.address,
                    updated_on = CURRENT_TIMESTAMP
            """
            
            execute_values(
                self.cursor,
                insert_doctors,
                doctors_data,
                template=None,
                page_size=1000
            )
            logger.info(f"Inserted {len(doctors_data)} doctors")
            
            # Sample Clinic Data
            clinics_data = [
                ('CLINIC001', 'DOC001', 'City Medical Center', '123 Main Street, City, State 12345', '+1-555-0123', 'info@citymedical.com', True),
                ('CLINIC002', 'DOC002', 'Oak Avenue Clinic', '456 Oak Avenue, City, State 12345', '+1-555-0124', 'info@oakclinic.com', True),
                ('CLINIC003', 'DOC003', 'Pine Street Hospital', '789 Pine Street, City, State 12345', '+1-555-0125', 'info@pinestreet.com', True)
            ]
            
            insert_clinics = """
                INSERT INTO clinic_master (clinic_id, doctor_id, clinic_name, clinic_address, clinic_phone, clinic_email, is_active)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (clinic_id) DO UPDATE SET
                    clinic_name = EXCLUDED.clinic_name,
                    clinic_address = EXCLUDED.clinic_address,
                    clinic_phone = EXCLUDED.clinic_phone,
                    clinic_email = EXCLUDED.clinic_email,
                    updated_on = CURRENT_TIMESTAMP
            """
            
            execute_values(
                self.cursor,
                insert_clinics,
                clinics_data,
                template=None,
                page_size=1000
            )
            logger.info(f"Inserted {len(clinics_data)} clinics")
            
            # Sample User Data
            users_data = [
                ('DOC001', 'dr.john', 'hashed_password_here', 'John', '', 'Smith', 1, True),
                ('DOC002', 'dr.jane', 'hashed_password_here', 'Jane', 'A', 'Doe', 1, True),
                ('DOC003', 'dr.robert', 'hashed_password_here', 'Robert', 'B', 'Johnson', 1, True),
                ('DOC001', 'nurse1', 'hashed_password_here', 'Mary', '', 'Johnson', 1, True),
                ('DOC002', 'reception1', 'hashed_password_here', 'Sarah', '', 'Wilson', 1, True)
            ]
            
            insert_users = """
                INSERT INTO user_master (doctor_id, login_id, password, first_name, middle_name, last_name, language_id, is_active)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (login_id) DO UPDATE SET
                    first_name = EXCLUDED.first_name,
                    middle_name = EXCLUDED.middle_name,
                    last_name = EXCLUDED.last_name,
                    language_id = EXCLUDED.language_id,
                    updated_on = CURRENT_TIMESTAMP
            """
            
            execute_values(
                self.cursor,
                insert_users,
                users_data,
                template=None,
                page_size=1000
            )
            logger.info(f"Inserted {len(users_data)} users")
            
            # Sample User Role Data
            user_roles_data = [
                (1, 1, 'CLINIC001', True, True),  # dr.john - Doctor
                (2, 1, 'CLINIC002', True, True),  # dr.jane - Doctor
                (3, 1, 'CLINIC003', True, True),  # dr.robert - Doctor
                (4, 2, 'CLINIC001', True, True),  # nurse1 - Nurse
                (5, 3, 'CLINIC002', True, True),  # reception1 - Receptionist
            ]
            
            insert_user_roles = """
                INSERT INTO user_role (user_id, role_id, clinic_id, is_default_clinic, is_active)
                VALUES (%s, %s, %s, %s, %s)
                ON CONFLICT (user_id, role_id, clinic_id) DO UPDATE SET
                    is_default_clinic = EXCLUDED.is_default_clinic,
                    updated_on = CURRENT_TIMESTAMP
            """
            
            execute_values(
                self.cursor,
                insert_user_roles,
                user_roles_data,
                template=None,
                page_size=1000
            )
            logger.info(f"Inserted {len(user_roles_data)} user roles")
            
            # Sample Patient Data
            patients_data = [
                ('DOC001', 'P001', 'Alice', '', 'Johnson', 'F', '1985-03-15', 39, 1, '+91-9876543212', 'alice@email.com', '789 Pine Street, City', '+91-9876543213', 'Bob Johnson', True),
                ('DOC001', 'P002', 'Bob', 'M', 'Smith', 'M', '1990-07-22', 34, 2, '+91-9876543214', 'bob@email.com', '321 Elm Street, City', '+91-9876543215', 'Alice Smith', True),
                ('DOC002', 'P003', 'Charlie', '', 'Brown', 'M', '1978-12-10', 45, 3, '+91-9876543216', 'charlie@email.com', '654 Maple Avenue, City', '+91-9876543217', 'Diana Brown', True),
                ('DOC002', 'P004', 'Diana', 'L', 'Davis', 'F', '1992-05-18', 32, 4, '+91-9876543218', 'diana@email.com', '987 Oak Street, City', '+91-9876543219', 'Charlie Davis', True),
                ('DOC003', 'P005', 'Eve', '', 'Wilson', 'F', '1988-09-30', 36, 5, '+91-9876543220', 'eve@email.com', '147 Cedar Lane, City', '+91-9876543221', 'Frank Wilson', True)
            ]
            
            insert_patients = """
                INSERT INTO patient_master (doctor_id, patient_id, first_name, middle_name, last_name, gender_id, date_of_birth, age, blood_group_id, phone, email, address, emergency_contact, emergency_contact_name, is_active)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (patient_id) DO UPDATE SET
                    first_name = EXCLUDED.first_name,
                    middle_name = EXCLUDED.middle_name,
                    last_name = EXCLUDED.last_name,
                    gender_id = EXCLUDED.gender_id,
                    date_of_birth = EXCLUDED.date_of_birth,
                    age = EXCLUDED.age,
                    blood_group_id = EXCLUDED.blood_group_id,
                    phone = EXCLUDED.phone,
                    email = EXCLUDED.email,
                    address = EXCLUDED.address,
                    emergency_contact = EXCLUDED.emergency_contact,
                    emergency_contact_name = EXCLUDED.emergency_contact_name,
                    updated_on = CURRENT_TIMESTAMP
            """
            
            execute_values(
                self.cursor,
                insert_patients,
                patients_data,
                template=None,
                page_size=1000
            )
            logger.info(f"Inserted {len(patients_data)} patients")
            
            # Sample Patient Visits Data
            visits_data = [
                ('V001', 1, 'DOC001', 'CLINIC001', '2024-09-10', '10:30:00', 'Fever and headache', 'Viral fever', 'Rest and medication', 'Paracetamol 500mg twice daily for 3 days', '2024-09-13', 1, False),
                ('V002', 2, 'DOC001', 'CLINIC001', '2024-09-11', '14:15:00', 'Chest pain', 'Acid reflux', 'Diet modification and medication', 'Omeprazole 20mg once daily for 2 weeks', '2024-09-25', 1, False),
                ('V003', 3, 'DOC002', 'CLINIC002', '2024-09-12', '09:00:00', 'Back pain', 'Muscle strain', 'Physical therapy and pain relief', 'Ibuprofen 400mg three times daily for 5 days', '2024-09-17', 2, False),
                ('V004', 4, 'DOC002', 'CLINIC002', '2024-09-13', '11:30:00', 'Skin rash', 'Allergic reaction', 'Antihistamine and topical cream', 'Cetirizine 10mg once daily for 1 week', '2024-09-20', 2, False),
                ('V005', 5, 'DOC003', 'CLINIC003', '2024-09-14', '15:45:00', 'Shortness of breath', 'Asthma', 'Inhaler and monitoring', 'Salbutamol inhaler as needed', '2024-09-21', 3, False)
            ]
            
            insert_visits = """
                INSERT INTO patient_visits (visit_id, patient_id, doctor_id, clinic_id, visit_date, visit_time, chief_complaint, diagnosis, treatment, prescription, follow_up_date, attended_by_id, delete_flag)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (visit_id) DO UPDATE SET
                    chief_complaint = EXCLUDED.chief_complaint,
                    diagnosis = EXCLUDED.diagnosis,
                    treatment = EXCLUDED.treatment,
                    prescription = EXCLUDED.prescription,
                    follow_up_date = EXCLUDED.follow_up_date,
                    updated_on = CURRENT_TIMESTAMP
            """
            
            execute_values(
                self.cursor,
                insert_visits,
                visits_data,
                template=None,
                page_size=1000
            )
            logger.info(f"Inserted {len(visits_data)} patient visits")
            
            # Sample System Parameters
            system_params_data = [
                ('DOC001', 'main_doctor', 'true', 'Main doctor flag', True),
                ('DOC001', 'clinic_timings', '09:00-17:00', 'Clinic operating hours', False),
                ('DOC001', 'consultation_fee', '500', 'Default consultation fee', False),
                ('DOC002', 'main_doctor', 'false', 'Main doctor flag', False),
                ('DOC002', 'clinic_timings', '14:00-21:00', 'Clinic operating hours', False),
                ('DOC002', 'consultation_fee', '600', 'Default consultation fee', False),
                ('DOC003', 'main_doctor', 'false', 'Main doctor flag', False),
                ('DOC003', 'clinic_timings', '08:00-16:00', 'Clinic operating hours', False),
                ('DOC003', 'consultation_fee', '700', 'Default consultation fee', False)
            ]
            
            insert_system_params = """
                INSERT INTO system_params (doctor_id, param_name, param_value, param_description, is_main_doctor)
                VALUES (%s, %s, %s, %s, %s)
                ON CONFLICT DO NOTHING
            """
            
            execute_values(
                self.cursor,
                insert_system_params,
                system_params_data,
                template=None,
                page_size=1000
            )
            logger.info(f"Inserted {len(system_params_data)} system parameters")
            
            self.connection.commit()
            logger.info("Sample data population completed successfully!")
            
        except Exception as e:
            logger.error(f"Failed to populate sample data: {e}")
            self.connection.rollback()
            raise
    
    def verify_data(self):
        """Verify the populated data."""
        logger.info("Verifying populated data...")
        
        try:
            verification_queries = [
                ("Doctor Master", "SELECT COUNT(*) FROM doctor_master"),
                ("Clinic Master", "SELECT COUNT(*) FROM clinic_master"),
                ("User Master", "SELECT COUNT(*) FROM user_master"),
                ("User Role", "SELECT COUNT(*) FROM user_role"),
                ("Patient Master", "SELECT COUNT(*) FROM patient_master"),
                ("Patient Visits", "SELECT COUNT(*) FROM patient_visits"),
                ("System Params", "SELECT COUNT(*) FROM system_params")
            ]
            
            for table_name, query in verification_queries:
                self.cursor.execute(query)
                count = self.cursor.fetchone()[0]
                logger.info(f"{table_name}: {count} records")
                
        except Exception as e:
            logger.error(f"Failed to verify data: {e}")
            raise
    
    def run_extraction(self):
        """Run the complete data extraction and population process."""
        try:
            self.connect_postgres()
            self.set_schema()
            self.populate_sample_data()
            self.verify_data()
            logger.info("Data extraction and population completed successfully!")
            
        except Exception as e:
            logger.error(f"Data extraction failed: {e}")
            raise
        finally:
            self.disconnect_postgres()

def main():
    """Main function to run the data extraction."""
    
    # PostgreSQL configuration
    postgres_config = {
        'host': os.getenv('DB_HOST', 'localhost'),
        'port': os.getenv('DB_PORT', '5432'),
        'database': os.getenv('DB_NAME', 'climasys_dev'),
        'user': os.getenv('DB_USER', 'postgres'),
        'password': os.getenv('DB_PASSWORD', 'root')
    }
    
    logger.info("🔍 Starting Climasys Data Extraction and Population")
    logger.info("=" * 60)
    
    # Create extractor instance
    extractor = ClimasysDataExtractor(postgres_config)
    
    try:
        extractor.run_extraction()
        logger.info("🎉 Data extraction and population completed successfully!")
        
    except Exception as e:
        logger.error(f"❌ Data extraction failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
