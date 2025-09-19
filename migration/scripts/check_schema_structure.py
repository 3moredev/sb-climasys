#!/usr/bin/env python3
"""
Check schema structure, constraints, and indexes
"""

import psycopg2

def check_schema_structure():
    """Check schema structure"""
    
    try:
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        cur = conn.cursor()
        
        print("=== SCHEMA STRUCTURE ANALYSIS ===")
        
        # Check primary keys
        print("\n--- PRIMARY KEYS ---")
        cur.execute("""
            SELECT 
                tc.table_name,
                kcu.column_name,
                tc.constraint_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu 
                ON tc.constraint_name = kcu.constraint_name
            WHERE tc.table_schema = 'climasys_dev'
            AND tc.constraint_type = 'PRIMARY KEY'
            ORDER BY tc.table_name
        """)
        primary_keys = cur.fetchall()
        for pk in primary_keys:
            print(f"  {pk[0]}.{pk[1]} ({pk[2]})")
        
        # Check foreign keys
        print("\n--- FOREIGN KEYS ---")
        cur.execute("""
            SELECT 
                tc.table_name,
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
            ORDER BY tc.table_name
        """)
        foreign_keys = cur.fetchall()
        for fk in foreign_keys:
            print(f"  {fk[0]}.{fk[1]} -> {fk[2]}.{fk[3]}")
        
        # Check unique constraints
        print("\n--- UNIQUE CONSTRAINTS ---")
        cur.execute("""
            SELECT 
                tc.table_name,
                kcu.column_name,
                tc.constraint_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu 
                ON tc.constraint_name = kcu.constraint_name
            WHERE tc.table_schema = 'climasys_dev'
            AND tc.constraint_type = 'UNIQUE'
            ORDER BY tc.table_name
        """)
        unique_constraints = cur.fetchall()
        for uc in unique_constraints:
            print(f"  {uc[0]}.{uc[1]} ({uc[2]})")
        
        # Check indexes
        print("\n--- INDEXES ---")
        cur.execute("""
            SELECT 
                tablename,
                indexname,
                indexdef
            FROM pg_indexes 
            WHERE schemaname = 'climasys_dev' 
            ORDER BY tablename, indexname
        """)
        indexes = cur.fetchall()
        for idx in indexes:
            print(f"  {idx[0]}.{idx[1]}")
        
        # Check table structures
        print("\n--- TABLE STRUCTURES ---")
        cur.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'climasys_dev' 
            ORDER BY table_name
        """)
        tables = cur.fetchall()
        
        for table in tables[:5]:  # Check first 5 tables
            table_name = table[0]
            print(f"\n  {table_name}:")
            cur.execute("""
                SELECT 
                    column_name, 
                    data_type, 
                    is_nullable,
                    column_default
                FROM information_schema.columns 
                WHERE table_schema = 'climasys_dev' 
                AND table_name = %s 
                ORDER BY ordinal_position
            """, (table_name,))
            columns = cur.fetchall()
            for col in columns:
                nullable = "NULL" if col[2] == "YES" else "NOT NULL"
                default = f" DEFAULT {col[3]}" if col[3] else ""
                print(f"    {col[0]}: {col[1]} {nullable}{default}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    check_schema_structure()

