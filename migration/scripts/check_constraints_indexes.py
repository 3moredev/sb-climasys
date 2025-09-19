#!/usr/bin/env python3
"""
Check constraints and indexes in PostgreSQL tables
"""

import psycopg2

def check_constraints_and_indexes():
    """Check constraints and indexes in all tables"""
    
    try:
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        cur = conn.cursor()
        
        print("=== CHECKING CONSTRAINTS AND INDEXES ===")
        
        # Get all tables
        cur.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'climasys_dev' 
            ORDER BY table_name
        """)
        tables = cur.fetchall()
        
        for table in tables:
            table_name = table[0]
            print(f"\n--- {table_name.upper()} ---")
            
            # Check primary keys
            cur.execute("""
                SELECT column_name, constraint_name
                FROM information_schema.key_column_usage 
                WHERE table_schema = 'climasys_dev' 
                AND table_name = %s 
                AND constraint_name LIKE '%_pkey'
            """, (table_name,))
            primary_keys = cur.fetchall()
            if primary_keys:
                print("  Primary Keys:")
                for pk in primary_keys:
                    print(f"    - {pk[0]} ({pk[1]})")
            
            # Check foreign keys
            cur.execute("""
                SELECT 
                    kcu.column_name,
                    ccu.table_name AS foreign_table_name,
                    ccu.column_name AS foreign_column_name,
                    tc.constraint_name
                FROM information_schema.table_constraints AS tc 
                JOIN information_schema.key_column_usage AS kcu
                    ON tc.constraint_name = kcu.constraint_name
                    AND tc.table_schema = kcu.table_schema
                JOIN information_schema.constraint_column_usage AS ccu
                    ON ccu.constraint_name = tc.constraint_name
                    AND ccu.table_schema = tc.table_schema
                WHERE tc.constraint_type = 'FOREIGN KEY' 
                AND tc.table_schema = 'climasys_dev'
                AND tc.table_name = %s
            """, (table_name,))
            foreign_keys = cur.fetchall()
            if foreign_keys:
                print("  Foreign Keys:")
                for fk in foreign_keys:
                    print(f"    - {fk[0]} -> {fk[1]}.{fk[2]} ({fk[3]})")
            
            # Check unique constraints
            cur.execute("""
                SELECT column_name, constraint_name
                FROM information_schema.key_column_usage 
                WHERE table_schema = 'climasys_dev' 
                AND table_name = %s 
                AND constraint_name LIKE '%_key'
                AND constraint_name NOT LIKE '%_pkey'
            """, (table_name,))
            unique_constraints = cur.fetchall()
            if unique_constraints:
                print("  Unique Constraints:")
                for uc in unique_constraints:
                    print(f"    - {uc[0]} ({uc[1]})")
            
            # Check indexes
            cur.execute("""
                SELECT indexname, indexdef
                FROM pg_indexes 
                WHERE schemaname = 'climasys_dev' 
                AND tablename = %s
                ORDER BY indexname
            """, (table_name,))
            indexes = cur.fetchall()
            if indexes:
                print("  Indexes:")
                for idx in indexes:
                    print(f"    - {idx[0]}")
                    print(f"      {idx[1]}")
        
        # Check for missing constraints that should exist
        print(f"\n=== MISSING CONSTRAINTS ANALYSIS ===")
        
        # Check if we have proper foreign key relationships
        expected_fks = [
            ('patient_master', 'doctor_id', 'doctor_master', 'doctor_id'),
            ('patient_visits', 'patient_id', 'patient_master', 'patient_id'),
            ('patient_visits', 'doctor_id', 'doctor_master', 'doctor_id'),
            ('user_master', 'doctor_id', 'doctor_master', 'doctor_id'),
            ('user_role', 'user_id', 'user_master', 'user_id'),
            ('user_role', 'role_id', 'role_master', 'role_id'),
        ]
        
        for table, column, ref_table, ref_column in expected_fks:
            cur.execute("""
                SELECT COUNT(*)
                FROM information_schema.table_constraints tc
                JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name
                WHERE tc.table_schema = 'climasys_dev'
                AND tc.table_name = %s
                AND kcu.column_name = %s
                AND tc.constraint_type = 'FOREIGN KEY'
            """, (table, column))
            fk_exists = cur.fetchone()[0] > 0
            
            if not fk_exists:
                print(f"  ❌ Missing FK: {table}.{column} -> {ref_table}.{ref_column}")
            else:
                print(f"  ✅ FK exists: {table}.{column} -> {ref_table}.{ref_column}")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    check_constraints_and_indexes()

