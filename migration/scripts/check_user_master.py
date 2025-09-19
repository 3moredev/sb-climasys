#!/usr/bin/env python3
"""
Check user_master table structure
"""

import psycopg2

def check_user_master():
    """Check user_master table structure"""
    
    try:
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        cur = conn.cursor()
        
        print("=== User Master Table Structure ===")
        cur.execute("""
            SELECT column_name, data_type, is_nullable, column_default
            FROM information_schema.columns 
            WHERE table_schema = 'climasys_dev' AND table_name = 'user_master' 
            ORDER BY ordinal_position
        """)
        
        for row in cur.fetchall():
            nullable = "NULL" if row[2] == "YES" else "NOT NULL"
            default = f" DEFAULT {row[3]}" if row[3] else ""
            print(f"  {row[0]}: {row[1]} {nullable}{default}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    check_user_master()
