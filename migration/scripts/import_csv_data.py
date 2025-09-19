#!/usr/bin/env python3
"""
Import CSV data to PostgreSQL database
"""

import psycopg2
import pandas as pd
import os
import sys
from datetime import datetime

def get_db_connection():
    """Get database connection"""
    return psycopg2.connect(
        host='localhost',
        port='5432',
        database='climasys_dev',
        user='postgres',
        password='root'
    )

def import_language_master():
    """Import language master data"""
    print("Importing Language Master...")
    
    try:
        df = pd.read_csv('extracted_data/Language_Master.csv')
        conn = get_db_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.language_master (language_id, language_name, created_on, modified_on)
                VALUES (%s, %s, %s, %s)
                ON CONFLICT (language_id) DO NOTHING
            """, (
                str(row['Language_Id']),
                row['Language_Name'],
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} language master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing language master: {e}")
        return False

def import_role_master():
    """Import role master data"""
    print("Importing Role Master...")
    
    try:
        df = pd.read_csv('extracted_data/Role_Master.csv')
        conn = get_db_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Parse created_on date
            created_on = None
            if pd.notna(row['Created_On']) and row['Created_On'] != 'None':
                try:
                    created_on = pd.to_datetime(row['Created_On'])
                except:
                    created_on = datetime.now()
            else:
                created_on = datetime.now()
            
            # Parse modified_on date
            modified_on = None
            if pd.notna(row['Modified_On']) and row['Modified_On'] != 'None':
                try:
                    modified_on = pd.to_datetime(row['Modified_On'])
                except:
                    modified_on = datetime.now()
            else:
                modified_on = datetime.now()
            
            cur.execute("""
                INSERT INTO climasys_dev.role_master (role_id, role_name, created_on, modified_on)
                VALUES (%s, %s, %s, %s)
                ON CONFLICT (role_id) DO NOTHING
            """, (
                str(row['Role_Id']),
                row['Role_Name'],
                created_on,
                modified_on
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} role master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing role master: {e}")
        return False

def import_gender_master():
    """Import gender master data"""
    print("Importing Gender Master...")
    
    try:
        df = pd.read_csv('extracted_data/Gender_Master.csv')
        conn = get_db_connection()
        cur = conn.cursor()
        
        # Map gender codes to names
        gender_names = {'M': 'Male', 'F': 'Female', 'O': 'Other'}
        
        for _, row in df.iterrows():
            gender_id = row['ID']
            gender_name = gender_names.get(gender_id, gender_id)
            
            cur.execute("""
                INSERT INTO climasys_dev.gender_master (gender_id, modified_on)
                VALUES (%s, %s)
                ON CONFLICT (gender_id) DO NOTHING
            """, (
                gender_id,
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} gender master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing gender master: {e}")
        return False

def import_doctor_master():
    """Import doctor master data"""
    print("Importing Doctor Master...")
    
    try:
        df = pd.read_csv('extracted_data/Doctor_Master.csv')
        conn = get_db_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.doctor_master (
                    doctor_id, first_name, middle_name, last_name, qualification, 
                    specialization, phone, email, address, is_active, created_on, updated_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (doctor_id) DO NOTHING
            """, (
                str(row.get('Doctor_Id', '')),
                row.get('First_Name', ''),
                row.get('Middle_Name', ''),
                row.get('Last_Name', ''),
                row.get('Qualification', ''),
                row.get('Specialization', ''),
                row.get('Phone', ''),
                row.get('Email', ''),
                row.get('Address', ''),
                True,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} doctor master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing doctor master: {e}")
        return False

def import_clinic_master():
    """Import clinic master data"""
    print("Importing Clinic Master...")
    
    try:
        df = pd.read_csv('extracted_data/Clinic_Master.csv')
        conn = get_db_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.clinic_master (
                    clinic_id, doctor_id, clinic_name, clinic_address, 
                    clinic_phone, clinic_email, is_active, created_on, updated_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (clinic_id) DO NOTHING
            """, (
                str(row.get('Clinic_Id', '')),
                str(row.get('Doctor_Id', '')),
                row.get('Clinic_Name', ''),
                row.get('Clinic_Address', ''),
                row.get('Clinic_Phone', ''),
                row.get('Clinic_Email', ''),
                True,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} clinic master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing clinic master: {e}")
        return False

def import_user_master():
    """Import user master data"""
    print("Importing User Master...")
    
    try:
        df = pd.read_csv('extracted_data/User_Master.csv')
        conn = get_db_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.user_master (
                    doctor_id, login_id, password, first_name, middle_name, 
                    last_name, language_id, is_active, created_on, updated_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (login_id) DO NOTHING
            """, (
                str(row.get('Doctor_Id', '')),
                row.get('Login_Id', ''),
                row.get('Password', ''),
                row.get('First_Name', ''),
                row.get('Middle_Name', ''),
                row.get('Last_Name', ''),
                1,  # Default to English language
                True,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} user master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing user master: {e}")
        return False

def import_patient_master():
    """Import patient master data"""
    print("Importing Patient Master...")
    
    try:
        df = pd.read_csv('extracted_data/Patient_Master.csv')
        conn = get_db_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Parse date of birth
            dob = None
            if pd.notna(row.get('Date_Of_Birth')):
                try:
                    dob = pd.to_datetime(row['Date_Of_Birth']).date()
                except:
                    dob = None
            
            cur.execute("""
                INSERT INTO climasys_dev.patient_master (
                    doctor_id, patient_id, first_name, middle_name, last_name,
                    gender_id, date_of_birth, age, blood_group_id, phone, email,
                    address, emergency_contact, emergency_contact_name, is_active, created_on, updated_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (patient_id) DO NOTHING
            """, (
                str(row.get('Doctor_Id', '')),
                str(row.get('Patient_Id', '')),
                row.get('First_Name', ''),
                row.get('Middle_Name', ''),
                row.get('Last_Name', ''),
                row.get('Gender_Id', ''),
                dob,
                row.get('Age', None),
                row.get('Blood_Group_Id', None),
                row.get('Phone', ''),
                row.get('Email', ''),
                row.get('Address', ''),
                row.get('Emergency_Contact', ''),
                row.get('Emergency_Contact_Name', ''),
                True,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} patient master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing patient master: {e}")
        return False

def import_system_params():
    """Import system parameters"""
    print("Importing System Parameters...")
    
    try:
        df = pd.read_csv('extracted_data/System_Params.csv')
        conn = get_db_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.system_params (
                    doctor_id, param_name, param_value, param_description, 
                    is_main_doctor, created_on, updated_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT DO NOTHING
            """, (
                str(row.get('Doctor_Id', '')),
                row.get('Param_Name', ''),
                row.get('Param_Value', ''),
                row.get('Param_Description', ''),
                row.get('Is_Main_Doctor', False),
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} system parameter records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing system parameters: {e}")
        return False

def verify_import():
    """Verify the imported data"""
    print("\n=== Verifying Imported Data ===")
    
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        tables = [
            'language_master', 'role_master', 'gender_master', 'doctor_master',
            'clinic_master', 'user_master', 'patient_master', 'system_params'
        ]
        
        for table in tables:
            cur.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cur.fetchone()[0]
            print(f"  {table}: {count} records")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"❌ Error verifying data: {e}")

def main():
    """Main import function"""
    print("🚀 Starting CSV Data Import to PostgreSQL")
    print("=" * 50)
    
    # Check if extracted_data directory exists
    if not os.path.exists('extracted_data'):
        print("❌ extracted_data directory not found!")
        return False
    
    # Import functions in order
    import_functions = [
        import_language_master,
        import_role_master,
        import_gender_master,
        import_doctor_master,
        import_clinic_master,
        import_user_master,
        import_patient_master,
        import_system_params
    ]
    
    success_count = 0
    for import_func in import_functions:
        if import_func():
            success_count += 1
    
    print(f"\n📊 Import Summary: {success_count}/{len(import_functions)} tables imported successfully")
    
    if success_count == len(import_functions):
        print("🎉 All data imported successfully!")
        verify_import()
        return True
    else:
        print("⚠️  Some imports failed. Check the error messages above.")
        return False

if __name__ == "__main__":
    if main():
        sys.exit(0)
    else:
        sys.exit(1)
