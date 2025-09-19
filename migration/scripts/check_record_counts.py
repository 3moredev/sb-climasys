#!/usr/bin/env python3
"""
Check actual record counts in all tables
"""

import psycopg2

def check_record_counts():
    """Check record counts in all tables"""
    
    try:
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        cur = conn.cursor()
        
        print("=== Current Record Counts ===")
        
        # Get all tables
        cur.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'climasys_dev' 
            ORDER BY table_name
        """)
        tables = cur.fetchall()
        
        total_records = 0
        for table in tables:
            table_name = table[0]
            cur.execute(f"SELECT COUNT(*) FROM climasys_dev.{table_name}")
            count = cur.fetchone()[0]
            total_records += count
            print(f"  {table_name}: {count:,} records")
        
        print(f"\n📊 Total Records Across All Tables: {total_records:,}")
        
        # Check CSV file sizes for comparison
        print("\n=== CSV File Sizes for Comparison ===")
        import pandas as pd
        import os
        
        csv_files = [
            'extracted_data/Language_Master.csv',
            'extracted_data/Role_Master.csv', 
            'extracted_data/Gender_Master.csv',
            'extracted_data/Doctor_Master.csv',
            'extracted_data/Clinic_Master.csv',
            'extracted_data/User_Master.csv',
            'extracted_data/Patient_Master.csv',
            'extracted_data/Patient_Visits.csv'
        ]
        
        for csv_file in csv_files:
            if os.path.exists(csv_file):
                try:
                    df = pd.read_csv(csv_file, low_memory=False)
                    print(f"  {csv_file}: {len(df):,} records")
                except Exception as e:
                    print(f"  {csv_file}: Error reading - {e}")
            else:
                print(f"  {csv_file}: File not found")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    check_record_counts()

