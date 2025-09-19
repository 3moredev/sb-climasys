#!/usr/bin/env python3
"""
Configure SQL Server Connection
This script helps you configure the connection details for SQL Server extraction.
"""

import os
import sys

def get_connection_details():
    """Get SQL Server connection details from user"""
    print("🔧 SQL Server Connection Configuration")
    print("=" * 50)
    print("Please provide your SQL Server connection details:")
    print("")
    
    # Get server details
    server = input("Server (hostname/IP): ").strip()
    if not server:
        server = "localhost"
    
    database = input("Database name: ").strip()
    if not database:
        database = "Climasys"
    
    username = input("Username: ").strip()
    if not username:
        username = "sa"
    
    password = input("Password: ").strip()
    if not password:
        password = "root"
    
    # Get driver preference
    print("\nDriver options:")
    print("1. {SQL Server} (default)")
    print("2. {ODBC Driver 17 for SQL Server}")
    print("3. {ODBC Driver 13 for SQL Server}")
    
    driver_choice = input("Choose driver (1-3) [1]: ").strip()
    if driver_choice == "2":
        driver = "{ODBC Driver 17 for SQL Server}"
    elif driver_choice == "3":
        driver = "{ODBC Driver 13 for SQL Server}"
    else:
        driver = "{SQL Server}"
    
    return {
        'server': server,
        'database': database,
        'username': username,
        'password': password,
        'driver': driver
    }

def update_extraction_script(config):
    """Update the extraction script with new connection details"""
    script_path = "extract_real_sql_server_data.py"
    
    if not os.path.exists(script_path):
        print(f"❌ Script not found: {script_path}")
        return False
    
    # Read the current script
    with open(script_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Update the DB_CONFIG section
    old_config = """    DB_CONFIG = {
        'driver': '{SQL Server}',  # Or '{ODBC Driver 17 for SQL Server}'
        'server': 'localhost',     # Your SQL Server hostname/IP
        'database': 'Climasys',    # Your SQL Server database name
        'uid': 'sa',              # Your SQL Server username
        'pwd': 'root'             # Your SQL Server password
    }"""
    
    new_config = f"""    DB_CONFIG = {{
        'driver': '{config['driver']}',  # SQL Server driver
        'server': '{config['server']}',     # Your SQL Server hostname/IP
        'database': '{config['database']}',    # Your SQL Server database name
        'uid': '{config['username']}',              # Your SQL Server username
        'pwd': '{config['password']}'             # Your SQL Server password
    }}"""
    
    # Replace the configuration
    updated_content = content.replace(old_config, new_config)
    
    # Write the updated script
    with open(script_path, 'w', encoding='utf-8') as f:
        f.write(updated_content)
    
    print(f"✅ Updated {script_path} with your connection details")
    return True

def test_connection(config):
    """Test the SQL Server connection"""
    print("\n🔍 Testing SQL Server Connection...")
    
    try:
        import pyodbc
        
        # Build connection string
        conn_str = (
            f"DRIVER={config['driver']};"
            f"SERVER={config['server']};"
            f"DATABASE={config['database']};"
            f"UID={config['username']};"
            f"PWD={config['password']}"
        )
        
        # Test connection
        conn = pyodbc.connect(conn_str)
        cursor = conn.cursor()
        cursor.execute("SELECT @@VERSION")
        version = cursor.fetchone()[0]
        
        print(f"✅ Connection successful!")
        print(f"📋 SQL Server version: {version[:50]}...")
        
        # Test database access
        cursor.execute(f"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo'")
        table_count = cursor.fetchone()[0]
        print(f"📋 Found {table_count} tables in database '{config['database']}'")
        
        cursor.close()
        conn.close()
        
        return True
        
    except ImportError:
        print("❌ pyodbc not installed. Install it with: pip install pyodbc")
        return False
    except Exception as e:
        print(f"❌ Connection failed: {e}")
        print("\n📋 Troubleshooting tips:")
        print("- Check if SQL Server is running")
        print("- Verify server name/IP address")
        print("- Check username and password")
        print("- Ensure SQL Server accepts remote connections")
        return False

def main():
    """Main function"""
    print("🚀 SQL Server Connection Configuration Tool")
    print("=" * 60)
    print("This tool will help you configure the connection to your SQL Server database.")
    print("")
    
    # Get connection details
    config = get_connection_details()
    
    print(f"\n📋 Configuration Summary:")
    print(f"  - Server: {config['server']}")
    print(f"  - Database: {config['database']}")
    print(f"  - Username: {config['username']}")
    print(f"  - Driver: {config['driver']}")
    
    # Confirm configuration
    confirm = input("\nProceed with this configuration? (y/n) [y]: ").strip().lower()
    if confirm in ['n', 'no']:
        print("❌ Configuration cancelled")
        return
    
    # Update the extraction script
    if update_extraction_script(config):
        print("✅ Extraction script updated successfully")
    else:
        print("❌ Failed to update extraction script")
        return
    
    # Test the connection
    if test_connection(config):
        print("\n🎉 Configuration complete!")
        print("📋 Next steps:")
        print("1. Run: python extract_real_sql_server_data.py")
        print("2. Run: python import_real_sql_server_data.py")
        print("3. Run: python check_data_status.py")
    else:
        print("\n❌ Configuration failed")
        print("📋 Please check your connection details and try again")

if __name__ == "__main__":
    main()
