# Logback Logging Implementation

This document describes the Logback logging implementation for the Climasys backend application.

## Overview

The application now uses Logback for comprehensive logging with the following features:
- Console logging for development
- File-based logging with rotation
- Separate error logs
- Audit trail logging
- Performance monitoring
- Profile-specific configurations

## Configuration

### Logback Configuration File
Location: `src/main/resources/logback-spring.xml`

### Key Features

#### 1. Multiple Appenders
- **CONSOLE**: Console output for development
- **FILE**: General application logs with rotation
- **ERROR_FILE**: Error and warning logs only
- **AUDIT_FILE**: Business operation audit trail
- **ASYNC_FILE**: Asynchronous file logging for performance

#### 2. Log Rotation
- Files rotate daily and by size (10MB max)
- 30 days retention for general logs
- 90 days retention for audit logs
- Total size cap to prevent disk space issues

#### 3. Log Levels by Package
- `com.climasys`: INFO level (DEBUG in dev profile)
- `com.climasys.audit`: INFO level for audit trail
- `org.springframework`: WARN level
- `org.hibernate.SQL`: DEBUG level for SQL queries
- Database drivers: WARN level

#### 4. Profile-Specific Settings
- **Development**: DEBUG level, console output
- **Production**: INFO level, file-only logging

## Usage Examples

### Basic Logging in Service Classes

```java
@Service
public class MyService {
    private static final Logger logger = LoggerFactory.getLogger(MyService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.climasys.audit");
    
    public void doSomething() {
        logger.info("Starting operation");
        try {
            // Business logic
            auditLogger.info("OPERATION_SUCCESS - Details: {}", details);
        } catch (Exception e) {
            logger.error("Operation failed: {}", e.getMessage(), e);
            auditLogger.error("OPERATION_FAILED - Error: {}", e.getMessage());
        }
    }
}
```

### Using LoggingUtil for Common Operations

```java
import com.climasys.util.LoggingUtil;

public class MyController {
    public ResponseEntity<?> handleRequest() {
        LoggingUtil.logMethodEntry("handleRequest", "param1", value1);
        
        try {
            // Process request
            LoggingUtil.logBusinessOperation("CREATE_APPOINTMENT", userId, "Appointment created");
            LoggingUtil.logMethodExit("handleRequest", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LoggingUtil.logSecurityEvent("UNAUTHORIZED_ACCESS", userId, "Access denied");
            throw e;
        }
    }
}
```

## Log Files

### File Locations
- General logs: `./logs/climasys-backend.log`
- Error logs: `./logs/climasys-backend-error.log`
- Audit logs: `./logs/climasys-backend-audit.log`

### Log Patterns
- **General**: `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n`
- **Audit**: `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] AUDIT %logger{36} - %msg%n`

## Environment Variables

You can customize logging behavior using environment variables:

```bash
# Set custom log directory
export LOG_PATH=/var/log/climasys

# Set log level (overrides configuration)
export LOGGING_LEVEL_COM_CLIMASYS=DEBUG
```

## Best Practices

### 1. Log Levels
- **ERROR**: System errors, exceptions
- **WARN**: Recoverable issues, deprecated usage
- **INFO**: Important business events, user actions
- **DEBUG**: Detailed flow information, variable values
- **TRACE**: Very detailed execution flow

### 2. Audit Logging
Use the audit logger for business operations:
```java
auditLogger.info("LOGIN_SUCCESS - User: {}, IP: {}", username, ipAddress);
auditLogger.warn("LOGIN_FAILED - User: {}, Reason: {}", username, reason);
auditLogger.info("DATA_MODIFIED - Table: {}, User: {}, Action: {}", table, user, action);
```

### 3. Performance Logging
```java
long startTime = System.currentTimeMillis();
// ... operation ...
LoggingUtil.logPerformance("DATABASE_QUERY", System.currentTimeMillis() - startTime, "User lookup");
```

### 4. Security Logging
```java
LoggingUtil.logSecurityEvent("UNAUTHORIZED_ACCESS", userId, "Attempted to access restricted resource");
LoggingUtil.logSecurityEvent("PASSWORD_CHANGE", userId, "Password changed successfully");
```

## Monitoring and Alerting

### Log Analysis
- Monitor error logs for system issues
- Track audit logs for compliance
- Analyze performance logs for bottlenecks

### Recommended Alerts
- ERROR level logs in production
- Multiple failed login attempts
- Unusual access patterns
- Performance degradation

## Migration from System.out.println()

The following changes were made to replace System.out.println() with proper logging:

### Before
```java
System.out.println("User found: " + user.getLoginId());
System.err.println("Error: " + e.getMessage());
```

### After
```java
logger.info("User found: {}", user.getLoginId());
logger.error("Error: {}", e.getMessage(), e);
```

## Troubleshooting

### Common Issues

1. **Logs not appearing**: Check log level configuration
2. **Large log files**: Verify rotation settings
3. **Performance impact**: Use async appenders for high-volume logging
4. **Missing audit logs**: Ensure audit logger is properly configured

### Debug Mode
Enable debug logging by setting the profile to `dev` or adding:
```yaml
logging:
  level:
    com.climasys: DEBUG
```

## Future Enhancements

1. **Structured Logging**: Implement JSON format for log aggregation
2. **Log Aggregation**: Integrate with ELK stack or similar
3. **Metrics Integration**: Add Micrometer metrics alongside logs
4. **Custom Appenders**: Create appenders for specific external systems
