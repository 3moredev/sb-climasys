#!/usr/bin/env python3
"""
Import user master data with correct column names
"""

import psycopg2
import pandas as pd
from datetime import datetime

def import_user_master():
    """Import user master data"""
    print("Importing User Master...")
    
    try:
        df = pd.read_csv('extracted_data/User_Master.csv')
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        cur = conn.cursor()
        
        for _, row in df.iterrows():
            cur.execute("""
                INSERT INTO climasys_dev.user_master (
                    user_id, username, password_hash, user_role, email, 
                    clinic_id, doctor_id, language_id, is_active, created_on, modified_on
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (user_id) DO NOTHING
            """, (
                str(row.get('User_Id', '')),
                row.get('Username', ''),
                row.get('Password', ''),
                row.get('User_Role', ''),
                row.get('Email', ''),
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
        print(f"✅ Imported {len(df)} user master records")
        return True
        
    except Exception as e:
        print(f"❌ Error importing user master: {e}")
        return False

if __name__ == "__main__":
    import_user_master()
