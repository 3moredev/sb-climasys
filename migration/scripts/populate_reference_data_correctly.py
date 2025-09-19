#!/usr/bin/env python3
"""
Populate Reference Data Correctly
This script populates reference tables with proper data using the correct column names.
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

def populate_reference_data(conn):
    """Populate reference tables with proper data"""
    logger.info("📝 Populating Reference Data")
    logger.info("=" * 50)
    
    cursor = conn.cursor()
    
    try:
        # Insert gender data
        cursor.execute("""
            INSERT INTO climasys_dev.gender_master (gender_id, gender_name, created_on, modified_on)
            VALUES 
                ('M', 'Male', NOW(), NOW()),
                ('F', 'Female', NOW(), NOW()),
                ('O', 'Other', NOW(), NOW())
            ON CONFLICT (gender_id) DO NOTHING
        """)
        logger.info("✅ Populated gender_master")
        
        # Insert blood group data
        cursor.execute("""
            INSERT INTO climasys_dev.blood_group_master (blood_group_id, blood_group_name, created_on, modified_on)
            VALUES 
                ('A+', 'A Positive', NOW(), NOW()),
                ('A-', 'A Negative', NOW(), NOW()),
                ('B+', 'B Positive', NOW(), NOW()),
                ('B-', 'B Negative', NOW(), NOW()),
                ('AB+', 'AB Positive', NOW(), NOW()),
                ('AB-', 'AB Negative', NOW(), NOW()),
                ('O+', 'O Positive', NOW(), NOW()),
                ('O-', 'O Negative', NOW(), NOW())
            ON CONFLICT (blood_group_id) DO NOTHING
        """)
        logger.info("✅ Populated blood_group_master")
        
        # Insert role data
        cursor.execute("""
            INSERT INTO climasys_dev.role_master (role_id, role_name, created_on, modified_on, "CreatedBy_Name", "ModifiedBy_Name")
            VALUES 
                ('ADMIN', 'Administrator', NOW(), NOW(), 'System', 'System'),
                ('DOCTOR', 'Doctor', NOW(), NOW(), 'System', 'System'),
                ('NURSE', 'Nurse', NOW(), NOW(), 'System', 'System'),
                ('RECEPTIONIST', 'Receptionist', NOW(), NOW(), 'System', 'System'),
                ('PATIENT', 'Patient', NOW(), NOW(), 'System', 'System')
            ON CONFLICT (role_id) DO NOTHING
        """)
        logger.info("✅ Populated role_master")
        
        # Insert model data
        cursor.execute("""
            INSERT INTO climasys_dev.model (model_id, model_name, model_description, is_active, created_on, modified_on, "Model_Number")
            VALUES 
                ('1', 'Basic Model', 'Basic clinic management model', true, NOW(), NOW(), 1),
                ('2', 'Premium Model', 'Premium clinic management model', true, NOW(), NOW(), 2),
                ('3', 'Enterprise Model', 'Enterprise clinic management model', true, NOW(), NOW(), 3)
            ON CONFLICT (model_id) DO NOTHING
        """)
        logger.info("✅ Populated model")
        
        # Insert language data
        cursor.execute("""
            INSERT INTO climasys_dev.language_master (language_id, language_name, created_on, modified_on, "Is_Default")
            VALUES 
                ('EN', 'English', NOW(), NOW(), 'Y'),
                ('HI', 'Hindi', NOW(), NOW(), 'N'),
                ('ES', 'Spanish', NOW(), NOW(), 'N')
            ON CONFLICT (language_id) DO NOTHING
        """)
        logger.info("✅ Populated language_master")
        
        # Insert shift data
        cursor.execute("""
            INSERT INTO climasys_dev.shift_master (shift_id, shift_name, shift_description, shift_day, start_time, end_time, created_on, modified_on, "Description", "CreatedBy_Name", "ModifiedBy_Name")
            VALUES 
                ('MORNING', 'Morning Shift', 'Morning shift from 8 AM to 4 PM', 'Monday-Friday', '08:00:00', '16:00:00', NOW(), NOW(), 'Morning shift', 'System', 'System'),
                ('EVENING', 'Evening Shift', 'Evening shift from 4 PM to 12 AM', 'Monday-Friday', '16:00:00', '00:00:00', NOW(), NOW(), 'Evening shift', 'System', 'System'),
                ('NIGHT', 'Night Shift', 'Night shift from 12 AM to 8 AM', 'Monday-Friday', '00:00:00', '08:00:00', NOW(), NOW(), 'Night shift', 'System', 'System')
            ON CONFLICT (shift_id) DO NOTHING
        """)
        logger.info("✅ Populated shift_master")
        
        conn.commit()
        logger.info("✅ All reference data populated successfully")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to populate reference data: {e}")
        conn.rollback()
        return False
    finally:
        cursor.close()

def verify_reference_data(conn):
    """Verify reference data was populated correctly"""
    logger.info("🔍 Verifying Reference Data")
    logger.info("=" * 50)
    
    cursor = conn.cursor()
    
    reference_tables = [
        'gender_master',
        'blood_group_master',
        'role_master',
        'model',
        'language_master',
        'shift_master'
    ]
    
    total_records = 0
    for table in reference_tables:
        try:
            cursor.execute(f"SELECT COUNT(*) FROM climasys_dev.{table}")
            count = cursor.fetchone()[0]
            total_records += count
            logger.info(f"  - {table}: {count} records")
        except Exception as e:
            logger.warning(f"  - {table}: Error getting count - {e}")
    
    logger.info(f"  - Total reference records: {total_records}")
    cursor.close()
    
    return total_records

def main():
    """Main function"""
    logger.info("🚀 Starting Reference Data Population")
    logger.info("=" * 70)
    
    # Get PostgreSQL connection
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Could not connect to PostgreSQL")
        return False
    
    try:
        # Populate reference data
        success = populate_reference_data(conn)
        
        if success:
            # Verify the data
            total_records = verify_reference_data(conn)
            
            # Final summary
            logger.info("🎉 Reference Data Population Complete!")
            logger.info("=" * 70)
            logger.info(f"📊 Summary:")
            logger.info(f"  - Reference data population: {'✅ Success' if success else '❌ Failed'}")
            logger.info(f"  - Total reference records: {total_records}")
            
            return success
        else:
            return False
        
    except Exception as e:
        logger.error(f"❌ Unexpected error: {e}")
        return False
    finally:
        conn.close()

if __name__ == "__main__":
    main()
