#!/bin/bash

echo "PostgreSQL Setup Script for Climasys"
echo "===================================="

echo ""
echo "This script will help you set up PostgreSQL for Climasys backend."
echo ""

echo "Checking if PostgreSQL is installed..."
if ! command -v psql &> /dev/null; then
    echo "PostgreSQL is not installed or not in PATH."
    echo "Please install PostgreSQL from: https://www.postgresql.org/download/"
    echo ""
    exit 1
fi

echo "PostgreSQL found!"
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
echo "Creating database 'climasys'..."
echo ""

PGPASSWORD=$password psql -h $host -p $port -U $username -c "CREATE DATABASE climasys;" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "Database 'climasys' might already exist or there was an error."
    echo "Continuing..."
fi

echo ""
echo "Setting up environment variables..."
echo ""

echo "Creating .env file..."
cat > .env << EOF
# PostgreSQL Configuration
DB_PASSWORD=$password
DB_HOST=$host
DB_PORT=$port
DB_NAME=climasys
DB_USERNAME=$username

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-in-production

# Climasys Configuration
CLIMASYS_ENCRYPTION_KEY=PA1ANDE61INI6

# Spring Profile
SPRING_PROFILES_ACTIVE=postgres
EOF

echo ".env file created successfully!"
echo ""

echo "Testing database connection..."
PGPASSWORD=$password psql -h $host -p $port -U $username -d climasys -c "SELECT version();" >/dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✓ Database connection successful!"
else
    echo "✗ Database connection failed. Please check your credentials."
    echo ""
    exit 1
fi

echo ""
echo "PostgreSQL setup completed successfully!"
echo ""
echo "Next steps:"
echo "1. Make sure your PostgreSQL server is running"
echo "2. Run: mvn clean install"
echo "3. Run: mvn spring-boot:run"
echo ""
echo "Your database configuration:"
echo "- Host: $host"
echo "- Port: $port"
echo "- Database: climasys"
echo "- Username: $username"
echo ""
