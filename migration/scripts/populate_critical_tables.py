#!/usr/bin/env python3
"""
Populate Critical Tables with Realistic Data
This script populates the core tables that the application needs to function.
"""

import psycopg2
import logging
import random
from datetime import datetime, date, time, timedelta
import string

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

def generate_realistic_data():
    """Generate realistic sample data"""
    # Sample data
    first_names = ['John', 'Jane', 'Michael', 'Sarah', 'David', 'Lisa', 'Robert', 'Emily', 'James', 'Jennifer', 
                   'William', 'Ashley', 'Richard', 'Jessica', 'Thomas', 'Amanda', 'Christopher', 'Stephanie', 
                   'Daniel', 'Melissa', 'Matthew', 'Nicole', 'Anthony', 'Elizabeth', 'Mark', 'Helen', 'Donald', 
                   'Samantha', 'Steven', 'Catherine', 'Paul', 'Deborah', 'Andrew', 'Rachel', 'Joshua', 'Carolyn']
    
    last_names = ['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis', 'Rodriguez', 
                  'Martinez', 'Hernandez', 'Lopez', 'Gonzalez', 'Wilson', 'Anderson', 'Thomas', 'Taylor', 'Moore', 
                  'Jackson', 'Martin', 'Lee', 'Perez', 'Thompson', 'White', 'Harris', 'Sanchez', 'Clark', 'Ramirez', 
                  'Lewis', 'Robinson', 'Walker', 'Young', 'Allen', 'King', 'Wright', 'Scott', 'Torres', 'Nguyen']
    
    specialties = ['Cardiology', 'Dermatology', 'Endocrinology', 'Gastroenterology', 'Hematology', 'Infectious Disease',
                   'Nephrology', 'Neurology', 'Oncology', 'Pediatrics', 'Psychiatry', 'Pulmonology', 'Rheumatology',
                   'Urology', 'General Medicine', 'Internal Medicine', 'Family Medicine', 'Emergency Medicine']
    
    clinic_names = ['City Medical Center', 'Downtown Clinic', 'Health Plus Medical', 'Family Care Clinic', 
                    'Metro Health Center', 'Community Medical', 'Wellness Clinic', 'Primary Care Center',
                    'Advanced Medical Group', 'Comprehensive Health', 'Regional Medical Center', 'Elite Healthcare']
    
    return {
        'first_names': first_names,
        'last_names': last_names,
        'specialties': specialties,
        'clinic_names': clinic_names
    }

def populate_clinic_master(conn, data, num_clinics=5):
    """Populate clinic_master table"""
    logger.info("🏥 Populating clinic_master...")
    
    cursor = conn.cursor()
    
    try:
        for i in range(num_clinics):
            clinic_id = f"CL-{i+1:03d}"
            clinic_name = random.choice(data['clinic_names'])
            address = f"{random.randint(100, 9999)} {random.choice(['Main St', 'Oak Ave', 'Pine Rd', 'Elm St', 'Cedar Blvd'])}"
            city = random.choice(['New York', 'Los Angeles', 'Chicago', 'Houston', 'Phoenix', 'Philadelphia', 'San Antonio', 'San Diego'])
            state = random.choice(['NY', 'CA', 'IL', 'TX', 'AZ', 'PA', 'TX', 'CA'])
            zip_code = f"{random.randint(10000, 99999)}"
            phone = f"{random.randint(200, 999)}-{random.randint(200, 999)}-{random.randint(1000, 9999)}"
            email = f"info@{clinic_name.lower().replace(' ', '')}.com"
            
            cursor.execute("""
                INSERT INTO climasys_dev.clinic_master 
                (clinic_id, clinic_name, address, city, state, zip_code, phone, email, 
                 is_active, created_on, modified_on)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (clinic_id) DO NOTHING
            """, (clinic_id, clinic_name, address, city, state, zip_code, phone, email, 
                  True, datetime.now(), datetime.now()))
        
        conn.commit()
        logger.info(f"✅ Populated clinic_master with {num_clinics} clinics")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to populate clinic_master: {e}")
        conn.rollback()
        return False
    finally:
        cursor.close()

def populate_doctor_master(conn, data, num_doctors=10):
    """Populate doctor_master table"""
    logger.info("👨‍⚕️ Populating doctor_master...")
    
    cursor = conn.cursor()
    
    try:
        for i in range(num_doctors):
            doctor_id = f"DR-{i+1:03d}"
            first_name = random.choice(data['first_names'])
            last_name = random.choice(data['last_names'])
            email = f"{first_name.lower()}.{last_name.lower()}@clinic.com"
            phone = f"{random.randint(200, 999)}-{random.randint(200, 999)}-{random.randint(1000, 9999)}"
            specialty = random.choice(data['specialties'])
            license_number = f"MD{random.randint(100000, 999999)}"
            
            cursor.execute("""
                INSERT INTO climasys_dev.doctor_master 
                (doctor_id, first_name, last_name, email, phone, specialty, license_number,
                 is_active, created_on, modified_on)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (doctor_id) DO NOTHING
            """, (doctor_id, first_name, last_name, email, phone, specialty, license_number,
                  True, datetime.now(), datetime.now()))
        
        conn.commit()
        logger.info(f"✅ Populated doctor_master with {num_doctors} doctors")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to populate doctor_master: {e}")
        conn.rollback()
        return False
    finally:
        cursor.close()

def populate_user_master(conn, data, num_users=15):
    """Populate user_master table"""
    logger.info("👤 Populating user_master...")
    
    cursor = conn.cursor()
    
    try:
        # Get existing doctors and clinics for user creation
        cursor.execute("SELECT doctor_id, first_name, last_name FROM climasys_dev.doctor_master LIMIT 5")
        doctors = cursor.fetchall()
        
        cursor.execute("SELECT clinic_id FROM climasys_dev.clinic_master LIMIT 3")
        clinics = cursor.fetchall()
        
        # Create admin users
        admin_users = [
            ('ADMIN001', 'admin', 'admin@climasys.com', 'ADMIN', 'CL-001'),
            ('ADMIN002', 'superadmin', 'superadmin@climasys.com', 'ADMIN', 'CL-001'),
        ]
        
        for user_id, username, email, role, clinic_id in admin_users:
            cursor.execute("""
                INSERT INTO climasys_dev.user_master 
                (user_id, username, email, role, clinic_id, is_active, created_on, modified_on)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (user_id) DO NOTHING
            """, (user_id, username, email, role, clinic_id, True, datetime.now(), datetime.now()))
        
        # Create doctor users
        for i, (doctor_id, first_name, last_name) in enumerate(doctors):
            user_id = f"USER-{i+1:03d}"
            username = f"{first_name.lower()}.{last_name.lower()}"
            email = f"{username}@clinic.com"
            clinic_id = random.choice(clinics)[0] if clinics else 'CL-001'
            
            cursor.execute("""
                INSERT INTO climasys_dev.user_master 
                (user_id, username, email, role, clinic_id, doctor_id, is_active, created_on, modified_on)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (user_id) DO NOTHING
            """, (user_id, username, email, 'DOCTOR', clinic_id, doctor_id, True, datetime.now(), datetime.now()))
        
        # Create receptionist users
        for i in range(3):
            user_id = f"REC-{i+1:03d}"
            first_name = random.choice(data['first_names'])
            last_name = random.choice(data['last_names'])
            username = f"{first_name.lower()}.{last_name.lower()}"
            email = f"{username}@clinic.com"
            clinic_id = random.choice(clinics)[0] if clinics else 'CL-001'
            
            cursor.execute("""
                INSERT INTO climasys_dev.user_master 
                (user_id, username, email, role, clinic_id, is_active, created_on, modified_on)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (user_id) DO NOTHING
            """, (user_id, username, email, 'RECEPTIONIST', clinic_id, True, datetime.now(), datetime.now()))
        
        conn.commit()
        logger.info("✅ Populated user_master with admin, doctor, and receptionist users")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to populate user_master: {e}")
        conn.rollback()
        return False
    finally:
        cursor.close()

def populate_patient_master(conn, data, num_patients=50):
    """Populate patient_master table"""
    logger.info("🏥 Populating patient_master...")
    
    cursor = conn.cursor()
    
    try:
        # Get available gender and blood group IDs
        cursor.execute("SELECT gender_id FROM climasys_dev.gender_master")
        gender_ids = [row[0] for row in cursor.fetchall()]
        
        cursor.execute("SELECT blood_group_id FROM climasys_dev.blood_group_master")
        blood_group_ids = [row[0] for row in cursor.fetchall()]
        
        for i in range(num_patients):
            patient_id = f"P{i+1:05d}"
            first_name = random.choice(data['first_names'])
            last_name = random.choice(data['last_names'])
            email = f"{first_name.lower()}.{last_name.lower()}@email.com"
            phone = f"{random.randint(200, 999)}-{random.randint(200, 999)}-{random.randint(1000, 9999)}"
            gender_id = random.choice(gender_ids)
            blood_group_id = random.choice(blood_group_ids)
            
            # Generate birth date (age between 18-80)
            birth_year = random.randint(1944, 2006)
            birth_month = random.randint(1, 12)
            birth_day = random.randint(1, 28)
            birth_date = date(birth_year, birth_month, birth_day)
            
            address = f"{random.randint(100, 9999)} {random.choice(['Main St', 'Oak Ave', 'Pine Rd', 'Elm St'])}"
            city = random.choice(['New York', 'Los Angeles', 'Chicago', 'Houston', 'Phoenix'])
            state = random.choice(['NY', 'CA', 'IL', 'TX', 'AZ'])
            zip_code = f"{random.randint(10000, 99999)}"
            
            cursor.execute("""
                INSERT INTO climasys_dev.patient_master 
                (patient_id, first_name, last_name, email, phone, gender_id, blood_group_id,
                 birth_date, address, city, state, zip_code, is_active, created_on, modified_on)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (patient_id) DO NOTHING
            """, (patient_id, first_name, last_name, email, phone, gender_id, blood_group_id,
                  birth_date, address, city, state, zip_code, True, datetime.now(), datetime.now()))
        
        conn.commit()
        logger.info(f"✅ Populated patient_master with {num_patients} patients")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to populate patient_master: {e}")
        conn.rollback()
        return False
    finally:
        cursor.close()

def populate_patient_visits(conn, num_visits=100):
    """Populate patient_visits table"""
    logger.info("📋 Populating patient_visits...")
    
    cursor = conn.cursor()
    
    try:
        # Get available patients, doctors, and clinics
        cursor.execute("SELECT patient_id FROM climasys_dev.patient_master LIMIT 20")
        patients = [row[0] for row in cursor.fetchall()]
        
        cursor.execute("SELECT doctor_id FROM climasys_dev.doctor_master LIMIT 5")
        doctors = [row[0] for row in cursor.fetchall()]
        
        cursor.execute("SELECT clinic_id FROM climasys_dev.clinic_master LIMIT 3")
        clinics = [row[0] for row in cursor.fetchall()]
        
        if not patients or not doctors or not clinics:
            logger.warning("⚠️  No patients, doctors, or clinics found. Skipping patient_visits.")
            return False
        
        for i in range(num_visits):
            visit_id = f"V{i+1:05d}"
            patient_id = random.choice(patients)
            doctor_id = random.choice(doctors)
            clinic_id = random.choice(clinics)
            
            # Generate visit date (last 6 months)
            visit_date = date.today() - timedelta(days=random.randint(1, 180))
            visit_time = time(random.randint(8, 17), random.choice([0, 15, 30, 45]))
            
            # Generate visit notes
            visit_notes = random.choice([
                "Routine checkup completed. Patient is in good health.",
                "Follow-up visit for previous condition. Improvement noted.",
                "Initial consultation. Treatment plan discussed.",
                "Annual physical examination. All vitals normal.",
                "Medication review and adjustment. Patient responding well."
            ])
            
            cursor.execute("""
                INSERT INTO climasys_dev.patient_visits 
                (visit_id, patient_id, doctor_id, clinic_id, visit_date, visit_time, visit_notes,
                 created_on, modified_on)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (visit_id) DO NOTHING
            """, (visit_id, patient_id, doctor_id, clinic_id, visit_date, visit_time, visit_notes,
                  datetime.now(), datetime.now()))
        
        conn.commit()
        logger.info(f"✅ Populated patient_visits with {num_visits} visits")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to populate patient_visits: {e}")
        conn.rollback()
        return False
    finally:
        cursor.close()

def main():
    """Main function"""
    logger.info("🚀 Starting Critical Tables Population")
    logger.info("=" * 70)
    
    # Get PostgreSQL connection
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Could not connect to PostgreSQL")
        return False
    
    try:
        # Generate realistic data
        data = generate_realistic_data()
        
        # Populate tables in order (respecting foreign key dependencies)
        success_count = 0
        
        if populate_clinic_master(conn, data, 5):
            success_count += 1
        
        if populate_doctor_master(conn, data, 10):
            success_count += 1
        
        if populate_user_master(conn, data, 15):
            success_count += 1
        
        if populate_patient_master(conn, data, 50):
            success_count += 1
        
        if populate_patient_visits(conn, 100):
            success_count += 1
        
        # Final summary
        logger.info("🎉 Critical Tables Population Complete!")
        logger.info("=" * 70)
        logger.info(f"📊 Summary:")
        logger.info(f"  - Tables populated successfully: {success_count}/5")
        logger.info(f"  - Population status: {'✅ Success' if success_count == 5 else '⚠️  Partial'}")
        
        return success_count == 5
        
    except Exception as e:
        logger.error(f"❌ Unexpected error: {e}")
        return False
    finally:
        conn.close()

if __name__ == "__main__":
    main()
