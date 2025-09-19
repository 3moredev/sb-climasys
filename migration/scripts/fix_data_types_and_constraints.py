#!/usr/bin/env python3
"""
Fix Data Types and Add Foreign Key Constraints
This script addresses data type mismatches and adds proper constraints to the PostgreSQL database.
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

def fix_data_types(conn):
    """Fix common data type issues"""
    logger.info("🔧 Fixing Data Type Issues")
    logger.info("=" * 50)
    
    cursor = conn.cursor()
    
    # Common data type fixes
    data_type_fixes = [
        # Fix phone number columns - change from integer to varchar
        {
            'table': 'patient_master',
            'column': 'phone',
            'new_type': 'VARCHAR(20)',
            'description': 'Phone number should be varchar, not integer'
        },
        {
            'table': 'patient_master', 
            'column': 'mobile',
            'new_type': 'VARCHAR(20)',
            'description': 'Mobile number should be varchar, not integer'
        },
        {
            'table': 'doctor_master',
            'column': 'phone',
            'new_type': 'VARCHAR(20)',
            'description': 'Doctor phone should be varchar, not integer'
        },
        {
            'table': 'doctor_master',
            'column': 'mobile',
            'new_type': 'VARCHAR(20)',
            'description': 'Doctor mobile should be varchar, not integer'
        },
        {
            'table': 'clinic_master',
            'column': 'phone',
            'new_type': 'VARCHAR(20)',
            'description': 'Clinic phone should be varchar, not integer'
        },
        {
            'table': 'clinic_master',
            'column': 'mobile',
            'new_type': 'VARCHAR(20)',
            'description': 'Clinic mobile should be varchar, not integer'
        },
        {
            'table': 'user_master',
            'column': 'phone',
            'new_type': 'VARCHAR(20)',
            'description': 'User phone should be varchar, not integer'
        },
        {
            'table': 'user_master',
            'column': 'mobile',
            'new_type': 'VARCHAR(20)',
            'description': 'User mobile should be varchar, not integer'
        },
        
        # Fix date columns that might be stored as double precision
        {
            'table': 'patient_master',
            'column': 'modified_on',
            'new_type': 'TIMESTAMP',
            'description': 'Modified date should be timestamp, not double'
        },
        {
            'table': 'doctor_master',
            'column': 'modified_on',
            'new_type': 'TIMESTAMP',
            'description': 'Doctor modified date should be timestamp, not double'
        },
        {
            'table': 'clinic_master',
            'column': 'modified_on',
            'new_type': 'TIMESTAMP',
            'description': 'Clinic modified date should be timestamp, not double'
        },
        {
            'table': 'user_master',
            'column': 'modified_on',
            'new_type': 'TIMESTAMP',
            'description': 'User modified date should be timestamp, not double'
        },
        
        # Fix gender_id columns that might have character values
        {
            'table': 'patient_master',
            'column': 'gender_id',
            'new_type': 'VARCHAR(10)',
            'description': 'Gender ID should be varchar to handle character values'
        },
        {
            'table': 'doctor_master',
            'column': 'gender_id',
            'new_type': 'VARCHAR(10)',
            'description': 'Doctor gender ID should be varchar to handle character values'
        },
        {
            'table': 'user_master',
            'column': 'gender_id',
            'new_type': 'VARCHAR(10)',
            'description': 'User gender ID should be varchar to handle character values'
        }
    ]
    
    successful_fixes = 0
    failed_fixes = 0
    
    for fix in data_type_fixes:
        try:
            # Check if column exists
            cursor.execute("""
                SELECT column_name, data_type 
                FROM information_schema.columns
                WHERE table_schema = 'climasys_dev' 
                AND table_name = %s 
                AND column_name = %s
            """, (fix['table'], fix['column']))
            
            column_info = cursor.fetchone()
            if not column_info:
                logger.warning(f"⚠️  Column {fix['table']}.{fix['column']} not found, skipping")
                continue
            
            current_type = column_info[1]
            if current_type.lower() == fix['new_type'].lower().replace('varchar(20)', 'character varying').replace('varchar(10)', 'character varying'):
                logger.info(f"✅ {fix['table']}.{fix['column']} already has correct type: {current_type}")
                continue
            
            # Alter column type
            alter_sql = f"""
                ALTER TABLE climasys_dev.{fix['table']} 
                ALTER COLUMN {fix['column']} TYPE {fix['new_type']}
            """
            
            cursor.execute(alter_sql)
            logger.info(f"✅ Fixed {fix['table']}.{fix['column']}: {current_type} → {fix['new_type']}")
            successful_fixes += 1
            
        except Exception as e:
            logger.error(f"❌ Failed to fix {fix['table']}.{fix['column']}: {e}")
            failed_fixes += 1
            conn.rollback()
            continue
    
    conn.commit()
    cursor.close()
    
    logger.info(f"📊 Data Type Fixes Summary:")
    logger.info(f"  - Successful fixes: {successful_fixes}")
    logger.info(f"  - Failed fixes: {failed_fixes}")
    
    return successful_fixes > 0

def add_foreign_key_constraints(conn):
    """Add foreign key constraints to establish relationships"""
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
        
        # Doctor relationships
        {
            'table': 'doctor_master',
            'column': 'gender_id',
            'ref_table': 'gender_master',
            'ref_column': 'gender_id',
            'name': 'fk_doctor_gender'
        },
        
        # User relationships
        {
            'table': 'user_master',
            'column': 'gender_id',
            'ref_table': 'gender_master',
            'ref_column': 'gender_id',
            'name': 'fk_user_gender'
        },
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
        
        # Doctor-Clinic relationships
        {
            'table': 'doctor_clinic_shift',
            'column': 'doctor_id',
            'ref_table': 'doctor_master',
            'ref_column': 'doctor_id',
            'name': 'fk_doctor_clinic_shift_doctor'
        },
        {
            'table': 'doctor_clinic_shift',
            'column': 'clinic_id',
            'ref_table': 'clinic_master',
            'ref_column': 'clinic_id',
            'name': 'fk_doctor_clinic_shift_clinic'
        },
        
        # Model relationships
        {
            'table': 'doctor_model',
            'column': 'doctor_id',
            'ref_table': 'doctor_master',
            'ref_column': 'doctor_id',
            'name': 'fk_doctor_model_doctor'
        },
        {
            'table': 'doctor_model',
            'column': 'model_id',
            'ref_table': 'model',
            'ref_column': 'model_id',
            'name': 'fk_doctor_model_model'
        },
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

def add_indexes(conn):
    """Add indexes for performance optimization"""
    logger.info("📈 Adding Performance Indexes")
    logger.info("=" * 50)
    
    cursor = conn.cursor()
    
    # Define indexes to add
    indexes = [
        # Primary lookup indexes
        {'table': 'patient_master', 'column': 'patient_id', 'name': 'idx_patient_id'},
        {'table': 'doctor_master', 'column': 'doctor_id', 'name': 'idx_doctor_id'},
        {'table': 'clinic_master', 'column': 'clinic_id', 'name': 'idx_clinic_id'},
        {'table': 'user_master', 'column': 'user_id', 'name': 'idx_user_id'},
        
        # Search indexes
        {'table': 'patient_master', 'column': 'first_name', 'name': 'idx_patient_first_name'},
        {'table': 'patient_master', 'column': 'last_name', 'name': 'idx_patient_last_name'},
        {'table': 'doctor_master', 'column': 'first_name', 'name': 'idx_doctor_first_name'},
        {'table': 'doctor_master', 'column': 'last_name', 'name': 'idx_doctor_last_name'},
        
        # Foreign key indexes
        {'table': 'patient_visits', 'column': 'patient_id', 'name': 'idx_visit_patient_id'},
        {'table': 'patient_visits', 'column': 'doctor_id', 'name': 'idx_visit_doctor_id'},
        {'table': 'patient_visits', 'column': 'clinic_id', 'name': 'idx_visit_clinic_id'},
        {'table': 'user_role', 'column': 'user_id', 'name': 'idx_user_role_user_id'},
        {'table': 'user_role', 'column': 'role_id', 'name': 'idx_user_role_role_id'},
        
        # Date indexes
        {'table': 'patient_visits', 'column': 'visit_date', 'name': 'idx_visit_date'},
        {'table': 'patient_master', 'column': 'created_on', 'name': 'idx_patient_created_on'},
        {'table': 'doctor_master', 'column': 'created_on', 'name': 'idx_doctor_created_on'},
    ]
    
    successful_indexes = 0
    failed_indexes = 0
    
    for idx in indexes:
        try:
            # Check if index already exists
            cursor.execute("""
                SELECT indexname 
                FROM pg_indexes 
                WHERE schemaname = 'climasys_dev' 
                AND tablename = %s 
                AND indexname = %s
            """, (idx['table'], idx['name']))
            
            if cursor.fetchone():
                logger.info(f"✅ Index {idx['name']} already exists")
                continue
            
            # Check if column exists
            cursor.execute("""
                SELECT COUNT(*) 
                FROM information_schema.columns
                WHERE table_schema = 'climasys_dev' 
                AND table_name = %s 
                AND column_name = %s
            """, (idx['table'], idx['column']))
            
            if cursor.fetchone()[0] == 0:
                logger.warning(f"⚠️  Column {idx['table']}.{idx['column']} not found, skipping")
                continue
            
            # Create index
            index_sql = f"""
                CREATE INDEX {idx['name']} 
                ON climasys_dev.{idx['table']} ({idx['column']})
            """
            
            cursor.execute(index_sql)
            logger.info(f"✅ Created index: {idx['name']} on {idx['table']}.{idx['column']}")
            successful_indexes += 1
            
        except Exception as e:
            logger.error(f"❌ Failed to create index {idx['name']}: {e}")
            failed_indexes += 1
            conn.rollback()
            continue
    
    conn.commit()
    cursor.close()
    
    logger.info(f"📊 Index Creation Summary:")
    logger.info(f"  - Successful indexes: {successful_indexes}")
    logger.info(f"  - Failed indexes: {failed_indexes}")
    
    return successful_indexes > 0

def verify_constraints_and_indexes(conn):
    """Verify that constraints and indexes were created successfully"""
    logger.info("🔍 Verifying Constraints and Indexes")
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
    
    # Check indexes
    cursor.execute("""
        SELECT 
            tablename,
            indexname,
            indexdef
        FROM pg_indexes 
        WHERE schemaname = 'climasys_dev'
        AND indexname NOT LIKE '%_pkey'
        ORDER BY tablename, indexname
    """)
    
    indexes = cursor.fetchall()
    logger.info(f"📋 Performance Indexes: {len(indexes)}")
    for idx in indexes:
        logger.info(f"  - {idx[0]}: {idx[1]}")
    
    cursor.close()
    
    return len(foreign_keys), len(indexes)

def main():
    """Main function"""
    logger.info("🚀 Starting Data Type Fixes and Constraint Addition")
    logger.info("=" * 70)
    
    # Get PostgreSQL connection
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Could not connect to PostgreSQL")
        return False
    
    try:
        # Step 1: Fix data types
        data_type_success = fix_data_types(conn)
        
        # Step 2: Add foreign key constraints
        constraint_success = add_foreign_key_constraints(conn)
        
        # Step 3: Add performance indexes
        index_success = add_indexes(conn)
        
        # Step 4: Verify everything
        fk_count, idx_count = verify_constraints_and_indexes(conn)
        
        # Final summary
        logger.info("🎉 Data Type and Constraint Fixes Complete!")
        logger.info("=" * 70)
        logger.info(f"📊 Summary:")
        logger.info(f"  - Data type fixes: {'✅ Success' if data_type_success else '❌ Failed'}")
        logger.info(f"  - Foreign key constraints: {'✅ Success' if constraint_success else '❌ Failed'}")
        logger.info(f"  - Performance indexes: {'✅ Success' if index_success else '❌ Failed'}")
        logger.info(f"  - Total foreign keys: {fk_count}")
        logger.info(f"  - Total indexes: {idx_count}")
        
        return data_type_success and constraint_success and index_success
        
    except Exception as e:
        logger.error(f"❌ Unexpected error: {e}")
        return False
    finally:
        conn.close()

if __name__ == "__main__":
    main()
