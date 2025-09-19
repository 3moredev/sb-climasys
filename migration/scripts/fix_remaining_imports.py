#!/usr/bin/env python3
"""
Fix remaining data imports with correct column mapping
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

def import_patient_master_fixed():
    """Import patient master data with correct column mapping"""
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
                        str(row.get('ID', '')),  # Use ID instead of Patient_ID
                        str(row.get('Doctor_ID', '')),
                        str(row.get('First_Name', '')),
                        str(row.get('Middle_Name', '')),
                        str(row.get('Last_Name', '')),
                        str(row.get('Gender_ID', '')),
                        dob,
                        row.get('Age_Given', None),
                        str(row.get('BloodGroup_ID', '')) if pd.notna(row.get('BloodGroup_ID')) else None,
                        str(row.get('Mobile_1', '')),
                        str(row.get('Email_ID', '')),
                        str(row.get('Address_1', '')),
                        str(row.get('Emergency_Number', '')),
                        str(row.get('Emergency_Name', '')),
                        True,
                        datetime.now(),
                        datetime.now()
                    ))
                    imported_count += 1
                    
                except Exception as e:
                    logger.warning(f"Skipping patient {row.get('ID', 'unknown')}: {e}")
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

def import_doctor_master_fixed():
    """Import doctor master data with correct column mapping"""
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

def import_clinic_master_fixed():
    """Import clinic master data with correct column mapping"""
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

def import_user_master_fixed():
    """Import user master data with correct column mapping"""
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

def add_foreign_key_constraints():
    """Add foreign key constraints after successful data import"""
    logger.info("🔗 Adding Foreign Key Constraints...")
    
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Define foreign key constraints
        fk_constraints = [
            # Patient Master foreign keys
            ("patient_master", "doctor_id", "doctor_master", "doctor_id"),
            ("patient_master", "gender_id", "gender_master", "gender_id"),
            ("patient_master", "blood_group_id", "blood_group_master", "blood_group_id"),
            
            # Patient Visits foreign keys
            ("patient_visits", "patient_id", "patient_master", "patient_id"),
            ("patient_visits", "doctor_id", "doctor_master", "doctor_id"),
            ("patient_visits", "clinic_id", "clinic_master", "clinic_id"),
            
            # User Master foreign keys
            ("user_master", "doctor_id", "doctor_master", "doctor_id"),
            ("user_master", "clinic_id", "clinic_master", "clinic_id"),
            ("user_master", "language_id", "language_master", "language_id"),
            
            # User Role foreign keys
            ("user_role", "user_id", "user_master", "user_id"),
            ("user_role", "role_id", "role_master", "role_id"),
            ("user_role", "clinic_id", "clinic_master", "clinic_id"),
        ]
        
        for table, column, ref_table, ref_column in fk_constraints:
            try:
                constraint_name = f"fk_{table}_{column}"
                cur.execute(f"""
                    ALTER TABLE climasys_dev.{table}
                    ADD CONSTRAINT {constraint_name}
                    FOREIGN KEY ({column}) REFERENCES climasys_dev.{ref_table}({ref_column})
                    ON DELETE CASCADE ON UPDATE CASCADE;
                """)
                logger.info(f"✅ Added FK: {table}.{column} -> {ref_table}.{ref_column}")
            except Exception as e:
                logger.warning(f"⚠️  Could not add FK {table}.{column}: {e}")
        
        conn.commit()
        cur.close()
        conn.close()
        logger.info("🎉 Foreign key constraints added successfully!")
        return True
        
    except Exception as e:
        logger.error(f"❌ Error adding foreign key constraints: {e}")
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
    logger.info("🚀 Fixing Remaining Data Imports and Adding Foreign Keys")
    logger.info("=" * 70)
    
    # Import remaining tables
    import_tasks = [
        ('patient_master', import_patient_master_fixed),
        ('doctor_master', import_doctor_master_fixed),
        ('clinic_master', import_clinic_master_fixed),
        ('user_master', import_user_master_fixed)
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
        logger.info("🎉 All remaining data imported successfully!")
        
        # Add foreign key constraints
        add_foreign_key_constraints()
        
        # Verify final counts
        verify_final_counts()
        return True
    else:
        logger.warning("⚠️  Some imports failed. Check the error messages above.")
        return False

if __name__ == "__main__":
    main()

