#!/usr/bin/env python3
"""
Final data import with proper column mapping from CSV to PostgreSQL
"""

import psycopg2
import pandas as pd
import os
from datetime import datetime
import logging

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_postgresql_connection():
    """Get PostgreSQL connection"""
    return psycopg2.connect(
        host='localhost',
        port='5432',
        database='climasys_dev',
        user='postgres',
        password='root'
    )

def clear_table_safely(table_name):
    """Clear a single table safely"""
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        cur.execute(f"DELETE FROM climasys_dev.{table_name}")
        conn.commit()
        cur.close()
        conn.close()
        logger.info(f"✅ Cleared {table_name}")
        return True
    except Exception as e:
        logger.error(f"❌ Error clearing {table_name}: {e}")
        return False

def import_language_master():
    """Import language master data"""
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
        logger.info(f"✅ Imported {len(df)} language master records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing language master: {e}")
        return False

def import_role_master():
    """Import role master data"""
    try:
        df = pd.read_csv('extracted_data/Role_Master.csv')
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Parse dates safely
            created_on = datetime.now()
            modified_on = datetime.now()
            
            if pd.notna(row.get('Created_On')) and str(row['Created_On']) != 'None':
                try:
                    created_on = pd.to_datetime(row['Created_On'])
                except:
                    created_on = datetime.now()
            
            if pd.notna(row.get('Modified_On')) and str(row['Modified_On']) != 'None':
                try:
                    modified_on = pd.to_datetime(row['Modified_On'])
                except:
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
        logger.info(f"✅ Imported {len(df)} role master records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing role master: {e}")
        return False

def import_gender_master():
    """Import gender master data"""
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
        logger.info(f"✅ Imported {len(df)} gender master records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing gender master: {e}")
        return False

def import_doctor_master():
    """Import doctor master data"""
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
                str(row.get('First_Name', '')),
                str(row.get('Middle_Name', '')),
                str(row.get('Last_Name', '')),
                str(row.get('Qualification', '')),
                str(row.get('Specialization', '')),
                str(row.get('Phone', '')),
                str(row.get('Email', '')),
                str(row.get('Address', '')),
                True,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        logger.info(f"✅ Imported {len(df)} doctor master records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing doctor master: {e}")
        return False

def import_clinic_master():
    """Import clinic master data"""
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
                str(row.get('Clinic_Name', '')),
                str(row.get('Clinic_Address', '')),
                str(row.get('Clinic_Phone', '')),
                str(row.get('Clinic_Email', '')),
                True,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        logger.info(f"✅ Imported {len(df)} clinic master records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing clinic master: {e}")
        return False

def import_user_master():
    """Import user master data"""
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
                str(row.get('Username', '')),
                str(row.get('Password', '')),
                str(row.get('User_Role', '')),
                str(row.get('Email', '')),
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
        logger.info(f"✅ Imported {len(df)} user master records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing user master: {e}")
        return False

def import_patient_master():
    """Import patient master data with proper column mapping"""
    try:
        df = pd.read_csv('extracted_data/Patient_Master.csv', low_memory=False)
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        batch_size = 1000
        imported_count = 0
        error_count = 0
        
        for i in range(0, len(df), batch_size):
            batch = df.iloc[i:i+batch_size]
            
            for _, row in batch.iterrows():
                try:
                    # Parse date of birth safely
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
                        str(row.get('First_Name', '')),
                        str(row.get('Middle_Name', '')),
                        str(row.get('Last_Name', '')),
                        str(row.get('Gender_Id', '')),
                        dob,
                        row.get('Age', None),
                        str(row.get('Blood_Group_Id', '')) if pd.notna(row.get('Blood_Group_Id')) else None,
                        str(row.get('Phone', '')),
                        str(row.get('Email', '')),
                        str(row.get('Address', '')),
                        str(row.get('Emergency_Contact', '')),
                        str(row.get('Emergency_Contact_Name', '')),
                        True,
                        datetime.now(),
                        datetime.now()
                    ))
                    imported_count += 1
                    
                except Exception as e:
                    logger.warning(f"Skipping patient {row.get('Patient_Id', 'unknown')}: {e}")
                    error_count += 1
                    continue
            
            # Commit batch
            conn.commit()
            logger.info(f"  Processed batch {i//batch_size + 1}: {imported_count} imported, {error_count} errors")
        
        cur.close()
        conn.close()
        logger.info(f"✅ Imported {imported_count} patient master records ({error_count} errors)")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing patient master: {e}")
        return False

def import_patient_visits():
    """Import patient visits data with proper column mapping"""
    try:
        df = pd.read_csv('extracted_data/Patient_Visits.csv', low_memory=False)
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        batch_size = 1000
        imported_count = 0
        error_count = 0
        
        for i in range(0, len(df), batch_size):
            batch = df.iloc[i:i+batch_size]
            
            for _, row in batch.iterrows():
                try:
                    # Create a composite visit_id from Patient_ID and Patient_Visit_No
                    visit_id = f"{row.get('Patient_ID', '')}-{row.get('Patient_Visit_No', '')}"
                    
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
                        visit_id,
                        str(row.get('Patient_ID', '')),
                        str(row.get('Doctor_ID', '')),
                        str(row.get('Clinic_ID', '')),
                        visit_date,
                        visit_time,
                        str(row.get('Current_Complaints', '')),
                        str(row.get('Impression', '')),
                        str(row.get('Treatment_plan', '')),
                        str(row.get('Current_Medicines', '')),
                        follow_up_date,
                        str(row.get('Attended_By_ID', '')),
                        bool(row.get('Delete_Flag', False)),
                        datetime.now(),
                        datetime.now()
                    ))
                    imported_count += 1
                    
                except Exception as e:
                    logger.warning(f"Skipping visit {row.get('Patient_ID', 'unknown')}-{row.get('Patient_Visit_No', 'unknown')}: {e}")
                    error_count += 1
                    continue
            
            # Commit batch
            conn.commit()
            logger.info(f"  Processed batch {i//batch_size + 1}: {imported_count} imported, {error_count} errors")
        
        cur.close()
        conn.close()
        logger.info(f"✅ Imported {imported_count} patient visit records ({error_count} errors)")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing patient visits: {e}")
        return False

def import_system_params():
    """Import system parameters with proper column mapping"""
    try:
        df = pd.read_csv('extracted_data/System_Params.csv')
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # System params CSV has different structure - it's more like a configuration table
        # We'll create a generic system parameter for each doctor
        for _, row in df.iterrows():
            doctor_id = str(row.get('Doctor_ID', ''))
            
            # Create multiple system parameters from the configuration
            params = [
                ('DEFAULT_FEES', str(row.get('Default_Fees', '500'))),
                ('DEFAULT_CLINIC_ID', str(row.get('Default_Clinic_ID', ''))),
                ('DEFAULT_PRESCRIPTION_COUNT', str(row.get('Default_Prescription_Count', '20'))),
                ('TREATMENT_DETAILS', str(row.get('Treatment_Details', 'Consulting Charges'))),
                ('TYPE_OF_SERVICE', str(row.get('Type_Of_Service', 'Consulting'))),
                ('SHOW_DAILY_COLLECTION', str(row.get('Show_Daily_Collection', 'True'))),
                ('SHOW_FINANCIALS', str(row.get('Show_Financials', 'True'))),
                ('PRINT_DIAGNOSIS_ON_PRESCRIPTION', str(row.get('Print_Diagnosis_on_Prescription', 'True'))),
                ('COPY_VISIT', str(row.get('Copy_Visit', 'True'))),
                ('AUDIO_ALLERGY', str(row.get('Audio_Allergy', 'False'))),
                ('IS_MAIN_DOCTOR', str(row.get('IS_MAIN_DOCTOR', 'False'))),
                ('SHOW_COMPLAINT', str(row.get('Show_complaint', 'False'))),
                ('PRINT_RECEIPT_HEADER', str(row.get('Print_Receipt_Header', 'True'))),
                ('PRINT_PRESCRIPTION_HEADER', str(row.get('Print_Prescription_Header', 'False'))),
                ('PRINT_OPS_LABEL', str(row.get('Print_OPS_Label', 'False'))),
                ('PRINT_DISCOUNT_ON_HOSPITAL_BILL', str(row.get('Print_Discount_on_Hospital_bill', 'True'))),
                ('PRINT_ADVICE_LABEL', str(row.get('Print_Advice_label', 'True'))),
                ('PRINT_GENERIC_NAME', str(row.get('Print_Generic_Name', 'True'))),
                ('CAPTURE_FOLLOWUP_TEXT', str(row.get('Capture_Followup_Text', 'True'))),
                ('COPY_PRESCRIPTION', str(row.get('Copy_prescription', 'True'))),
                ('PRINT_VITALS', str(row.get('Print_Vitals', 'True'))),
                ('PRINT_COMPLAINTS_ON_PRESCRIPTION', str(row.get('Print_Complaints_on_prescriptipon', 'True'))),
                ('PRINT_EXAMINATION_ON_PRESCRIPTION', str(row.get('Print_Examination_on_prescriptipon', 'True'))),
                ('SHOW_SERVICES', str(row.get('Show_Services', 'False'))),
                ('DEFAULT_PRESCRIPTION_CATEGORY', str(row.get('Default_Prescription_Category', 'False')))
            ]
            
            for param_name, param_value in params:
                param_id = f"{doctor_id}_{param_name}"
                
                cur.execute("""
                    INSERT INTO climasys_dev.system_params (
                        param_id, param_name, param_value, created_on, modified_on
                    )
                    VALUES (%s, %s, %s, %s, %s)
                    ON CONFLICT (param_id) DO UPDATE SET
                        param_value = EXCLUDED.param_value,
                        modified_on = EXCLUDED.modified_on
                """, (
                    param_id,
                    param_name,
                    param_value,
                    datetime.now(),
                    datetime.now()
                ))
        
        conn.commit()
        cur.close()
        conn.close()
        logger.info(f"✅ Imported system parameters for {len(df)} doctors")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing system parameters: {e}")
        return False

def import_user_role():
    """Import user role data with proper column mapping"""
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
                str(row.get('User_ID', '')),
                str(row.get('Clinic_ID', '')),
                str(row.get('Role_Id', '')),
                True,
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        logger.info(f"✅ Imported {len(df)} user role records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing user role: {e}")
        return False

def verify_final_counts():
    """Verify final record counts"""
    logger.info("=== Final Record Counts ===")
    
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
            logger.info(f"  {table}: {count:,} records")
        
        logger.info(f"📊 Total Records: {total_records:,}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        logger.error(f"❌ Error verifying data: {e}")

def main():
    """Main function"""
    logger.info("🚀 Final Data Import with Proper Column Mapping")
    logger.info("=" * 60)
    
    # Import tables one by one with proper error handling
    import_tasks = [
        ('language_master', import_language_master),
        ('role_master', import_role_master),
        ('gender_master', import_gender_master),
        ('doctor_master', import_doctor_master),
        ('clinic_master', import_clinic_master),
        ('user_master', import_user_master),
        ('patient_master', import_patient_master),
        ('patient_visits', import_patient_visits),
        ('system_params', import_system_params),
        ('user_role', import_user_role)
    ]
    
    success_count = 0
    for table_name, import_function in import_tasks:
        logger.info(f"🔄 Starting import for {table_name}")
        
        # Clear table first
        if not clear_table_safely(table_name):
            continue
        
        # Import data
        try:
            success = import_function()
            if success:
                logger.info(f"✅ {table_name} imported successfully")
                success_count += 1
            else:
                logger.error(f"❌ {table_name} import failed")
        except Exception as e:
            logger.error(f"❌ Error importing {table_name}: {e}")
    
    logger.info(f"📊 Import Summary: {success_count}/{len(import_tasks)} tables imported successfully")
    
    if success_count == len(import_tasks):
        logger.info("🎉 All data imported successfully!")
        verify_final_counts()
        return True
    else:
        logger.warning("⚠️  Some imports failed. Check the error messages above.")
        return False

if __name__ == "__main__":
    main()

