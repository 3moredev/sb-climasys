#!/usr/bin/env python3
"""
Climasys Database Connection Test Script

This script tests the database connection and verifies
that the migration environment is properly set up.
"""

import os
import sys
import psycopg2
from psycopg2 import sql
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def test_database_connection():
    """Test PostgreSQL database connection."""
    
    # Database configuration
    db_config = {
        'host': os.getenv('DB_HOST', 'localhost'),
        'port': os.getenv('DB_PORT', '5432'),
        'database': os.getenv('DB_NAME', 'climasys_dev'),
        'user': os.getenv('DB_USER', 'postgres'),
        'password': os.getenv('DB_PASSWORD', 'root')
    }
    
    try:
        logger.info("Testing PostgreSQL connection...")
        logger.info(f"Host: {db_config['host']}")
        logger.info(f"Port: {db_config['port']}")
        logger.info(f"Database: {db_config['database']}")
        logger.info(f"User: {db_config['user']}")
        
        # Connect to database
        connection = psycopg2.connect(**db_config)
        cursor = connection.cursor()
        
        # Test basic connection
        cursor.execute("SELECT version();")
        version = cursor.fetchone()[0]
        logger.info(f"PostgreSQL version: {version}")
        
        # Test schema access
        cursor.execute("SELECT current_schema();")
        current_schema = cursor.fetchone()[0]
        logger.info(f"Current schema: {current_schema}")
        
        # Check if climasys_dev schema exists
        cursor.execute("""
            SELECT schema_name 
            FROM information_schema.schemata 
            WHERE schema_name = 'climasys_dev'
        """)
        schema_exists = cursor.fetchone()
        
        if schema_exists:
            logger.info("✅ climasys_dev schema exists")
            
            # Check tables in the schema
            cursor.execute("""
                SELECT table_name 
                FROM information_schema.tables 
                WHERE table_schema = 'climasys_dev'
                ORDER BY table_name
            """)
            tables = cursor.fetchall()
            
            if tables:
                logger.info(f"✅ Found {len(tables)} tables in climasys_dev schema:")
                for table in tables:
                    logger.info(f"  - {table[0]}")
            else:
                logger.info("ℹ️  No tables found in climasys_dev schema")
        else:
            logger.info("ℹ️  climasys_dev schema does not exist yet")
        
        # Test permissions
        cursor.execute("""
            SELECT has_schema_privilege(current_user, 'climasys_dev', 'CREATE')
        """)
        can_create = cursor.fetchone()[0]
        
        if can_create:
            logger.info("✅ User has CREATE privileges on climasys_dev schema")
        else:
            logger.warning("⚠️  User may not have CREATE privileges on climasys_dev schema")
        
        cursor.close()
        connection.close()
        
        logger.info("✅ Database connection test successful!")
        return True
        
    except psycopg2.Error as e:
        logger.error(f"❌ Database connection failed: {e}")
        return False
    except Exception as e:
        logger.error(f"❌ Unexpected error: {e}")
        return False

def test_csv_directory():
    """Test CSV directory setup."""
    
    csv_directory = os.getenv('CSV_DIRECTORY', './csv_data')
    
    logger.info(f"Testing CSV directory: {csv_directory}")
    
    if not os.path.exists(csv_directory):
        logger.info(f"ℹ️  CSV directory does not exist: {csv_directory}")
        try:
            os.makedirs(csv_directory)
            logger.info(f"✅ Created CSV directory: {csv_directory}")
        except Exception as e:
            logger.error(f"❌ Failed to create CSV directory: {e}")
            return False
    else:
        logger.info(f"✅ CSV directory exists: {csv_directory}")
    
    # Check if directory is writable
    test_file = os.path.join(csv_directory, 'test_write.tmp')
    try:
        with open(test_file, 'w') as f:
            f.write('test')
        os.remove(test_file)
        logger.info("✅ CSV directory is writable")
    except Exception as e:
        logger.error(f"❌ CSV directory is not writable: {e}")
        return False
    
    return True

def test_python_packages():
    """Test required Python packages."""
    
    required_packages = ['psycopg2', 'pandas']
    optional_packages = ['dotenv']
    
    logger.info("Testing Python packages...")
    
    all_good = True
    
    for package in required_packages:
        try:
            __import__(package)
            logger.info(f"✅ {package} is installed")
        except ImportError:
            logger.error(f"❌ {package} is not installed")
            all_good = False
    
    for package in optional_packages:
        try:
            __import__(package)
            logger.info(f"✅ {package} is installed (optional)")
        except ImportError:
            logger.info(f"ℹ️  {package} is not installed (optional)")
    
    return all_good

def main():
    """Main test function."""
    
    logger.info("🔍 Climasys Migration Environment Test")
    logger.info("=" * 50)
    
    tests = [
        ("Python Packages", test_python_packages),
        ("CSV Directory", test_csv_directory),
        ("Database Connection", test_database_connection)
    ]
    
    results = []
    
    for test_name, test_func in tests:
        logger.info(f"\n📋 Running {test_name} test...")
        result = test_func()
        results.append((test_name, result))
    
    logger.info("\n" + "=" * 50)
    logger.info("📊 Test Results Summary:")
    
    all_passed = True
    for test_name, result in results:
        status = "✅ PASS" if result else "❌ FAIL"
        logger.info(f"  {test_name}: {status}")
        if not result:
            all_passed = False
    
    if all_passed:
        logger.info("\n🎉 All tests passed! Migration environment is ready.")
        return 0
    else:
        logger.info("\n⚠️  Some tests failed. Please fix the issues before running migration.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
