#!/usr/bin/env python3
"""
Check existing tables in PostgreSQL
"""

import psycopg2

def get_postgresql_connection():
    """Get PostgreSQL connection"""
    return psycopg2.connect(
        host='localhost',
        port='5432',
        database='climasys_dev',
        user='postgres',
        password='root'
    )

def main():
    """Main function"""
    try:
        conn = get_postgresql_connection()
        cur = conn.cursor()
        
        # Get all tables
        cur.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'climasys_dev' 
            ORDER BY table_name;
        """)
        tables = [row[0] for row in cur.fetchall()]
        
        print(f"Existing tables ({len(tables)}):")
        for table in tables:
            print(f"  {table}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()

