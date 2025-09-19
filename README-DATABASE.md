# Database Configuration Guide

This guide provides comprehensive instructions for configuring the database settings for the Climasys Spring Boot backend.

## Quick Start

### Option 1: Use Configuration Scripts

**Windows:**
```cmd
cd backend-spring
scripts\configure-database.bat
```

**Linux/Mac:**
```bash
cd backend-spring
chmod +x scripts/configure-database.sh
./scripts/configure-database.sh
```

### Option 2: Use Docker Compose

**Start with SQL Server (Default):**
```bash
docker-compose up -d
```

**Start with MySQL:**
```bash
docker-compose --profile mysql up -d
```

**Start with PostgreSQL:**
```bash
docker-compose --profile postgres up -d
```

## Manual Configuration

### 1. SQL Server Configuration

#### Update application.yml
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;encrypt=false;databaseName=Climasys-00009
    username: sa
    password: ${DB_PASSWORD:your-password}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

#### Environment Variables
```bash
export DB_PASSWORD=your-secure-password
export SPRING_PROFILES_ACTIVE=dev
```

### 2. MySQL Configuration

#### Update pom.xml
```xml
<!-- Replace SQL Server dependency -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

#### Update application.yml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/climasys?useSSL=false&serverTimezone=UTC
    username: root
    password: ${DB_PASSWORD:your-password}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. PostgreSQL Configuration

#### Update pom.xml
```xml
<!-- Replace SQL Server dependency -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>
```

#### Update application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/climasys
    username: postgres
    password: ${DB_PASSWORD:your-password}
    driver-class-name: org.postgresql.Driver
```

## Environment-Specific Configuration

### Development Environment
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Environment
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Custom Profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Environment Variables

### Required Variables
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `CLIMASYS_ENCRYPTION_KEY` - Legacy encryption key

### Optional Variables
- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port
- `DB_NAME` - Database name
- `DB_USERNAME` - Database username
- `SPRING_PROFILES_ACTIVE` - Active profile

## Connection Pool Configuration

For better performance, configure HikariCP:

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

## Security Best Practices

1. **Use Environment Variables**: Never hardcode passwords
2. **Enable SSL**: Use encrypted connections in production
3. **Connection Pooling**: Configure appropriate pool sizes
4. **Regular Updates**: Keep database drivers updated
5. **Access Control**: Use least privilege principle

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Check if database server is running
   - Verify host and port settings

2. **Authentication Failed**
   - Verify username and password
   - Check user permissions

3. **Database Not Found**
   - Ensure database exists
   - Check database name spelling

4. **Driver Issues**
   - Verify correct JDBC driver is included
   - Check driver version compatibility

### Enable Debug Logging

```yaml
logging:
  level:
    org.springframework.jdbc: DEBUG
    com.zaxxer.hikari: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

## Testing Database Connection

### Health Check Endpoint
```bash
curl http://localhost:8080/actuator/health
```

### Database Connection Test
```bash
curl http://localhost:8080/actuator/health/db
```

## Migration Between Databases

1. **Backup Current Data**: Always backup before migration
2. **Update Configuration**: Change database settings
3. **Update Dependencies**: Change JDBC driver if needed
4. **Test Connection**: Verify database connectivity
5. **Update Stored Procedures**: Ensure compatibility
6. **Data Migration**: Migrate data if needed

## Docker Deployment

### Build and Run
```bash
# Build the application
docker build -t climasys-backend .

# Run with environment variables
docker run -d \
  -p 8080:8080 \
  -e DB_PASSWORD=your-password \
  -e JWT_SECRET=your-secret \
  climasys-backend
```

### Docker Compose
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f climasys-backend

# Stop services
docker-compose down
```

## Performance Tuning

### SQL Server
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=Climasys;encrypt=false;trustServerCertificate=true;loginTimeout=30
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
```

### MySQL
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/climasys?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8
    hikari:
      maximum-pool-size: 30
      minimum-idle: 5
```

### PostgreSQL
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/climasys?ssl=false&sslmode=disable
    hikari:
      maximum-pool-size: 25
      minimum-idle: 5
```

## Support

For additional help:
1. Check the application logs
2. Review the health check endpoints
3. Verify database server status
4. Test with a simple database connection tool
