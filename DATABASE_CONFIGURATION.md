# Database Configuration Guide

This guide explains how to configure the database settings for the Climasys Spring Boot backend.

## Current Configuration

The application is currently configured to use **SQL Server** with the following settings:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;encrypt=false;databaseName=Climasys-00009
    username: sa
    password: ${DB_PASSWORD:change-me}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

## Configuration Options

### 1. SQL Server Configuration

#### Basic SQL Server Setup
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;encrypt=false;databaseName=Climasys-00009
    username: sa
    password: ${DB_PASSWORD:your-password}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

#### SQL Server with SSL
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=Climasys-00009
    username: sa
    password: ${DB_PASSWORD:your-password}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

#### SQL Server with Connection Pool
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;encrypt=false;databaseName=Climasys-00009
    username: sa
    password: ${DB_PASSWORD:your-password}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 2. MySQL Configuration

To switch to MySQL, update the `pom.xml` and `application.yml`:

#### pom.xml Changes
```xml
<!-- Replace SQL Server dependency with MySQL -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

#### application.yml for MySQL
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/climasys?useSSL=false&serverTimezone=UTC
    username: root
    password: ${DB_PASSWORD:your-password}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. PostgreSQL Configuration

#### pom.xml Changes
```xml
<!-- Replace SQL Server dependency with PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>
```

#### application.yml for PostgreSQL
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/climasys
    username: postgres
    password: ${DB_PASSWORD:your-password}
    driver-class-name: org.postgresql.Driver
```

### 4. Environment-Specific Configuration

#### Development Environment (application-dev.yml)
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;encrypt=false;databaseName=Climasys-Dev
    username: sa
    password: dev-password
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

#### Production Environment (application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://prod-server:1433;encrypt=true;trustServerCertificate=true;databaseName=Climasys-Prod
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
```

## Environment Variables

Set these environment variables for secure configuration:

### Windows
```cmd
set DB_PASSWORD=your-secure-password
set DB_USERNAME=your-username
set SPRING_PROFILES_ACTIVE=prod
```

### Linux/Mac
```bash
export DB_PASSWORD=your-secure-password
export DB_USERNAME=your-username
export SPRING_PROFILES_ACTIVE=prod
```

### Docker
```yaml
environment:
  - DB_PASSWORD=your-secure-password
  - DB_USERNAME=your-username
  - SPRING_PROFILES_ACTIVE=prod
```

## Connection Pool Configuration

For better performance, configure HikariCP connection pool:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

## JPA/Hibernate Configuration

Add JPA configuration for entity management:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.SQLServerDialect
        format_sql: true
        use_sql_comments: true
```

## Testing Configuration

For testing, use an in-memory database:

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

## Migration Steps

1. **Backup Current Database**: Always backup before making changes
2. **Update Configuration**: Modify `application.yml` with new settings
3. **Update Dependencies**: Change database driver in `pom.xml` if switching databases
4. **Test Connection**: Run the application and verify database connectivity
5. **Update Stored Procedures**: Ensure stored procedures are compatible with new database

## Troubleshooting

### Common Issues

1. **Connection Refused**: Check if database server is running
2. **Authentication Failed**: Verify username/password
3. **Database Not Found**: Ensure database exists
4. **Driver Issues**: Check if correct JDBC driver is included

### Logging Configuration

Add database logging to troubleshoot issues:

```yaml
logging:
  level:
    org.springframework.jdbc: DEBUG
    com.zaxxer.hikari: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

## Security Best Practices

1. **Use Environment Variables**: Never hardcode passwords
2. **Enable SSL**: Use encrypted connections in production
3. **Connection Pooling**: Configure appropriate pool sizes
4. **Regular Updates**: Keep database drivers updated
5. **Access Control**: Use least privilege principle for database users
