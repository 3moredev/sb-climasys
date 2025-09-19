#!/usr/bin/env python3
"""
Comprehensive Data Import Script for All Tables
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

def import_table_data(table_name, csv_file, column_mapping=None):
    """Generic function to import data for any table"""
    print(f"Importing {table_name}...")
    
    try:
        if not os.path.exists(csv_file):
            print(f"  ⚠️  CSV file not found: {csv_file}")
            return False
        
        df = pd.read_csv(csv_file, low_memory=False)
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        imported_count = 0
        for _, row in df.iterrows():
            try:
                # Build dynamic INSERT query based on CSV columns
                columns = list(df.columns)
                placeholders = ['%s'] * len(columns)
                
                query = f"""
                    INSERT INTO climasys_dev.{table_name} ({', '.join(columns)})
                    VALUES ({', '.join(placeholders)})
                    ON CONFLICT DO NOTHING
                """
                
                values = [row[col] for col in columns]
                cur.execute(query, values)
                imported_count += 1
                
                if imported_count % 1000 == 0:
                    print(f"  Imported {imported_count} records...")
                    conn.commit()
                    
            except Exception as e:
                print(f"  Warning: Skipping record: {e}")
                continue
        
        conn.commit()
        cur.close()
        conn.close()
        print(f"✅ Imported {imported_count} {table_name} records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing {table_name}: {e}")
        return False

def main():
    """Main import function"""
    print("🚀 Starting comprehensive data import...")
    
    # Define all tables and their CSV files
    tables_to_import = [
        ('language_master', 'csv_data/language_master.csv'),
        ('role_master', 'csv_data/role_master.csv'),
        ('gender_master', 'csv_data/gender_master.csv'),
        ('blood_group_master', 'csv_data/blood_group_master.csv'),
        ('doctor_master', 'csv_data/doctor_master.csv'),
        ('clinic_master', 'csv_data/clinic_master.csv'),
        ('user_master', 'csv_data/user_master.csv'),
        ('user_role', 'csv_data/user_role.csv'),
        ('patient_master', 'csv_data/patient_master.csv'),
        ('patient_visits', 'csv_data/patient_visits.csv'),
        ('medicine_master', 'csv_data/medicine_master.csv'),
        ('prescription_master', 'csv_data/prescription_master.csv'),
        ('visit_prescription_overwrite', 'csv_data/visit_prescription_overwrite.csv'),
        ('billing_master', 'csv_data/billing_master.csv'),
        ('billing_details', 'csv_data/billing_details.csv'),
        ('lab_test_master', 'csv_data/lab_test_master.csv'),
        ('patient_lab_tests', 'csv_data/patient_lab_tests.csv'),
        ('insurance_company_master', 'csv_data/insurance_company_master.csv'),
        ('patient_insurance', 'csv_data/patient_insurance.csv'),
        ('appointment_master', 'csv_data/appointment_master.csv'),
        ('family_relationship_master', 'csv_data/family_relationship_master.csv'),
        ('patient_family', 'csv_data/patient_family.csv'),
    ]
    
    success_count = 0
    for table_name, csv_file in tables_to_import:
        if import_table_data(table_name, csv_file):
            success_count += 1
    
    print(f"\n📊 Import Summary: {success_count}/{len(tables_to_import)} tables imported successfully")
    
    if success_count == len(tables_to_import):
        print("🎉 All data imported successfully!")
    else:
        print("⚠️  Some imports failed. Check the error messages above.")

if __name__ == "__main__":
    main()
