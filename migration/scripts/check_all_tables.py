#!/usr/bin/env python3
"""
Check all table structures in the database
"""

import psycopg2

def get_db_connection():
    """Get database connection"""
    return psycopg2.connect(
        host='localhost',
        port='5432',
        database='climasys_dev',
        user='postgres',
        password='root'
    )

def check_all_tables():
    """Check all table structures"""
    
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        # Get all tables in climasys_dev schema
        cur.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'climasys_dev' 
            ORDER BY table_name
        """)
        tables = cur.fetchall()
        
        print("=== All Tables in climasys_dev Schema ===")
        for table in tables:
            table_name = table[0]
            print(f"\n--- {table_name.upper()} ---")
            
            cur.execute("""
                SELECT column_name, data_type, is_nullable, column_default
                FROM information_schema.columns 
                WHERE table_schema = 'climasys_dev' AND table_name = %s
                ORDER BY ordinal_position
            """, (table_name,))
            
            columns = cur.fetchall()
            for col in columns:
                nullable = "NULL" if col[2] == "YES" else "NOT NULL"
                default = f" DEFAULT {col[3]}" if col[3] else ""
                print(f"  {col[0]}: {col[1]} {nullable}{default}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    check_all_tables()
