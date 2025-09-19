@echo off
echo Climasys Database Configuration Script
echo ======================================

echo.
echo Select database type:
echo 1. SQL Server (Default)
echo 2. MySQL
echo 3. PostgreSQL
echo 4. Exit
echo.

set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" goto sqlserver
if "%choice%"=="2" goto mysql
if "%choice%"=="3" goto postgres
if "%choice%"=="4" goto exit
goto invalid

:sqlserver
echo.
echo Configuring for SQL Server...
echo.
set /p server="Enter SQL Server host (default: localhost): "
if "%server%"=="" set server=localhost

set /p port="Enter SQL Server port (default: 1433): "
if "%port%"=="" set port=1433

set /p database="Enter database name (default: Climasys-00009): "
if "%database%"=="" set database=Climasys-00009

set /p username="Enter username (default: sa): "
if "%username%"=="" set username=sa

set /p password="Enter password: "

echo.
echo Creating application-sqlserver.yml...
(
echo # SQL Server Database Configuration
echo spring:
echo   application:
echo     name: climasys-backend
echo   datasource:
echo     url: jdbc:sqlserver://%server%:%port%;encrypt=false;databaseName=%database%
echo     username: %username%
echo     password: %password%
echo     driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
echo     hikari:
echo       maximum-pool-size: 20
echo       minimum-idle: 5
echo       connection-timeout: 30000
echo       idle-timeout: 600000
echo       max-lifetime: 1800000
echo   jackson:
echo     serialization:
echo       write-dates-as-timestamps: false
echo   jpa:
echo     hibernate:
echo       ddl-auto: validate
echo     show-sql: false
echo     properties:
echo       hibernate:
echo         dialect: org.hibernate.dialect.SQLServerDialect
echo         format_sql: false
echo.
echo server:
echo   port: 8080
echo.
echo management:
echo   endpoints:
echo     web:
echo       exposure:
echo         include: health,info
echo.
echo logging:
echo   level:
echo     org.springframework.jdbc: INFO
echo     com.zaxxer.hikari: INFO
echo     org.hibernate.SQL: INFO
echo     com.climasys: INFO
echo.
echo climasys:
echo   encryption:
echo     key: PA1ANDE61INI6
echo   jwt:
echo     secret: your-jwt-secret
echo     expiration: 86400000
) > src\main\resources\application-sqlserver.yml

echo Configuration saved to application-sqlserver.yml
echo.
echo To use this configuration, run:
echo mvn spring-boot:run -Dspring-boot.run.profiles=sqlserver
goto end

:mysql
echo.
echo Configuring for MySQL...
echo.
set /p server="Enter MySQL host (default: localhost): "
if "%server%"=="" set server=localhost

set /p port="Enter MySQL port (default: 3306): "
if "%port%"=="" set port=3306

set /p database="Enter database name (default: climasys): "
if "%database%"=="" set database=climasys

set /p username="Enter username (default: root): "
if "%username%"=="" set username=root

set /p password="Enter password: "

echo.
echo Creating application-mysql.yml...
(
echo # MySQL Database Configuration
echo spring:
echo   application:
echo     name: climasys-backend
echo   datasource:
echo     url: jdbc:mysql://%server%:%port%/%database%?useSSL=false^&serverTimezone=UTC^&allowPublicKeyRetrieval=true
echo     username: %username%
echo     password: %password%
echo     driver-class-name: com.mysql.cj.jdbc.Driver
echo     hikari:
echo       maximum-pool-size: 20
echo       minimum-idle: 5
echo       connection-timeout: 30000
echo       idle-timeout: 600000
echo       max-lifetime: 1800000
echo   jackson:
echo     serialization:
echo       write-dates-as-timestamps: false
echo   jpa:
echo     hibernate:
echo       ddl-auto: validate
echo     show-sql: false
echo     properties:
echo       hibernate:
echo         dialect: org.hibernate.dialect.MySQLDialect
echo         format_sql: false
echo.
echo server:
echo   port: 8080
echo.
echo management:
echo   endpoints:
echo     web:
echo       exposure:
echo         include: health,info
echo.
echo logging:
echo   level:
echo     org.springframework.jdbc: INFO
echo     com.zaxxer.hikari: INFO
echo     org.hibernate.SQL: INFO
echo     com.climasys: INFO
echo.
echo climasys:
echo   encryption:
echo     key: PA1ANDE61INI6
echo   jwt:
echo     secret: your-jwt-secret
echo     expiration: 86400000
) > src\main\resources\application-mysql.yml

echo Configuration saved to application-mysql.yml
echo.
echo To use this configuration, run:
echo mvn spring-boot:run -Dspring-boot.run.profiles=mysql
goto end

:postgres
echo.
echo Configuring for PostgreSQL...
echo.
set /p server="Enter PostgreSQL host (default: localhost): "
if "%server%"=="" set server=localhost

set /p port="Enter PostgreSQL port (default: 5432): "
if "%port%"=="" set port=5432

set /p database="Enter database name (default: climasys): "
if "%database%"=="" set database=climasys

set /p username="Enter username (default: postgres): "
if "%username%"=="" set username=postgres

set /p password="Enter password: "

echo.
echo Creating application-postgres.yml...
(
echo # PostgreSQL Database Configuration
echo spring:
echo   application:
echo     name: climasys-backend
echo   datasource:
echo     url: jdbc:postgresql://%server%:%port%/%database%
echo     username: %username%
echo     password: %password%
echo     driver-class-name: org.postgresql.Driver
echo     hikari:
echo       maximum-pool-size: 20
echo       minimum-idle: 5
echo       connection-timeout: 30000
echo       idle-timeout: 600000
echo       max-lifetime: 1800000
echo   jackson:
echo     serialization:
echo       write-dates-as-timestamps: false
echo   jpa:
echo     hibernate:
echo       ddl-auto: validate
echo     show-sql: false
echo     properties:
echo       hibernate:
echo         dialect: org.hibernate.dialect.PostgreSQLDialect
echo         format_sql: false
echo.
echo server:
echo   port: 8080
echo.
echo management:
echo   endpoints:
echo     web:
echo       exposure:
echo         include: health,info
echo.
echo logging:
echo   level:
echo     org.springframework.jdbc: INFO
echo     com.zaxxer.hikari: INFO
echo     org.hibernate.SQL: INFO
echo     com.climasys: INFO
echo.
echo climasys:
echo   encryption:
echo     key: PA1ANDE61INI6
echo   jwt:
echo     secret: your-jwt-secret
echo     expiration: 86400000
) > src\main\resources\application-postgres.yml

echo Configuration saved to application-postgres.yml
echo.
echo To use this configuration, run:
echo mvn spring-boot:run -Dspring-boot.run.profiles=postgres
goto end

:invalid
echo Invalid choice. Please try again.
goto end

:exit
echo Goodbye!
goto end

:end
echo.
pause
