#!/usr/bin/env python3
"""
Robust data import with comprehensive error handling and data validation
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
    """Get PostgreSQL connection with proper error handling"""
    try:
        return psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
    except Exception as e:
        logger.error(f"Failed to connect to PostgreSQL: {e}")
        raise

def clear_table_safely(table_name):
    """Clear a single table safely with proper error handling"""
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

def validate_and_clean_data(df, table_name):
    """Validate and clean data before import"""
    logger.info(f"Validating data for {table_name}: {len(df)} records")
    
    # Remove completely empty rows
    df = df.dropna(how='all')
    
    # Log data quality issues
    for col in df.columns:
        null_count = df[col].isnull().sum()
        if null_count > 0:
            logger.warning(f"  Column {col}: {null_count} null values")
    
    return df

def import_table_robust(table_name, csv_file, import_function):
    """Import table with comprehensive error handling"""
    logger.info(f"🔄 Starting import for {table_name}")
    
    # Clear table first
    if not clear_table_safely(table_name):
        return False
    
    # Import data
    try:
        success = import_function()
        if success:
            logger.info(f"✅ {table_name} imported successfully")
            return True
        else:
            logger.error(f"❌ {table_name} import failed")
            return False
    except Exception as e:
        logger.error(f"❌ Error importing {table_name}: {e}")
        return False

def import_language_master():
    """Import language master data with validation"""
    try:
        df = pd.read_csv('extracted_data/Language_Master.csv')
        df = validate_and_clean_data(df, 'language_master')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Validate required fields
            if pd.isna(row.get('Language_Id')) or pd.isna(row.get('Language_Name')):
                logger.warning(f"Skipping row with missing required fields: {row}")
                continue
            
            cur.execute("""
                INSERT INTO climasys_dev.language_master (language_id, language_name, created_on, modified_on)
                VALUES (%s, %s, %s, %s)
            """, (
                str(row['Language_Id']),
                str(row['Language_Name']),
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
    """Import role master data with validation"""
    try:
        df = pd.read_csv('extracted_data/Role_Master.csv')
        df = validate_and_clean_data(df, 'role_master')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Validate required fields
            if pd.isna(row.get('Role_Id')) or pd.isna(row.get('Role_Name')):
                logger.warning(f"Skipping row with missing required fields: {row}")
                continue
            
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
                str(row['Role_Name']),
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
    """Import gender master data with validation"""
    try:
        df = pd.read_csv('extracted_data/Gender_Master.csv')
        df = validate_and_clean_data(df, 'gender_master')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Map gender codes to names
        gender_names = {'M': 'Male', 'F': 'Female', 'O': 'Other'}
        
        for _, row in df.iterrows():
            # Validate required fields
            if pd.isna(row.get('ID')):
                logger.warning(f"Skipping row with missing ID: {row}")
                continue
            
            gender_id = str(row['ID'])
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
    """Import doctor master data with validation"""
    try:
        df = pd.read_csv('extracted_data/Doctor_Master.csv')
        df = validate_and_clean_data(df, 'doctor_master')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Validate required fields
            if pd.isna(row.get('Doctor_Id')):
                logger.warning(f"Skipping row with missing Doctor_Id: {row}")
                continue
            
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
    """Import clinic master data with validation"""
    try:
        df = pd.read_csv('extracted_data/Clinic_Master.csv')
        df = validate_and_clean_data(df, 'clinic_master')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Validate required fields
            if pd.isna(row.get('Clinic_Id')):
                logger.warning(f"Skipping row with missing Clinic_Id: {row}")
                continue
            
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
    """Import user master data with validation"""
    try:
        df = pd.read_csv('extracted_data/User_Master.csv')
        df = validate_and_clean_data(df, 'user_master')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Validate required fields
            if pd.isna(row.get('User_Id')):
                logger.warning(f"Skipping row with missing User_Id: {row}")
                continue
            
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
    """Import patient master data with batch processing and validation"""
    try:
        df = pd.read_csv('extracted_data/Patient_Master.csv', low_memory=False)
        df = validate_and_clean_data(df, 'patient_master')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        batch_size = 500  # Smaller batch size for better error handling
        imported_count = 0
        error_count = 0
        
        for i in range(0, len(df), batch_size):
            batch = df.iloc[i:i+batch_size]
            
            for _, row in batch.iterrows():
                try:
                    # Validate required fields
                    if pd.isna(row.get('Patient_Id')):
                        logger.warning(f"Skipping row with missing Patient_Id")
                        error_count += 1
                        continue
                    
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
    """Import patient visits data with batch processing and validation"""
    try:
        df = pd.read_csv('extracted_data/Patient_Visits.csv', low_memory=False)
        df = validate_and_clean_data(df, 'patient_visits')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        batch_size = 500  # Smaller batch size for better error handling
        imported_count = 0
        error_count = 0
        
        for i in range(0, len(df), batch_size):
            batch = df.iloc[i:i+batch_size]
            
            for _, row in batch.iterrows():
                try:
                    # Validate required fields
                    if pd.isna(row.get('Visit_Id')):
                        logger.warning(f"Skipping row with missing Visit_Id")
                        error_count += 1
                        continue
                    
                    # Parse dates safely
                    visit_date = None
                    if pd.notna(row.get('Visit_Date')):
                        try:
                            visit_date = pd.to_datetime(row['Visit_Date']).date()
                        except:
                            visit_date = None
                    
                    visit_time = None
                    if pd.notna(row.get('Visit_Time')):
                        try:
                            visit_time = pd.to_datetime(row['Visit_Time']).time()
                        except:
                            visit_time = None
                    
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
                        str(row.get('Chief_Complaint', '')),
                        str(row.get('Diagnosis', '')),
                        str(row.get('Treatment', '')),
                        str(row.get('Prescription', '')),
                        follow_up_date,
                        str(row.get('Attended_By_Id', '')),
                        False,
                        datetime.now(),
                        datetime.now()
                    ))
                    imported_count += 1
                    
                except Exception as e:
                    logger.warning(f"Skipping visit {row.get('Visit_Id', 'unknown')}: {e}")
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
    """Import system parameters with duplicate handling"""
    try:
        df = pd.read_csv('extracted_data/System_Params.csv')
        df = validate_and_clean_data(df, 'system_params')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Skip rows with empty param_id
            if pd.isna(row.get('Param_Id')) or str(row.get('Param_Id')).strip() == '':
                logger.warning(f"Skipping row with empty param_id")
                continue
            
            cur.execute("""
                INSERT INTO climasys_dev.system_params (
                    param_id, param_name, param_value, created_on, modified_on
                )
                VALUES (%s, %s, %s, %s, %s)
                ON CONFLICT (param_id) DO UPDATE SET
                    param_name = EXCLUDED.param_name,
                    param_value = EXCLUDED.param_value,
                    modified_on = EXCLUDED.modified_on
            """, (
                str(row.get('Param_Id', '')),
                str(row.get('Param_Name', '')),
                str(row.get('Param_Value', '')),
                datetime.now(),
                datetime.now()
            ))
        
        conn.commit()
        cur.close()
        conn.close()
        logger.info(f"✅ Imported {len(df)} system parameter records")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error importing system parameters: {e}")
        return False

def import_user_role():
    """Import user role data with validation"""
    try:
        df = pd.read_csv('extracted_data/User_Role.csv')
        df = validate_and_clean_data(df, 'user_role')
        
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            # Validate required fields
            if pd.isna(row.get('User_Id')):
                logger.warning(f"Skipping row with missing User_Id: {row}")
                continue
            
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
    logger.info("🚀 Starting Robust Data Import with Comprehensive Error Handling")
    logger.info("=" * 80)
    
    # Import tables one by one with proper error handling
    import_tasks = [
        ('language_master', 'Language_Master.csv', import_language_master),
        ('role_master', 'Role_Master.csv', import_role_master),
        ('gender_master', 'Gender_Master.csv', import_gender_master),
        ('doctor_master', 'Doctor_Master.csv', import_doctor_master),
        ('clinic_master', 'Clinic_Master.csv', import_clinic_master),
        ('user_master', 'User_Master.csv', import_user_master),
        ('patient_master', 'Patient_Master.csv', import_patient_master),
        ('patient_visits', 'Patient_Visits.csv', import_patient_visits),
        ('system_params', 'System_Params.csv', import_system_params),
        ('user_role', 'User_Role.csv', import_user_role)
    ]
    
    success_count = 0
    for table_name, csv_file, import_function in import_tasks:
        if import_table_robust(table_name, csv_file, import_function):
            success_count += 1
    
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

