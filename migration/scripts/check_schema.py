#!/usr/bin/env python3
"""
Check database schema and populate reference data
"""

import psycopg2
import sys

def check_table_structure():
    """Check the structure of key tables"""
    
    db_config = {
        'host': 'localhost',
        'port': '5432',
        'database': 'climasys_dev',
        'user': 'postgres',
        'password': 'root'
    }
    
    try:
        conn = psycopg2.connect(**db_config)
        cur = conn.cursor()
        
        # Check language_master table structure
        print("=== Language Master Table Structure ===")
        cur.execute("""
            SELECT column_name, data_type, is_nullable, column_default
            FROM information_schema.columns 
            WHERE table_schema = 'climasys_dev' AND table_name = 'language_master' 
            ORDER BY ordinal_position
        """)
        for row in cur.fetchall():
            print(f"  {row[0]}: {row[1]} (nullable: {row[2]}, default: {row[3]})")
        
        # Check role_master table structure
        print("\n=== Role Master Table Structure ===")
        cur.execute("""
            SELECT column_name, data_type, is_nullable, column_default
            FROM information_schema.columns 
            WHERE table_schema = 'climasys_dev' AND table_name = 'role_master' 
            ORDER BY ordinal_position
        """)
        for row in cur.fetchall():
            print(f"  {row[0]}: {row[1]} (nullable: {row[2]}, default: {row[3]})")
        
        # Check gender_master table structure
        print("\n=== Gender Master Table Structure ===")
        cur.execute("""
            SELECT column_name, data_type, is_nullable, column_default
            FROM information_schema.columns 
            WHERE table_schema = 'climasys_dev' AND table_name = 'gender_master' 
            ORDER BY ordinal_position
        """)
        for row in cur.fetchall():
            print(f"  {row[0]}: {row[1]} (nullable: {row[2]}, default: {row[3]})")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")
        return False
    
    return True

def populate_reference_data():
    """Populate reference data based on actual table structure"""
    
    db_config = {
        'host': 'localhost',
        'port': '5432',
        'database': 'climasys_dev',
        'user': 'postgres',
        'password': 'root'
    }
    
    try:
        conn = psycopg2.connect(**db_config)
        cur = conn.cursor()
        
        # Set search path
        cur.execute("SET search_path TO climasys_dev, public;")
        
        # Insert language master data (without language_code)
        print("Inserting language master data...")
        cur.execute("""
            INSERT INTO language_master (language_name, is_active) VALUES
            ('English', true),
            ('Hindi', true),
            ('Marathi', true),
            ('Gujarati', true),
            ('Tamil', true),
            ('Telugu', true),
            ('Bengali', true),
            ('Kannada', true),
            ('Malayalam', true),
            ('Punjabi', true)
            ON CONFLICT DO NOTHING;
        """)
        
        # Insert role master data
        print("Inserting role master data...")
        cur.execute("""
            INSERT INTO role_master (role_name, role_description, is_active) VALUES
            ('Doctor', 'Primary doctor role with full access', true),
            ('Nurse', 'Nursing staff with limited access', true),
            ('Receptionist', 'Front desk and appointment management', true),
            ('Pharmacist', 'Medicine and prescription management', true),
            ('Lab Technician', 'Laboratory test management', true),
            ('Admin', 'System administration role', true),
            ('Compounder', 'Medicine compounding and dispensing', true),
            ('Accountant', 'Financial and billing management', true)
            ON CONFLICT (role_name) DO NOTHING;
        """)
        
        # Insert gender master data
        print("Inserting gender master data...")
        cur.execute("""
            INSERT INTO gender_master (gender_id, gender_name, is_active) VALUES
            ('M', 'Male', true),
            ('F', 'Female', true),
            ('O', 'Other', true)
            ON CONFLICT (gender_id) DO NOTHING;
        """)
        
        # Insert blood group master data
        print("Inserting blood group master data...")
        cur.execute("""
            INSERT INTO blood_group_master (blood_group_name, is_active) VALUES
            ('A+', true),
            ('A-', true),
            ('B+', true),
            ('B-', true),
            ('AB+', true),
            ('AB-', true),
            ('O+', true),
            ('O-', true)
            ON CONFLICT DO NOTHING;
        """)
        
        # Insert shift master data
        print("Inserting shift master data...")
        cur.execute("""
            INSERT INTO shift_master (description, shift_day, start_time, end_time, is_active) VALUES
            ('Morning Shift - Monday', 'Monday', '09:00:00', '17:00:00', true),
            ('Evening Shift - Monday', 'Monday', '17:00:00', '21:00:00', true),
            ('Morning Shift - Tuesday', 'Tuesday', '09:00:00', '17:00:00', true),
            ('Evening Shift - Tuesday', 'Tuesday', '17:00:00', '21:00:00', true),
            ('Morning Shift - Wednesday', 'Wednesday', '09:00:00', '17:00:00', true),
            ('Evening Shift - Wednesday', 'Wednesday', '17:00:00', '21:00:00', true),
            ('Morning Shift - Thursday', 'Thursday', '09:00:00', '17:00:00', true),
            ('Evening Shift - Thursday', 'Thursday', '17:00:00', '21:00:00', true),
            ('Morning Shift - Friday', 'Friday', '09:00:00', '17:00:00', true),
            ('Evening Shift - Friday', 'Friday', '17:00:00', '21:00:00', true),
            ('Morning Shift - Saturday', 'Saturday', '09:00:00', '13:00:00', true),
            ('Evening Shift - Saturday', 'Saturday', '13:00:00', '17:00:00', true),
            ('Emergency Shift - Sunday', 'Sunday', '09:00:00', '13:00:00', true)
            ON CONFLICT DO NOTHING;
        """)
        
        # Insert model master data
        print("Inserting model master data...")
        cur.execute("""
            INSERT INTO model (model_name, model_description, is_active) VALUES
            ('Basic Model', 'Basic clinic management model', true),
            ('Standard Model', 'Standard clinic management with advanced features', true),
            ('Premium Model', 'Premium clinic management with all features', true),
            ('Enterprise Model', 'Enterprise-level clinic management system', true)
            ON CONFLICT DO NOTHING;
        """)
        
        # Insert system parameters
        print("Inserting system parameters...")
        cur.execute("""
            INSERT INTO system_params (param_name, param_value, param_description, is_main_doctor) VALUES
            ('system_name', 'Climasys', 'Name of the clinic management system', false),
            ('version', '2.0', 'System version', false),
            ('timezone', 'Asia/Kolkata', 'System timezone', false),
            ('date_format', 'DD-MM-YYYY', 'Default date format', false),
            ('currency', 'INR', 'Default currency', false),
            ('max_login_attempts', '3', 'Maximum login attempts before lockout', false),
            ('session_timeout', '30', 'Session timeout in minutes', false),
            ('backup_frequency', 'daily', 'Backup frequency', false),
            ('auto_logout', 'true', 'Enable automatic logout', false),
            ('maintenance_mode', 'false', 'System maintenance mode', false)
            ON CONFLICT DO NOTHING;
        """)
        
        # Insert license key
        print("Inserting license key...")
        cur.execute("""
            INSERT INTO license_key (license_key, installation_date, valid_to, is_active) VALUES
            ('CLIMASYS-DEMO-LICENSE-2024', '2019-06-04', '2026-03-31', true)
            ON CONFLICT DO NOTHING;
        """)
        
        # Insert common medicines
        print("Inserting medicine master data...")
        cur.execute("""
            INSERT INTO medicine_master (medicine_name, generic_name, manufacturer, unit, dosage_form, strength, is_active) VALUES
            ('Paracetamol', 'Acetaminophen', 'Generic', 'Tablet', 'Tablet', '500mg', true),
            ('Ibuprofen', 'Ibuprofen', 'Generic', 'Tablet', 'Tablet', '400mg', true),
            ('Aspirin', 'Acetylsalicylic Acid', 'Generic', 'Tablet', 'Tablet', '75mg', true),
            ('Amoxicillin', 'Amoxicillin', 'Generic', 'Capsule', 'Capsule', '500mg', true),
            ('Ciprofloxacin', 'Ciprofloxacin', 'Generic', 'Tablet', 'Tablet', '500mg', true),
            ('Azithromycin', 'Azithromycin', 'Generic', 'Tablet', 'Tablet', '500mg', true),
            ('Omeprazole', 'Omeprazole', 'Generic', 'Capsule', 'Capsule', '20mg', true),
            ('Ranitidine', 'Ranitidine', 'Generic', 'Tablet', 'Tablet', '150mg', true),
            ('Vitamin D3', 'Cholecalciferol', 'Generic', 'Tablet', 'Tablet', '1000 IU', true),
            ('Vitamin B12', 'Cyanocobalamin', 'Generic', 'Tablet', 'Tablet', '1000mcg', true),
            ('Cetirizine', 'Cetirizine', 'Generic', 'Tablet', 'Tablet', '10mg', true),
            ('Loratadine', 'Loratadine', 'Generic', 'Tablet', 'Tablet', '10mg', true)
            ON CONFLICT DO NOTHING;
        """)
        
        conn.commit()
        print("✅ Reference data inserted successfully!")
        
        # Verify counts
        print("\n=== Reference Data Counts ===")
        tables = [
            'language_master', 'role_master', 'gender_master', 'blood_group_master',
            'shift_master', 'model', 'system_params', 'license_key', 'medicine_master'
        ]
        
        for table in tables:
            cur.execute(f"SELECT COUNT(*) FROM {table}")
            count = cur.fetchone()[0]
            print(f"  {table}: {count} records")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")
        return False
    
    return True

if __name__ == "__main__":
    print("🔍 Checking database schema...")
    if check_table_structure():
        print("\n📊 Populating reference data...")
        if populate_reference_data():
            print("\n🎉 Reference data population completed successfully!")
        else:
            print("\n❌ Reference data population failed!")
            sys.exit(1)
    else:
        print("\n❌ Schema check failed!")
        sys.exit(1)
