@echo off
echo Setting up new database for Climasys
echo ====================================

echo.
echo This script will create the new database and test the connection.
echo.

set /p host="Enter PostgreSQL host (default: localhost): "
if "%host%"=="" set host=localhost

set /p port="Enter PostgreSQL port (default: 5432): "
if "%port%"=="" set port=5432

set /p username="Enter PostgreSQL username (default: postgres): "
if "%username%"=="" set username=postgres

set /p password="Enter PostgreSQL password: "

echo.
echo Creating database 'climasys_dev_new'...
echo.

psql -h %host% -p %port% -U %username% -c "CREATE DATABASE climasys_dev_new;" 2>nul
if %errorlevel% equ 0 (
    echo ✓ Database 'climasys_dev_new' created successfully!
) else (
    echo ! Database might already exist or connection failed
)

echo.
echo Testing connection to the new database...
echo.

psql -h %host% -p %port% -U %username% -d climasys_dev_new -c "SELECT 'Connection to climasys_dev_new successful!' as status, version() as postgres_version;" 2>nul
if %errorlevel% equ 0 (
    echo.
    echo ✓ Connection to new database PASSED!
    echo.
    echo The database is ready for Hibernate to create the schema.
    echo You can now run the Spring Boot application.
    echo.
) else (
    echo.
    echo ✗ Connection to new database FAILED!
    echo.
    echo Please check:
    echo 1. PostgreSQL server is running
    echo 2. Username and password are correct
    echo 3. Host and port are correct
    echo 4. PostgreSQL client tools are installed
    echo.
)

pause
