@echo off
echo PostgreSQL Setup Script for Climasys
echo ====================================

echo.
echo This script will help you set up PostgreSQL for Climasys backend.
echo.

echo Checking if PostgreSQL is installed...
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo PostgreSQL is not installed or not in PATH.
    echo Please install PostgreSQL from: https://www.postgresql.org/download/
    echo.
    pause
    exit /b 1
)

echo PostgreSQL found!
echo.

set /p host="Enter PostgreSQL host (default: localhost): "
if "%host%"=="" set host=localhost

set /p port="Enter PostgreSQL port (default: 5432): "
if "%port%"=="" set port=5432

set /p username="Enter PostgreSQL username (default: postgres): "
if "%username%"=="" set username=postgres

set /p password="Enter PostgreSQL password: "

echo.
echo Creating database 'climasys'...
echo.

psql -h %host% -p %port% -U %username% -c "CREATE DATABASE climasys;" 2>nul
if %errorlevel% neq 0 (
    echo Database 'climasys' might already exist or there was an error.
    echo Continuing...
)

echo.
echo Setting up environment variables...
echo.

echo Creating .env file...
(
echo # PostgreSQL Configuration
echo DB_PASSWORD=%password%
echo DB_HOST=%host%
echo DB_PORT=%port%
echo DB_NAME=climasys
echo DB_USERNAME=%username%
echo.
echo # JWT Configuration
echo JWT_SECRET=your-super-secret-jwt-key-change-in-production
echo.
echo # Climasys Configuration
echo CLIMASYS_ENCRYPTION_KEY=PA1ANDE61INI6
echo.
echo # Spring Profile
echo SPRING_PROFILES_ACTIVE=postgres
) > .env

echo .env file created successfully!
echo.

echo Testing database connection...
psql -h %host% -p %port% -U %username% -d climasys -c "SELECT version();" >nul 2>nul
if %errorlevel% equ 0 (
    echo ✓ Database connection successful!
) else (
    echo ✗ Database connection failed. Please check your credentials.
    echo.
    pause
    exit /b 1
)

echo.
echo PostgreSQL setup completed successfully!
echo.
echo Next steps:
echo 1. Make sure your PostgreSQL server is running
echo 2. Run: mvn clean install
echo 3. Run: mvn spring-boot:run
echo.
echo Your database configuration:
echo - Host: %host%
echo - Port: %port%
echo - Database: climasys
echo - Username: %username%
echo.
pause
