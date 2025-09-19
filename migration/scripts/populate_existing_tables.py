#!/usr/bin/env python3
"""
Populate existing tables with CSV data from extracted_data directory
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

def populate_language_master():
    """Populate language master table"""
    print("Populating Language Master...")
    
    try:
        df = pd.read_csv('extracted_data/Language_Master.csv')
        conn = get_postgresql_connection()
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
        print(f"✅ Populated {len(df)} language master records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating language master: {e}")
        return False

def populate_role_master():
    """Populate role master table"""
    print("Populating Role Master...")
    
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
        print(f"✅ Populated {len(df)} role master records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating role master: {e}")
        return False

def populate_gender_master():
    """Populate gender master table"""
    print("Populating Gender Master...")
    
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
                ON CONFLICT (gender_id) DO NOTHING
            """, (
                gender_id,
                gender_name,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Populated {len(df)} gender master records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating gender master: {e}")
        return False

def populate_doctor_master():
    """Populate doctor master table"""
    print("Populating Doctor Master...")
    
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
        print(f"✅ Populated {len(df)} doctor master records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating doctor master: {e}")
        return False

def populate_clinic_master():
    """Populate clinic master table"""
    print("Populating Clinic Master...")
    
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
                ON CONFLICT (clinic_id) DO NOTHING
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
        print(f"✅ Populated {len(df)} clinic master records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating clinic master: {e}")
        return False

def populate_user_master():
    """Populate user master table"""
    print("Populating User Master...")
    
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
                ON CONFLICT (user_id) DO NOTHING
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
        print(f"✅ Populated {len(df)} user master records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating user master: {e}")
        return False

def populate_patient_master():
    """Populate patient master table"""
    print("Populating Patient Master...")
    
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
                    ON CONFLICT (patient_id) DO NOTHING
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
        print(f"✅ Populated {imported_count} patient master records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating patient master: {e}")
        return False

def populate_patient_visits():
    """Populate patient visits table"""
    print("Populating Patient Visits...")
    
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
                    ON CONFLICT (visit_id) DO NOTHING
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
        print(f"✅ Populated {imported_count} patient visit records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating patient visits: {e}")
        return False

def populate_system_params():
    """Populate system parameters table"""
    print("Populating System Parameters...")
    
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
                ON CONFLICT (param_id) DO NOTHING
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
        print(f"✅ Populated {len(df)} system parameter records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating system parameters: {e}")
        return False

def populate_user_role():
    """Populate user role table"""
    print("Populating User Role...")
    
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
                ON CONFLICT DO NOTHING
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
        print(f"✅ Populated {len(df)} user role records")
        return True
        
    except Exception as e:
        print(f"❌ Error populating user role: {e}")
        return False

def verify_population():
    """Verify the populated data"""
    print("\n=== Verifying Data Population ===")
    
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
        
        print(f"\n📊 Total Records Populated: {total_records:,}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"❌ Error verifying data: {e}")

def main():
    """Main function"""
    print("🚀 Populating Existing Tables with CSV Data")
    print("=" * 50)
    
    # Check if extracted_data directory exists
    if not os.path.exists('extracted_data'):
        print("❌ extracted_data directory not found!")
        return False
    
    # Populate functions in order
    populate_functions = [
        populate_language_master,
        populate_role_master,
        populate_gender_master,
        populate_doctor_master,
        populate_clinic_master,
        populate_user_master,
        populate_patient_master,
        populate_patient_visits,
        populate_system_params,
        populate_user_role
    ]
    
    success_count = 0
    for populate_func in populate_functions:
        if populate_func():
            success_count += 1
    
    print(f"\n📊 Population Summary: {success_count}/{len(populate_functions)} tables populated successfully")
    
    if success_count == len(populate_functions):
        print("🎉 All tables populated successfully!")
        verify_population()
        return True
    else:
        print("⚠️  Some tables failed to populate. Check the error messages above.")
        return False

if __name__ == "__main__":
    main()
