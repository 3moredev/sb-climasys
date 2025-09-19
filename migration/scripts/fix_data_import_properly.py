#!/usr/bin/env python3
"""
Fix data import with proper constraint handling
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
    """Clear all tables in proper order (respecting dependencies)"""
    print("🧹 Clearing all tables...")
    
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Clear tables in reverse dependency order
        tables_to_clear = [
            'patient_visits', 'visit_prescription_overwrite', 'user_role',
            'patient_master', 'user_master', 'clinic_master', 'doctor_master',
            'system_params', 'license_key', 'model_config_params', 'doctor_model',
            'shift_master', 'doctor_clinic_shift', 'medicine_master',
            'model', 'blood_group_master', 'gender_master', 'role_master', 'language_master',
            'billing_details', 'billing_master', 'patient_lab_tests', 'lab_test_master',
            'patient_insurance', 'insurance_company_master', 'patient_family', 
            'family_relationship_master', 'appointment_master', 'prescription_master'
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

def import_language_master():
    """Import language master data"""
    print("Importing Language Master...")
    
    try:
        df = pd.read_csv('extracted_data/Language_Master.csv')
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.language_master (language_id, language_name, created_on, modified_on)
                VALUES (%s, %s, %s, %s)
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
        conn = get_postgresql_connection()
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
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Map gender codes to names
        gender_names = {'M': 'Male', 'F': 'Female', 'O': 'Other'}
        
        for _, row in df.iterrows():
            gender_id = row['ID']
            gender_name = gender_names.get(gender_id, gender_id)
            
            cur.execute("""
                INSERT INTO climasys_dev.gender_master (gender_id, gender_name, created_on, modified_on)
                VALUES (%s, %s, %s, %s)
            """, (
                gender_id,
                gender_name,
                datetime.now(),
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

def import_clinic_master():
    """Import clinic master data"""
    print("Importing Clinic Master...")
    
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

def import_user_master():
    """Import user master data"""
    print("Importing User Master...")
    
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

def import_patient_master():
    """Import patient master data"""
    print("Importing Patient Master...")
    
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

def import_patient_visits():
    """Import patient visits data"""
    print("Importing Patient Visits...")
    
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

def import_system_params():
    """Import system parameters"""
    print("Importing System Parameters...")
    
    try:
        df = pd.read_csv('extracted_data/System_Params.csv')
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.system_params (
                    param_id, param_name, param_value, created_on, modified_on
                )
                VALUES (%s, %s, %s, %s, %s)
            """, (
                str(row.get('Param_Id', '')),
                row.get('Param_Name', ''),
                row.get('Param_Value', ''),
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

def import_user_role():
    """Import user role data"""
    print("Importing User Role...")
    
    try:
        df = pd.read_csv('extracted_data/User_Role.csv')
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.user_role (
                    user_id, clinic_id, role_id, is_active, created_on, modified_on
                )
                VALUES (%s, %s, %s, %s, %s, %s)
            """, (
                str(row.get('User_Id', '')),
                str(row.get('Clinic_Id', '')),
                str(row.get('Role_Id', '')),
                True,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {len(df)} user role records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing user role: {e}")
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
        print(f"❌ Error verifying data: {e}")

def main():
    """Main function"""
    print("🚀 Fixing Data Import with Proper Constraint Handling")
    print("=" * 60)
    
    # Step 1: Clear all tables
    if not clear_all_tables():
        return False
    
    # Step 2: Import data in proper order
    import_functions = [
        import_language_master,
        import_role_master,
        import_gender_master,
        import_doctor_master,
        import_clinic_master,
        import_user_master,
        import_patient_master,
        import_patient_visits,
        import_system_params,
        import_user_role
    ]
    
    success_count = 0
    for import_func in import_functions:
        if import_func():
            success_count += 1
    
    print(f"\n📊 Import Summary: {success_count}/{len(import_functions)} tables imported successfully")
    
    if success_count == len(import_functions):
        print("🎉 All data imported successfully!")
        verify_final_counts()
        return True
    else:
        print("⚠️  Some imports failed. Check the error messages above.")
        return False

if __name__ == "__main__":
    main()

