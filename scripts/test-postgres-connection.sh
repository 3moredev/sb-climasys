#!/bin/bash

echo "Testing PostgreSQL Connection for Climasys"
echo "=========================================="

echo ""
echo "This script will test the PostgreSQL database connection."
echo ""

read -p "Enter PostgreSQL host (default: localhost): " host
host=${host:-localhost}

read -p "Enter PostgreSQL port (default: 5432): " port
port=${port:-5432}

read -p "Enter PostgreSQL username (default: postgres): " username
username=${username:-postgres}

read -s -p "Enter PostgreSQL password: " password
echo ""

echo ""
echo "Testing connection to PostgreSQL..."
echo ""

PGPASSWORD=$password psql -h $host -p $port -U $username -d climasys -c "SELECT 'Connection successful!' as status, version() as postgres_version;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo ""
    echo "✓ PostgreSQL connection test PASSED!"
    echo ""
    echo "Testing database operations..."
    echo ""
    
    PGPASSWORD=$password psql -h $host -p $port -U $username -d climasys -c "CREATE TABLE IF NOT EXISTS test_connection (id SERIAL PRIMARY KEY, test_message VARCHAR(100), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✓ Table creation test PASSED!"
    else
        echo "✗ Table creation test FAILED!"
    fi
    
    PGPASSWORD=$password psql -h $host -p $port -U $username -d climasys -c "INSERT INTO test_connection (test_message) VALUES ('Climasys connection test');" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✓ Data insertion test PASSED!"
    else
        echo "✗ Data insertion test FAILED!"
    fi
    
    PGPASSWORD=$password psql -h $host -p $port -U $username -d climasys -c "SELECT * FROM test_connection WHERE test_message = 'Climasys connection test';" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✓ Data retrieval test PASSED!"
    else
        echo "✗ Data retrieval test FAILED!"
    fi
    
    PGPASSWORD=$password psql -h $host -p $port -U $username -d climasys -c "DROP TABLE test_connection;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✓ Table cleanup test PASSED!"
    else
        echo "✗ Table cleanup test FAILED!"
    fi
    
    echo ""
    echo "All tests completed! Your PostgreSQL database is ready for Climasys."
    echo ""
    echo "You can now run:"
    echo "  mvn spring-boot:run"
    echo ""
else
    echo ""
    echo "✗ PostgreSQL connection test FAILED!"
    echo ""
    echo "Please check:"
    echo "1. PostgreSQL server is running"
    echo "2. Database 'climasys' exists"
    echo "3. Username and password are correct"
    echo "4. Host and port are correct"
    echo "5. PostgreSQL client tools are installed"
    echo ""
fi
