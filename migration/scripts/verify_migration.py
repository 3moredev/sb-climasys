#!/usr/bin/env python3
"""
Verify the migration results
"""

import psycopg2

def verify_migration():
    """Verify the migration results"""
    
    try:
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        cur = conn.cursor()
        
        print("=== Final Data Counts ===")
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
        
        print(f"\n📊 Total Records Migrated: {total_records:,}")
        
        # Test some sample data
        print("\n=== Sample Data Verification ===")
        
        # Check doctors
        cur.execute("SELECT doctor_id, first_name, last_name FROM climasys_dev.doctor_master LIMIT 3")
        doctors = cur.fetchall()
        print("Sample Doctors:")
        for doctor in doctors:
            print(f"  {doctor[0]}: {doctor[1]} {doctor[2]}")
        
        # Check patients
        cur.execute("SELECT patient_id, first_name, last_name FROM climasys_dev.patient_master LIMIT 3")
        patients = cur.fetchall()
        print("\nSample Patients:")
        for patient in patients:
            print(f"  {patient[0]}: {patient[1]} {patient[2]}")
        
        # Check users
        cur.execute("SELECT user_id, username, user_role FROM climasys_dev.user_master LIMIT 3")
        users = cur.fetchall()
        print("\nSample Users:")
        for user in users:
            print(f"  {user[0]}: {user[1]} ({user[2]})")
        
        cur.close()
        conn.close()
        
        print("\n✅ Migration verification completed successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Error during verification: {e}")
        return False

if __name__ == "__main__":
    verify_migration()
