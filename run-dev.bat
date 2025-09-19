@echo off
echo Starting Climasys Backend in Development Mode...
echo.
echo Database Configuration:
echo - Host: localhost:5432
echo - Database: climasys_dev
echo - Username: postgres
echo - Password: root (or set DB_PASSWORD environment variable)
echo.
echo Starting Spring Boot application...
mvn spring-boot:run
