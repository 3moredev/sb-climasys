#!/usr/bin/env python3
"""
Fix Data Integrity Issues and Add Foreign Key Constraints
This script fixes data integrity issues and properly adds foreign key constraints.
"""

import psycopg2
import logging
from datetime import datetime

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_postgres_connection():
    """Get PostgreSQL connection"""
    try:
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        return conn
    except Exception as e:
        logger.error(f"❌ Error connecting to PostgreSQL: {e}")
        return None

def fix_data_integrity_issues(conn):
    """Fix data integrity issues by cleaning up sample data"""
    logger.info("🔧 Fixing Data Integrity Issues")
    logger.info("=" * 50)
    
    cursor = conn.cursor()
    
    # Clear sample data from tables that have foreign key relationships
    tables_to_clear = [
        'doctor_clinic_shift',
        'doctor_model', 
        'model_config_params',
        'user_role'
    ]
    
    for table in tables_to_clear:
        try:
            cursor.execute(f"DELETE FROM climasys_dev.{table}")
            deleted_count = cursor.rowcount
            logger.info(f"✅ Cleared {deleted_count} records from {table}")
        except Exception as e:
            logger.error(f"❌ Failed to clear {table}: {e}")
            conn.rollback()
            continue
    
    # Insert proper reference data
    try:
        # Insert proper gender data
        cursor.execute("""
            INSERT INTO climasys_dev.gender_master (gender_id, gender_name, created_on, updated_on, is_active)
            VALUES 
                ('M', 'Male', NOW(), NOW(), true),
                ('F', 'Female', NOW(), NOW(), true),
                ('O', 'Other', NOW(), NOW(), true)
            ON CONFLICT (gender_id) DO NOTHING
        """)
        logger.info("✅ Ensured gender_master has proper data")
        
        # Insert proper blood group data
        cursor.execute("""
            INSERT INTO climasys_dev.blood_group_master (blood_group_id, blood_group_name, created_on, updated_on, is_active)
            VALUES 
                ('A+', 'A Positive', NOW(), NOW(), true),
                ('A-', 'A Negative', NOW(), NOW(), true),
                ('B+', 'B Positive', NOW(), NOW(), true),
                ('B-', 'B Negative', NOW(), NOW(), true),
                ('AB+', 'AB Positive', NOW(), NOW(), true),
                ('AB-', 'AB Negative', NOW(), NOW(), true),
                ('O+', 'O Positive', NOW(), NOW(), true),
                ('O-', 'O Negative', NOW(), NOW(), true)
            ON CONFLICT (blood_group_id) DO NOTHING
        """)
        logger.info("✅ Ensured blood_group_master has proper data")
        
        # Insert proper role data
        cursor.execute("""
            INSERT INTO climasys_dev.role_master (role_id, role_name, role_description, created_on, updated_on, is_active)
            VALUES 
                ('ADMIN', 'Administrator', 'System Administrator', NOW(), NOW(), true),
                ('DOCTOR', 'Doctor', 'Medical Doctor', NOW(), NOW(), true),
                ('NURSE', 'Nurse', 'Nursing Staff', NOW(), NOW(), true),
                ('RECEPTIONIST', 'Receptionist', 'Front Desk Staff', NOW(), NOW(), true),
                ('PATIENT', 'Patient', 'Patient User', NOW(), NOW(), true)
            ON CONFLICT (role_id) DO NOTHING
        """)
        logger.info("✅ Ensured role_master has proper data")
        
        # Insert proper model data
        cursor.execute("""
            INSERT INTO climasys_dev.model (model_id, model_name, model_description, created_on, updated_on, is_active)
            VALUES 
                (1, 'Basic Model', 'Basic clinic management model', NOW(), NOW(), true),
                (2, 'Premium Model', 'Premium clinic management model', NOW(), NOW(), true),
                (3, 'Enterprise Model', 'Enterprise clinic management model', NOW(), NOW(), true)
            ON CONFLICT (model_id) DO NOTHING
        """)
        logger.info("✅ Ensured model has proper data")
        
        conn.commit()
        
    except Exception as e:
        logger.error(f"❌ Failed to insert reference data: {e}")
        conn.rollback()
        return False
    
    cursor.close()
    return True

def add_foreign_key_constraints(conn):
    """Add foreign key constraints after fixing data integrity"""
    logger.info("🔗 Adding Foreign Key Constraints")
    logger.info("=" * 50)
    
    cursor = conn.cursor()
    
    # Define foreign key relationships
    foreign_keys = [
        # Patient relationships
        {
            'table': 'patient_master',
            'column': 'gender_id',
            'ref_table': 'gender_master',
            'ref_column': 'gender_id',
            'name': 'fk_patient_gender'
        },
        {
            'table': 'patient_master',
            'column': 'blood_group_id',
            'ref_table': 'blood_group_master',
            'ref_column': 'blood_group_id',
            'name': 'fk_patient_blood_group'
        },
        
        # User relationships
        {
            'table': 'user_role',
            'column': 'user_id',
            'ref_table': 'user_master',
            'ref_column': 'user_id',
            'name': 'fk_user_role_user'
        },
        {
            'table': 'user_role',
            'column': 'role_id',
            'ref_table': 'role_master',
            'ref_column': 'role_id',
            'name': 'fk_user_role_role'
        },
        
        # Visit relationships
        {
            'table': 'patient_visits',
            'column': 'patient_id',
            'ref_table': 'patient_master',
            'ref_column': 'patient_id',
            'name': 'fk_visit_patient'
        },
        {
            'table': 'patient_visits',
            'column': 'doctor_id',
            'ref_table': 'doctor_master',
            'ref_column': 'doctor_id',
            'name': 'fk_visit_doctor'
        },
        {
            'table': 'patient_visits',
            'column': 'clinic_id',
            'ref_table': 'clinic_master',
            'ref_column': 'clinic_id',
            'name': 'fk_visit_clinic'
        },
        
        # Model relationships
        {
            'table': 'model_config_params',
            'column': 'model_id',
            'ref_table': 'model',
            'ref_column': 'model_id',
            'name': 'fk_model_config_params_model'
        }
    ]
    
    successful_constraints = 0
    failed_constraints = 0
    
    for fk in foreign_keys:
        try:
            # Check if constraint already exists
            cursor.execute("""
                SELECT constraint_name 
                FROM information_schema.table_constraints
                WHERE table_schema = 'climasys_dev' 
                AND table_name = %s 
                AND constraint_name = %s
            """, (fk['table'], fk['name']))
            
            if cursor.fetchone():
                logger.info(f"✅ Foreign key {fk['name']} already exists")
                continue
            
            # Check if both tables and columns exist
            cursor.execute("""
                SELECT COUNT(*) 
                FROM information_schema.columns
                WHERE table_schema = 'climasys_dev' 
                AND table_name = %s 
                AND column_name = %s
            """, (fk['table'], fk['column']))
            
            if cursor.fetchone()[0] == 0:
                logger.warning(f"⚠️  Column {fk['table']}.{fk['column']} not found, skipping")
                continue
            
            cursor.execute("""
                SELECT COUNT(*) 
                FROM information_schema.columns
                WHERE table_schema = 'climasys_dev' 
                AND table_name = %s 
                AND column_name = %s
            """, (fk['ref_table'], fk['ref_column']))
            
            if cursor.fetchone()[0] == 0:
                logger.warning(f"⚠️  Reference column {fk['ref_table']}.{fk['ref_column']} not found, skipping")
                continue
            
            # Add foreign key constraint
            fk_sql = f"""
                ALTER TABLE climasys_dev.{fk['table']} 
                ADD CONSTRAINT {fk['name']} 
                FOREIGN KEY ({fk['column']}) 
                REFERENCES climasys_dev.{fk['ref_table']}({fk['ref_column']})
            """
            
            cursor.execute(fk_sql)
            logger.info(f"✅ Added foreign key: {fk['name']} ({fk['table']}.{fk['column']} → {fk['ref_table']}.{fk['ref_column']})")
            successful_constraints += 1
            
        except Exception as e:
            logger.error(f"❌ Failed to add foreign key {fk['name']}: {e}")
            failed_constraints += 1
            conn.rollback()
            continue
    
    conn.commit()
    cursor.close()
    
    logger.info(f"📊 Foreign Key Constraints Summary:")
    logger.info(f"  - Successful constraints: {successful_constraints}")
    logger.info(f"  - Failed constraints: {failed_constraints}")
    
    return successful_constraints > 0

def verify_database_integrity(conn):
    """Verify database integrity and constraints"""
    logger.info("🔍 Verifying Database Integrity")
    logger.info("=" * 50)
    
    cursor = conn.cursor()
    
    # Check foreign key constraints
    cursor.execute("""
        SELECT 
            tc.table_name,
            tc.constraint_name,
            kcu.column_name,
            ccu.table_name AS foreign_table_name,
            ccu.column_name AS foreign_column_name
        FROM information_schema.table_constraints AS tc
        JOIN information_schema.key_column_usage AS kcu
            ON tc.constraint_name = kcu.constraint_name
        JOIN information_schema.constraint_column_usage AS ccu
            ON ccu.constraint_name = tc.constraint_name
        WHERE tc.constraint_type = 'FOREIGN KEY'
        AND tc.table_schema = 'climasys_dev'
        ORDER BY tc.table_name, tc.constraint_name
    """)
    
    foreign_keys = cursor.fetchall()
    logger.info(f"📋 Foreign Key Constraints: {len(foreign_keys)}")
    for fk in foreign_keys:
        logger.info(f"  - {fk[0]}.{fk[2]} → {fk[3]}.{fk[4]} ({fk[1]})")
    
    # Check data counts
    tables_to_check = [
        'patient_master', 'doctor_master', 'clinic_master', 'user_master',
        'gender_master', 'blood_group_master', 'role_master', 'model',
        'patient_visits', 'user_role', 'model_config_params'
    ]
    
    logger.info(f"📊 Data Counts:")
    total_records = 0
    for table in tables_to_check:
        try:
            cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cursor.fetchone()[0]
            total_records += count
            logger.info(f"  - {table}: {count:,} records")
        except Exception as e:
            logger.warning(f"  - {table}: Error getting count - {e}")
    
    logger.info(f"  - Total records: {total_records:,}")
    
    cursor.close()
    
    return len(foreign_keys), total_records

def main():
    """Main function"""
    logger.info("🚀 Starting Data Integrity Fixes and Constraint Addition")
    logger.info("=" * 70)
    
    # Get PostgreSQL connection
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Could not connect to PostgreSQL")
        return False
    
    try:
        # Step 1: Fix data integrity issues
        integrity_success = fix_data_integrity_issues(conn)
        
        # Step 2: Add foreign key constraints
        constraint_success = add_foreign_key_constraints(conn)
        
        # Step 3: Verify database integrity
        fk_count, total_records = verify_database_integrity(conn)
        
        # Final summary
        logger.info("🎉 Data Integrity and Constraint Fixes Complete!")
        logger.info("=" * 70)
        logger.info(f"📊 Summary:")
        logger.info(f"  - Data integrity fixes: {'✅ Success' if integrity_success else '❌ Failed'}")
        logger.info(f"  - Foreign key constraints: {'✅ Success' if constraint_success else '❌ Failed'}")
        logger.info(f"  - Total foreign keys: {fk_count}")
        logger.info(f"  - Total records: {total_records:,}")
        
        return integrity_success and constraint_success
        
    except Exception as e:
        logger.error(f"❌ Unexpected error: {e}")
        return False
    finally:
        conn.close()

if __name__ == "__main__":
    main()
