#!/usr/bin/env python3
"""
Complete Migration Template for All SQL Server Tables to PostgreSQL
This script provides a framework to migrate all ~150 tables
"""

import psycopg2
import pandas as pd
import os
import json
from datetime import datetime

def get_postgresql_connection():
    """Get PostgreSQL connection"""
    return psycopg2.connect(
        host='localhost',
        port='5432',
        database='climasys_dev',
        user='postgres',
        password='root'
    )

def create_complete_schema():
    """Create complete PostgreSQL schema for all possible tables"""
    print("🏗️ Creating complete PostgreSQL schema...")
    
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Set search path
        cur.execute("SET search_path TO climasys_dev, public;")
        
        # Complete schema creation script
        schema_sql = """
-- =====================================================
-- COMPLETE CLIMASYS POSTGRESQL SCHEMA
-- All possible tables for comprehensive migration
-- =====================================================

-- Create the climasys_dev schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS climasys_dev;

-- Set the search path to use the climasys_dev schema
SET search_path TO climasys_dev, public;

-- =====================================================
-- REFERENCE DATA TABLES
-- =====================================================

-- Language Master
CREATE TABLE IF NOT EXISTS language_master (
    id SERIAL PRIMARY KEY,
    language_id VARCHAR(10) NOT NULL UNIQUE,
    language_name VARCHAR(50) NOT NULL,
    language_code VARCHAR(10),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Role Master
CREATE TABLE IF NOT EXISTS role_master (
    id SERIAL PRIMARY KEY,
    role_id VARCHAR(10) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    role_description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Gender Master
CREATE TABLE IF NOT EXISTS gender_master (
    id SERIAL PRIMARY KEY,
    gender_id VARCHAR(1) NOT NULL UNIQUE,
    gender_name VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Blood Group Master
CREATE TABLE IF NOT EXISTS blood_group_master (
    id SERIAL PRIMARY KEY,
    blood_group_id VARCHAR(10) NOT NULL UNIQUE,
    blood_group_name VARCHAR(10) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CORE MASTER TABLES
-- =====================================================

-- Doctor Master
CREATE TABLE IF NOT EXISTS doctor_master (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    qualification VARCHAR(200),
    specialization VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clinic Master
CREATE TABLE IF NOT EXISTS clinic_master (
    id SERIAL PRIMARY KEY,
    clinic_id VARCHAR(30) NOT NULL UNIQUE,
    clinic_name VARCHAR(200) NOT NULL,
    address TEXT,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Master
CREATE TABLE IF NOT EXISTS user_master (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL UNIQUE,
    username VARCHAR(60) NOT NULL,
    password_hash VARCHAR(100),
    user_role VARCHAR(50),
    email VARCHAR(100),
    clinic_id VARCHAR(30),
    doctor_id VARCHAR(30),
    language_id VARCHAR(10),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Role
CREATE TABLE IF NOT EXISTS user_role (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(30),
    clinic_id VARCHAR(30),
    role_id VARCHAR(10),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- PATIENT MANAGEMENT TABLES
-- =====================================================

-- Patient Master
CREATE TABLE IF NOT EXISTS patient_master (
    id SERIAL PRIMARY KEY,
    patient_id VARCHAR(30) NOT NULL UNIQUE,
    doctor_id VARCHAR(30),
    first_name VARCHAR(100),
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    gender_id VARCHAR(1),
    date_of_birth DATE,
    age INTEGER,
    blood_group_id VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    emergency_contact VARCHAR(20),
    emergency_contact_name VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patient Visits
CREATE TABLE IF NOT EXISTS patient_visits (
    id SERIAL PRIMARY KEY,
    visit_id VARCHAR(30) NOT NULL UNIQUE,
    patient_id VARCHAR(30),
    doctor_id VARCHAR(30),
    clinic_id VARCHAR(30),
    visit_date DATE,
    visit_time TIME,
    chief_complaint TEXT,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    follow_up_date DATE,
    attended_by_id VARCHAR(30),
    delete_flag BOOLEAN DEFAULT false,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- MEDICINE AND PRESCRIPTION TABLES
-- =====================================================

-- Medicine Master
CREATE TABLE IF NOT EXISTS medicine_master (
    medicine_id SERIAL PRIMARY KEY,
    medicine_name VARCHAR(200) NOT NULL,
    generic_name VARCHAR(200),
    manufacturer VARCHAR(200),
    unit VARCHAR(50),
    dosage_form VARCHAR(100),
    strength VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Prescription Master
CREATE TABLE IF NOT EXISTS prescription_master (
    id SERIAL PRIMARY KEY,
    prescription_id VARCHAR(30) NOT NULL UNIQUE,
    doctor_id VARCHAR(30),
    prescription_name VARCHAR(200),
    prescription_type VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Visit Prescription Overwrite
CREATE TABLE IF NOT EXISTS visit_prescription_overwrite (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30),
    patient_id VARCHAR(30),
    visit_id VARCHAR(30),
    prescription_data TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- SHIFT AND SCHEDULING TABLES
-- =====================================================

-- Shift Master
CREATE TABLE IF NOT EXISTS shift_master (
    id SERIAL PRIMARY KEY,
    shift_id VARCHAR(10) NOT NULL UNIQUE,
    shift_name VARCHAR(200) NOT NULL,
    shift_description TEXT,
    shift_day VARCHAR(20),
    start_time TIME,
    end_time TIME,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Doctor Clinic Shift
CREATE TABLE IF NOT EXISTS doctor_clinic_shift (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30),
    clinic_id VARCHAR(30),
    shift_id VARCHAR(10),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- SYSTEM CONFIGURATION TABLES
-- =====================================================

-- System Parameters
CREATE TABLE IF NOT EXISTS system_params (
    id SERIAL PRIMARY KEY,
    param_id VARCHAR(30) NOT NULL UNIQUE,
    param_name VARCHAR(100) NOT NULL,
    param_value TEXT,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- License Key
CREATE TABLE IF NOT EXISTS license_key (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30),
    clinic_id VARCHAR(30),
    installation_date DATE,
    valid_to DATE,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Model
CREATE TABLE IF NOT EXISTS model (
    id SERIAL PRIMARY KEY,
    model_id VARCHAR(10) NOT NULL UNIQUE,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Doctor Model
CREATE TABLE IF NOT EXISTS doctor_model (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(30),
    model_id VARCHAR(10),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Model Config Params
CREATE TABLE IF NOT EXISTS model_config_params (
    config_id SERIAL PRIMARY KEY,
    model_id VARCHAR(10),
    param_name VARCHAR(100),
    param_value TEXT,
    is_enabled BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- BILLING AND FINANCIAL TABLES
-- =====================================================

-- Billing Master
CREATE TABLE IF NOT EXISTS billing_master (
    id SERIAL PRIMARY KEY,
    billing_id VARCHAR(30) NOT NULL UNIQUE,
    patient_id VARCHAR(30),
    visit_id VARCHAR(30),
    doctor_id VARCHAR(30),
    total_amount DECIMAL(10,2),
    paid_amount DECIMAL(10,2),
    balance_amount DECIMAL(10,2),
    billing_date DATE,
    payment_status VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Billing Details
CREATE TABLE IF NOT EXISTS billing_details (
    id SERIAL PRIMARY KEY,
    billing_detail_id VARCHAR(30) NOT NULL UNIQUE,
    billing_id VARCHAR(30),
    service_type VARCHAR(100),
    service_name VARCHAR(200),
    quantity INTEGER,
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- LAB AND INVESTIGATION TABLES
-- =====================================================

-- Lab Test Master
CREATE TABLE IF NOT EXISTS lab_test_master (
    id SERIAL PRIMARY KEY,
    test_id VARCHAR(30) NOT NULL UNIQUE,
    doctor_id VARCHAR(30),
    test_name VARCHAR(200),
    test_type VARCHAR(100),
    test_description TEXT,
    normal_range VARCHAR(100),
    unit VARCHAR(50),
    price DECIMAL(10,2),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patient Lab Tests
CREATE TABLE IF NOT EXISTS patient_lab_tests (
    id SERIAL PRIMARY KEY,
    patient_id VARCHAR(30),
    visit_id VARCHAR(30),
    test_id VARCHAR(30),
    test_date DATE,
    test_result TEXT,
    test_status VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- INSURANCE TABLES
-- =====================================================

-- Insurance Company Master
CREATE TABLE IF NOT EXISTS insurance_company_master (
    id SERIAL PRIMARY KEY,
    company_id VARCHAR(30) NOT NULL UNIQUE,
    company_name VARCHAR(200),
    company_code VARCHAR(50),
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patient Insurance
CREATE TABLE IF NOT EXISTS patient_insurance (
    id SERIAL PRIMARY KEY,
    patient_id VARCHAR(30),
    company_id VARCHAR(30),
    policy_number VARCHAR(100),
    policy_type VARCHAR(100),
    expiry_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- APPOINTMENT TABLES
-- =====================================================

-- Appointment Master
CREATE TABLE IF NOT EXISTS appointment_master (
    id SERIAL PRIMARY KEY,
    appointment_id VARCHAR(30) NOT NULL UNIQUE,
    patient_id VARCHAR(30),
    doctor_id VARCHAR(30),
    clinic_id VARCHAR(30),
    appointment_date DATE,
    appointment_time TIME,
    appointment_type VARCHAR(100),
    status VARCHAR(50),
    notes TEXT,
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- ADDITIONAL TABLES (Add more as needed)
-- =====================================================

-- Family Relationship Master
CREATE TABLE IF NOT EXISTS family_relationship_master (
    id SERIAL PRIMARY KEY,
    relationship_id VARCHAR(10) NOT NULL UNIQUE,
    relationship_name VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patient Family
CREATE TABLE IF NOT EXISTS patient_family (
    id SERIAL PRIMARY KEY,
    patient_id VARCHAR(30),
    family_member_name VARCHAR(100),
    relationship_id VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_doctor_master_doctor_id ON doctor_master(doctor_id);
CREATE INDEX IF NOT EXISTS idx_patient_master_patient_id ON patient_master(patient_id);
CREATE INDEX IF NOT EXISTS idx_patient_visits_patient_id ON patient_visits(patient_id);
CREATE INDEX IF NOT EXISTS idx_patient_visits_visit_date ON patient_visits(visit_date);
CREATE INDEX IF NOT EXISTS idx_user_master_user_id ON user_master(user_id);
CREATE INDEX IF NOT EXISTS idx_billing_master_patient_id ON billing_master(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointment_master_patient_id ON appointment_master(patient_id);

-- =====================================================
-- COMMENTS
-- =====================================================

COMMENT ON SCHEMA climasys_dev IS 'Complete Climasys application database schema';
COMMENT ON TABLE doctor_master IS 'Master table for doctors';
COMMENT ON TABLE patient_master IS 'Master table for patients';
COMMENT ON TABLE patient_visits IS 'Patient visit records';
COMMENT ON TABLE user_master IS 'System users';
COMMENT ON TABLE billing_master IS 'Billing information';
COMMENT ON TABLE appointment_master IS 'Appointment scheduling';
"""
        
        # Execute the schema creation
        cur.execute(schema_sql)
        conn.commit()
        
        cur.close()
        conn.close()
        
        print("✅ Complete PostgreSQL schema created successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Error creating schema: {e}")
        return False

def create_comprehensive_extraction_queries():
    """Create comprehensive extraction queries for all tables"""
    print("📝 Creating comprehensive extraction queries...")
    
    extraction_queries = """
-- =====================================================
-- COMPLETE SQL SERVER DATA EXTRACTION QUERIES
-- For all ~150 tables in Climasys database
-- =====================================================

-- =====================================================
-- REFERENCE DATA EXTRACTION
-- =====================================================

-- Language Master
SELECT 
    Language_ID,
    Language_Name,
    Language_Code,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Language_Master]
WHERE Is_Active = 1
ORDER BY Language_ID;

-- Role Master
SELECT 
    Role_ID,
    Role_Name,
    Role_Description,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Role_Master]
WHERE Is_Active = 1
ORDER BY Role_ID;

-- Gender Master
SELECT 
    Gender_ID,
    Gender_Name,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active
FROM [Climasys-00010].[dbo].[Gender_Master]
WHERE Is_Active = 1
ORDER BY Gender_ID;

-- Blood Group Master
SELECT 
    Blood_Group_ID,
    Blood_Group_Name,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active
FROM [Climasys-00010].[dbo].[Blood_Group_Master]
WHERE Is_Active = 1
ORDER BY Blood_Group_ID;

-- =====================================================
-- CORE MASTER TABLES
-- =====================================================

-- Doctor Master
SELECT 
    Doctor_ID,
    First_Name,
    Middle_Name,
    Last_Name,
    Qualification,
    Specialization,
    Phone,
    Email,
    Address,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Doctor_Master]
WHERE Is_Active = 1
ORDER BY Doctor_ID;

-- Clinic Master
SELECT 
    Clinic_ID,
    Clinic_Name,
    Clinic_Address,
    Clinic_Phone,
    Clinic_Email,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Clinic_Master]
WHERE Is_Active = 1
ORDER BY Clinic_ID;

-- User Master
SELECT 
    ID,
    Doctor_ID,
    Login_Id,
    Password,
    First_Name,
    Middle_Name,
    Last_Name,
    Language_Id,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[User_Master]
WHERE Is_Active = 1
ORDER BY ID;

-- User Role
SELECT 
    UR.ID,
    UR.User_ID,
    UR.Role_Id,
    UR.Clinic_ID,
    CASE WHEN UR.Is_Default_Clinic = 1 THEN 'true' ELSE 'false' END as Is_Default_Clinic,
    CASE WHEN UR.Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    UR.Created_On,
    UR.Updated_On
FROM [Climasys-00010].[dbo].[User_Role] UR
INNER JOIN [Climasys-00010].[dbo].[User_Master] UM ON UR.User_ID = UM.ID
WHERE UR.Is_Active = 1 AND UM.Is_Active = 1
ORDER BY UR.ID;

-- Patient Master
SELECT 
    ID,
    Doctor_ID,
    Patient_ID,
    First_Name,
    Middle_Name,
    Last_Name,
    Gender_ID,
    Date_Of_Birth,
    Age,
    Blood_Group_ID,
    Phone,
    Email,
    Address,
    Emergency_Contact,
    Emergency_Contact_Name,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Master]
WHERE Is_Active = 1
ORDER BY ID;

-- Patient Visits
SELECT 
    ID,
    Visit_ID,
    Patient_ID,
    Doctor_ID,
    Clinic_ID,
    Visit_Date,
    Visit_Time,
    Chief_Complaint,
    Diagnosis,
    Treatment,
    Prescription,
    Follow_Up_Date,
    Attended_By_ID,
    CASE WHEN Delete_Flag = 0 THEN 'false' ELSE 'true' END as Delete_Flag,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Visits]
WHERE Delete_Flag = 0
ORDER BY ID;

-- =====================================================
-- MEDICINE AND PRESCRIPTION TABLES
-- =====================================================

-- Medicine Master
SELECT 
    Medicine_ID,
    Medicine_Name,
    Generic_Name,
    Manufacturer,
    Unit,
    Dosage_Form,
    Strength,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On
FROM [Climasys-00010].[dbo].[Medicine_Master]
WHERE Is_Active = 1
ORDER BY Medicine_ID;

-- Prescription Master
SELECT 
    Prescription_ID,
    Doctor_ID,
    Prescription_Name,
    Prescription_Type,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Prescription_Master]
WHERE Is_Active = 1
ORDER BY Prescription_ID;

-- Visit Prescription Overwrite
SELECT 
    ID,
    Visit_ID,
    Patient_ID,
    Doctor_ID,
    Medicine_ID,
    Medicine_Name,
    Dosage,
    Frequency,
    Duration,
    Instructions,
    Created_On
FROM [Climasys-00010].[dbo].[Visit_Prescription_Overwrite]
ORDER BY ID;

-- =====================================================
-- BILLING AND FINANCIAL TABLES
-- =====================================================

-- Billing Master
SELECT 
    Billing_ID,
    Patient_ID,
    Visit_ID,
    Doctor_ID,
    Total_Amount,
    Paid_Amount,
    Balance_Amount,
    Billing_Date,
    Payment_Status,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Billing_Master]
WHERE Is_Active = 1
ORDER BY Billing_ID;

-- Billing Details
SELECT 
    Billing_Detail_ID,
    Billing_ID,
    Service_Type,
    Service_Name,
    Quantity,
    Unit_Price,
    Total_Price,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Billing_Details]
WHERE Is_Active = 1
ORDER BY Billing_Detail_ID;

-- =====================================================
-- LAB AND INVESTIGATION TABLES
-- =====================================================

-- Lab Test Master
SELECT 
    Test_ID,
    Doctor_ID,
    Test_Name,
    Test_Type,
    Test_Description,
    Normal_Range,
    Unit,
    Price,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Lab_Test_Master]
WHERE Is_Active = 1
ORDER BY Test_ID;

-- Patient Lab Tests
SELECT 
    ID,
    Patient_ID,
    Visit_ID,
    Test_ID,
    Test_Date,
    Test_Result,
    Test_Status,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Lab_Tests]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- INSURANCE TABLES
-- =====================================================

-- Insurance Company Master
SELECT 
    Company_ID,
    Company_Name,
    Company_Code,
    Contact_Person,
    Phone,
    Email,
    Address,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Insurance_Company_Master]
WHERE Is_Active = 1
ORDER BY Company_ID;

-- Patient Insurance
SELECT 
    ID,
    Patient_ID,
    Company_ID,
    Policy_Number,
    Policy_Type,
    Expiry_Date,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Insurance]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- APPOINTMENT TABLES
-- =====================================================

-- Appointment Master
SELECT 
    Appointment_ID,
    Patient_ID,
    Doctor_ID,
    Clinic_ID,
    Appointment_Date,
    Appointment_Time,
    Appointment_Type,
    Status,
    Notes,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Appointment_Master]
WHERE Is_Active = 1
ORDER BY Appointment_ID;

-- =====================================================
-- ADDITIONAL TABLES (Add more as needed)
-- =====================================================

-- Family Relationship Master
SELECT 
    Relationship_ID,
    Relationship_Name,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On
FROM [Climasys-00010].[dbo].[Family_Relationship_Master]
WHERE Is_Active = 1
ORDER BY Relationship_ID;

-- Patient Family
SELECT 
    ID,
    Patient_ID,
    Family_Member_Name,
    Relationship_ID,
    Phone,
    Email,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Family]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- EXPORT INSTRUCTIONS
-- =====================================================

/*
EXPORT INSTRUCTIONS FOR ALL TABLES:

1. Run each query above separately in SQL Server Management Studio
2. Export results to CSV files with these exact names:
   - language_master.csv
   - role_master.csv
   - gender_master.csv
   - blood_group_master.csv
   - doctor_master.csv
   - clinic_master.csv
   - user_master.csv
   - user_role.csv
   - patient_master.csv
   - patient_visits.csv
   - medicine_master.csv
   - prescription_master.csv
   - visit_prescription_overwrite.csv
   - billing_master.csv
   - billing_details.csv
   - lab_test_master.csv
   - patient_lab_tests.csv
   - insurance_company_master.csv
   - patient_insurance.csv
   - appointment_master.csv
   - family_relationship_master.csv
   - patient_family.csv

3. Place all CSV files in: migration/scripts/csv_data/

4. Run the PostgreSQL import script to complete the migration

NOTES:
- Some tables may not exist in your SQL Server database
- Skip queries for tables that don't exist
- Ensure CSV files have headers
- Check for special characters that might need escaping
- Verify data types match the PostgreSQL schema
- For tables with ~150 total, you may need to add more extraction queries
*/
"""
    
    # Save extraction queries to file
    with open('complete_extraction_queries.sql', 'w', encoding='utf-8') as f:
        f.write(extraction_queries)
    
    print("✅ Complete extraction queries created!")
    return True

def create_comprehensive_import_script():
    """Create comprehensive import script for all tables"""
    print("📥 Creating comprehensive import script...")
    
    import_script = '''#!/usr/bin/env python3
"""
Comprehensive Data Import Script for All Tables
"""

import psycopg2
import pandas as pd
import os
from datetime import datetime

def get_postgresql_connection():
    """Get PostgreSQL connection"""
    return psycopg2.connect(
        host='localhost',
        port='5432',
        database='climasys_dev',
        user='postgres',
        password='root'
    )

def import_table_data(table_name, csv_file, column_mapping=None):
    """Generic function to import data for any table"""
    print(f"Importing {table_name}...")
    
    try:
        if not os.path.exists(csv_file):
            print(f"  ⚠️  CSV file not found: {csv_file}")
            return False
        
        df = pd.read_csv(csv_file, low_memory=False)
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        imported_count = 0
        for _, row in df.iterrows():
            try:
                # Build dynamic INSERT query based on CSV columns
                columns = list(df.columns)
                placeholders = ['%s'] * len(columns)
                
                query = f"""
                    INSERT INTO climasys_dev.{table_name} ({', '.join(columns)})
                    VALUES ({', '.join(placeholders)})
                    ON CONFLICT DO NOTHING
                """
                
                values = [row[col] for col in columns]
                cur.execute(query, values)
                imported_count += 1
                
                if imported_count % 1000 == 0:
                    print(f"  Imported {imported_count} records...")
                    conn.commit()
                    
            except Exception as e:
                print(f"  Warning: Skipping record: {e}")
                continue
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {imported_count} {table_name} records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing {table_name}: {e}")
        return False

def main():
    """Main import function"""
    print("🚀 Starting comprehensive data import...")
    
    # Define all tables and their CSV files
    tables_to_import = [
        ('language_master', 'csv_data/language_master.csv'),
        ('role_master', 'csv_data/role_master.csv'),
        ('gender_master', 'csv_data/gender_master.csv'),
        ('blood_group_master', 'csv_data/blood_group_master.csv'),
        ('doctor_master', 'csv_data/doctor_master.csv'),
        ('clinic_master', 'csv_data/clinic_master.csv'),
        ('user_master', 'csv_data/user_master.csv'),
        ('user_role', 'csv_data/user_role.csv'),
        ('patient_master', 'csv_data/patient_master.csv'),
        ('patient_visits', 'csv_data/patient_visits.csv'),
        ('medicine_master', 'csv_data/medicine_master.csv'),
        ('prescription_master', 'csv_data/prescription_master.csv'),
        ('visit_prescription_overwrite', 'csv_data/visit_prescription_overwrite.csv'),
        ('billing_master', 'csv_data/billing_master.csv'),
        ('billing_details', 'csv_data/billing_details.csv'),
        ('lab_test_master', 'csv_data/lab_test_master.csv'),
        ('patient_lab_tests', 'csv_data/patient_lab_tests.csv'),
        ('insurance_company_master', 'csv_data/insurance_company_master.csv'),
        ('patient_insurance', 'csv_data/patient_insurance.csv'),
        ('appointment_master', 'csv_data/appointment_master.csv'),
        ('family_relationship_master', 'csv_data/family_relationship_master.csv'),
        ('patient_family', 'csv_data/patient_family.csv'),
    ]
    
    success_count = 0
    for table_name, csv_file in tables_to_import:
        if import_table_data(table_name, csv_file):
            success_count += 1
    
    print(f"\\n📊 Import Summary: {success_count}/{len(tables_to_import)} tables imported successfully")
    
    if success_count == len(tables_to_import):
        print("🎉 All data imported successfully!")
    else:
        print("⚠️  Some imports failed. Check the error messages above.")

if __name__ == "__main__":
    main()
'''
    
    # Save import script to file
    with open('import_all_tables.py', 'w', encoding='utf-8') as f:
        f.write(import_script)
    
    print("✅ Comprehensive import script created!")
    return True

def main():
    """Main function"""
    print("🚀 Complete Migration Template for All Tables")
    print("=" * 60)
    
    # Step 1: Create complete schema
    if create_complete_schema():
        print("✅ Step 1: Schema creation completed")
    else:
        print("❌ Step 1: Schema creation failed")
        return
    
    # Step 2: Create extraction queries
    if create_comprehensive_extraction_queries():
        print("✅ Step 2: Extraction queries created")
    else:
        print("❌ Step 2: Extraction queries creation failed")
        return
    
    # Step 3: Create import script
    if create_comprehensive_import_script():
        print("✅ Step 3: Import script created")
    else:
        print("❌ Step 3: Import script creation failed")
        return
    
    print("\n🎉 Complete migration template created successfully!")
    print("\n📁 Files created:")
    print("  - complete_postgresql_schema.sql (in database)")
    print("  - complete_extraction_queries.sql")
    print("  - import_all_tables.py")
    
    print("\n🎯 Next steps:")
    print("1. Run the extraction queries on your SQL Server")
    print("2. Export results to CSV files in csv_data/ directory")
    print("3. Run: python import_all_tables.py")
    print("4. Verify data migration")
    
    print("\n💡 For ~150 tables:")
    print("- Add more table definitions to the schema script")
    print("- Add more extraction queries")
    print("- Update the import script with all table names")

if __name__ == "__main__":
    main()
