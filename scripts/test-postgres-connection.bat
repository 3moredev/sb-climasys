@echo off
echo Testing PostgreSQL Connection for Climasys
echo ==========================================

echo.
echo This script will test the PostgreSQL database connection.
echo.

set /p host="Enter PostgreSQL host (default: localhost): "
if "%host%"=="" set host=localhost

set /p port="Enter PostgreSQL port (default: 5432): "
if "%port%"=="" set port=5432

set /p username="Enter PostgreSQL username (default: postgres): "
if "%username%"=="" set username=postgres

set /p password="Enter PostgreSQL password: "

echo.
echo Testing connection to PostgreSQL...
echo.

psql -h %host% -p %port% -U %username% -d climasys -c "SELECT 'Connection successful!' as status, version() as postgres_version;" 2>nul
if %errorlevel% equ 0 (
    echo.
    echo ✓ PostgreSQL connection test PASSED!
    echo.
    echo Testing database operations...
    echo.
    
    psql -h %host% -p %port% -U %username% -d climasys -c "CREATE TABLE IF NOT EXISTS test_connection (id SERIAL PRIMARY KEY, test_message VARCHAR(100), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" 2>nul
    if %errorlevel% equ 0 (
        echo ✓ Table creation test PASSED!
    ) else (
        echo ✗ Table creation test FAILED!
    )
    
    psql -h %host% -p %port% -U %username% -d climasys -c "INSERT INTO test_connection (test_message) VALUES ('Climasys connection test');" 2>nul
    if %errorlevel% equ 0 (
        echo ✓ Data insertion test PASSED!
    ) else (
        echo ✗ Data insertion test FAILED!
    )
    
    psql -h %host% -p %port% -U %username% -d climasys -c "SELECT * FROM test_connection WHERE test_message = 'Climasys connection test';" 2>nul
    if %errorlevel% equ 0 (
        echo ✓ Data retrieval test PASSED!
    ) else (
        echo ✗ Data retrieval test FAILED!
    )
    
    psql -h %host% -p %port% -U %username% -d climasys -c "DROP TABLE test_connection;" 2>nul
    if %errorlevel% equ 0 (
        echo ✓ Table cleanup test PASSED!
    ) else (
        echo ✗ Table cleanup test FAILED!
    )
    
    echo.
    echo All tests completed! Your PostgreSQL database is ready for Climasys.
    echo.
    echo You can now run:
    echo   mvn spring-boot:run
    echo.
) else (
    echo.
    echo ✗ PostgreSQL connection test FAILED!
    echo.
    echo Please check:
    echo 1. PostgreSQL server is running
    echo 2. Database 'climasys' exists
    echo 3. Username and password are correct
    echo 4. Host and port are correct
    echo 5. PostgreSQL client tools are installed
    echo.
)

pause
