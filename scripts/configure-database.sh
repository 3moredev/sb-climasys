#!/bin/bash

echo "Climasys Database Configuration Script"
echo "======================================"

echo ""
echo "Select database type:"
echo "1. SQL Server (Default)"
echo "2. MySQL"
echo "3. PostgreSQL"
echo "4. Exit"
echo ""

read -p "Enter your choice (1-4): " choice

case $choice in
    1)
        echo ""
        echo "Configuring for SQL Server..."
        echo ""
        read -p "Enter SQL Server host (default: localhost): " server
        server=${server:-localhost}
        
        read -p "Enter SQL Server port (default: 1433): " port
        port=${port:-1433}
        
        read -p "Enter database name (default: Climasys-00009): " database
        database=${database:-Climasys-00009}
        
        read -p "Enter username (default: sa): " username
        username=${username:-sa}
        
        read -s -p "Enter password: " password
        echo ""
        
        echo ""
        echo "Creating application-sqlserver.yml..."
        cat > src/main/resources/application-sqlserver.yml << EOF
# SQL Server Database Configuration
spring:
  application:
    name: climasys-backend
  datasource:
    url: jdbc:sqlserver://$server:$port;encrypt=false;databaseName=$database
    username: $username
    password: $password
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jackson:
    serialization:
      write-dates-as-timestamps: false
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.SQLServerDialect
        format_sql: false

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info

logging:
  level:
    org.springframework.jdbc: INFO
    com.zaxxer.hikari: INFO
    org.hibernate.SQL: INFO
    com.climasys: INFO

climasys:
  encryption:
    key: PA1ANDE61INI6
  jwt:
    secret: your-jwt-secret
    expiration: 86400000
EOF
        
        echo "Configuration saved to application-sqlserver.yml"
        echo ""
        echo "To use this configuration, run:"
        echo "mvn spring-boot:run -Dspring-boot.run.profiles=sqlserver"
        ;;
        
    2)
        echo ""
        echo "Configuring for MySQL..."
        echo ""
        read -p "Enter MySQL host (default: localhost): " server
        server=${server:-localhost}
        
        read -p "Enter MySQL port (default: 3306): " port
        port=${port:-3306}
        
        read -p "Enter database name (default: climasys): " database
        database=${database:-climasys}
        
        read -p "Enter username (default: root): " username
        username=${username:-root}
        
        read -s -p "Enter password: " password
        echo ""
        
        echo ""
        echo "Creating application-mysql.yml..."
        cat > src/main/resources/application-mysql.yml << EOF
# MySQL Database Configuration
spring:
  application:
    name: climasys-backend
  datasource:
    url: jdbc:mysql://$server:$port/$database?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: $username
    password: $password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jackson:
    serialization:
      write-dates-as-timestamps: false
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: false

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info

logging:
  level:
    org.springframework.jdbc: INFO
    com.zaxxer.hikari: INFO
    org.hibernate.SQL: INFO
    com.climasys: INFO

climasys:
  encryption:
    key: PA1ANDE61INI6
  jwt:
    secret: your-jwt-secret
    expiration: 86400000
EOF
        
        echo "Configuration saved to application-mysql.yml"
        echo ""
        echo "To use this configuration, run:"
        echo "mvn spring-boot:run -Dspring-boot.run.profiles=mysql"
        ;;
        
    3)
        echo ""
        echo "Configuring for PostgreSQL..."
        echo ""
        read -p "Enter PostgreSQL host (default: localhost): " server
        server=${server:-localhost}
        
        read -p "Enter PostgreSQL port (default: 5432): " port
        port=${port:-5432}
        
        read -p "Enter database name (default: climasys): " database
        database=${database:-climasys}
        
        read -p "Enter username (default: postgres): " username
        username=${username:-postgres}
        
        read -s -p "Enter password: " password
        echo ""
        
        echo ""
        echo "Creating application-postgres.yml..."
        cat > src/main/resources/application-postgres.yml << EOF
# PostgreSQL Database Configuration
spring:
  application:
    name: climasys-backend
  datasource:
    url: jdbc:postgresql://$server:$port/$database
    username: $username
    password: $password
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jackson:
    serialization:
      write-dates-as-timestamps: false
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info

logging:
  level:
    org.springframework.jdbc: INFO
    com.zaxxer.hikari: INFO
    org.hibernate.SQL: INFO
    com.climasys: INFO

climasys:
  encryption:
    key: PA1ANDE61INI6
  jwt:
    secret: your-jwt-secret
    expiration: 86400000
EOF
        
        echo "Configuration saved to application-postgres.yml"
        echo ""
        echo "To use this configuration, run:"
        echo "mvn spring-boot:run -Dspring-boot.run.profiles=postgres"
        ;;
        
    4)
        echo "Goodbye!"
        exit 0
        ;;
        
    *)
        echo "Invalid choice. Please try again."
        ;;
esac

echo ""
read -p "Press Enter to continue..."
