#!/usr/bin/env python3
"""
Fix data import issues and properly populate all tables
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

def clear_all_tables():
    """Clear all data from tables"""
    print("🧹 Clearing all existing data...")
    
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Clear tables in reverse dependency order
        tables_to_clear = [
            'patient_visits', 'visit_prescription_overwrite', 'user_role',
            'patient_master', 'user_master', 'clinic_master', 'doctor_master',
            'system_params', 'license_key', 'model_config_params', 'doctor_model',
            'shift_master', 'doctor_clinic_shift', 'medicine_master',
            'model', 'blood_group_master', 'gender_master', 'role_master', 'language_master'
        ]
        
        for table in tables_to_clear:
            cur.execute(f"DELETE FROM climasys_dev.{table}")
            print(f"  Cleared {table}")
        
        conn.commit()
        cur.close()
        conn.close()
        print("✅ All tables cleared successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Error clearing tables: {e}")
        return False

def import_patient_master_fixed():
    """Import patient master data without conflicts"""
    print("Importing Patient Master (Fixed)...")
    
    try:
        df = pd.read_csv('extracted_data/Patient_Master.csv', low_memory=False)
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        imported_count = 0
        for _, row in df.iterrows():
            try:
                # Parse date of birth
                dob = None
                if pd.notna(row.get('Date_Of_Birth')):
                    try:
                        dob = pd.to_datetime(row['Date_Of_Birth']).date()
                    except:
                        dob = None
                
                cur.execute("""
                    INSERT INTO climasys_dev.patient_master (
                        patient_id, doctor_id, first_name, middle_name, last_name,
                        gender_id, date_of_birth, age, blood_group_id, phone, email,
                        address, emergency_contact, emergency_contact_name, is_active, created_on, modified_on
                    )
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                """, (
                    str(row.get('Patient_Id', '')),
                    str(row.get('Doctor_Id', '')),
                    row.get('First_Name', ''),
                    row.get('Middle_Name', ''),
                    row.get('Last_Name', ''),
                    row.get('Gender_Id', ''),
                    dob,
                    row.get('Age', None),
                    str(row.get('Blood_Group_Id', '')) if pd.notna(row.get('Blood_Group_Id')) else None,
                    row.get('Phone', ''),
                    row.get('Email', ''),
                    row.get('Address', ''),
                    row.get('Emergency_Contact', ''),
                    row.get('Emergency_Contact_Name', ''),
                    True,
                    datetime.now(),
                    datetime.now()
                ))
                imported_count += 1
                
                if imported_count % 1000 == 0:
                    print(f"  Imported {imported_count} records...")
                    conn.commit()
                    
            except Exception as e:
                print(f"  Warning: Skipping record {row.get('Patient_Id', 'unknown')}: {e}")
                continue
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {imported_count} patient master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing patient master: {e}")
        return False

def import_patient_visits_fixed():
    """Import patient visits data without conflicts"""
    print("Importing Patient Visits (Fixed)...")
    
    try:
        df = pd.read_csv('extracted_data/Patient_Visits.csv', low_memory=False)
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        imported_count = 0
        for _, row in df.iterrows():
            try:
                # Parse visit date
                visit_date = None
                if pd.notna(row.get('Visit_Date')):
                    try:
                        visit_date = pd.to_datetime(row['Visit_Date']).date()
                    except:
                        visit_date = None
                
                # Parse visit time
                visit_time = None
                if pd.notna(row.get('Visit_Time')):
                    try:
                        visit_time = pd.to_datetime(row['Visit_Time']).time()
                    except:
                        visit_time = None
                
                # Parse follow up date
                follow_up_date = None
                if pd.notna(row.get('Follow_Up_Date')):
                    try:
                        follow_up_date = pd.to_datetime(row['Follow_Up_Date']).date()
                    except:
                        follow_up_date = None
                
                cur.execute("""
                    INSERT INTO climasys_dev.patient_visits (
                        visit_id, patient_id, doctor_id, clinic_id, visit_date, visit_time,
                        chief_complaint, diagnosis, treatment, prescription, follow_up_date,
                        attended_by_id, delete_flag, created_on, modified_on
                    )
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                """, (
                    str(row.get('Visit_Id', '')),
                    str(row.get('Patient_Id', '')),
                    str(row.get('Doctor_Id', '')),
                    str(row.get('Clinic_Id', '')),
                    visit_date,
                    visit_time,
                    row.get('Chief_Complaint', ''),
                    row.get('Diagnosis', ''),
                    row.get('Treatment', ''),
                    row.get('Prescription', ''),
                    follow_up_date,
                    str(row.get('Attended_By_Id', '')),
                    False,
                    datetime.now(),
                    datetime.now()
                ))
                imported_count += 1
                
                if imported_count % 1000 == 0:
                    print(f"  Imported {imported_count} records...")
                    conn.commit()
                    
            except Exception as e:
                print(f"  Warning: Skipping record {row.get('Visit_Id', 'unknown')}: {e}")
                continue
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {imported_count} patient visit records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing patient visits: {e}")
        return False

def import_doctor_master_fixed():
    """Import doctor master data without conflicts"""
    print("Importing Doctor Master (Fixed)...")
    
    try:
        df = pd.read_csv('extracted_data/Doctor_Master.csv')
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.doctor_master (
                    doctor_id, first_name, middle_name, last_name, qualification, 
                    specialization, phone, email, address, is_active, created_on, modified_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
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

def import_clinic_master_fixed():
    """Import clinic master data without conflicts"""
    print("Importing Clinic Master (Fixed)...")
    
    try:
        df = pd.read_csv('extracted_data/Clinic_Master.csv')
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.clinic_master (
                    clinic_id, clinic_name, address, phone_number, email, 
                    is_active, created_on, modified_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            """, (
                str(row.get('Clinic_Id', '')),
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

def import_user_master_fixed():
    """Import user master data without conflicts"""
    print("Importing User Master (Fixed)...")
    
    try:
        df = pd.read_csv('extracted_data/User_Master.csv')
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.user_master (
                    user_id, username, password_hash, user_role, email, 
                    clinic_id, doctor_id, language_id, is_active, created_on, modified_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """, (
                str(row.get('User_Id', '')),
                row.get('Username', ''),
                row.get('Password', ''),
                row.get('User_Role', ''),
                row.get('Email', ''),
                str(row.get('Clinic_Id', '')),
                str(row.get('Doctor_Id', '')),
                str(row.get('Language_Id', '1')),
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

def verify_final_counts():
    """Verify final record counts"""
    print("\n=== Final Record Counts ===")
    
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        tables = [
            'language_master', 'role_master', 'gender_master', 'doctor_master',
            'clinic_master', 'user_master', 'patient_master', 'patient_visits',
            'system_params', 'user_role'
        ]
        
        total_records = 0
        for table in tables:
            cur.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cur.fetchone()[0]
            total_records += count
            print(f"  {table}: {count:,} records")
        
        print(f"\n📊 Total Records: {total_records:,}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"❌ Error verifying counts: {e}")

def main():
    """Main function"""
    print("🔧 Fixing Data Import Issues")
    print("=" * 40)
    
    # Step 1: Clear all tables
    if not clear_all_tables():
        return False
    
    # Step 2: Import data without conflicts
    success_count = 0
    import_functions = [
        import_doctor_master_fixed,
        import_clinic_master_fixed,
        import_user_master_fixed,
        import_patient_master_fixed,
        import_patient_visits_fixed
    ]
    
    for import_func in import_functions:
        if import_func():
            success_count += 1
    
    print(f"\n📊 Import Summary: {success_count}/{len(import_functions)} tables imported successfully")
    
    if success_count == len(import_functions):
        print("🎉 Data import fixed successfully!")
        verify_final_counts()
        return True
    else:
        print("⚠️  Some imports failed.")
        return False

if __name__ == "__main__":
    main()

