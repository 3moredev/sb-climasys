# Climasys Login API

This document describes the login API implementation for the Climasys application.

## Overview

The login API has been implemented using the logic from the original SQL Server stored procedure `login_sp.txt`, converted to PostgreSQL and integrated with Spring Boot.

## API Endpoints

### 1. Login Endpoint
- **URL**: `POST /api/auth/login`
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "loginId": "string",
  "password": "string", 
  "todaysDay": "string",
  "languageId": integer
}
```

#### Response
```json
{
  "loginStatus": integer,
  "userDetails": object,
  "shiftTimes": array,
  "availableRoles": array,
  "systemParams": array,
  "licenseKey": "string",
  "userMasterDetails": object,
  "errorMessage": "string"
}
```

#### Login Status Codes
- `1`: Login successful
- `0`: Invalid credentials
- `-1`: System error

### 2. Health Check Endpoints
- **Auth Health**: `GET /api/auth/health`
- **Test Database**: `GET /api/test/database`
- **Test Function**: `GET /api/test/function`

## Database Setup

The PostgreSQL function `usp_get_logindetails_json` is automatically created when the application starts up using the SQL script in `src/main/resources/sql/init-db.sql`.

## Testing the API

### Using curl
```bash
# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "test_user",
    "password": "test_password",
    "todaysDay": "Monday",
    "languageId": 1
  }'

# Test database connection
curl http://localhost:8080/api/test/database

# Test function exists
curl http://localhost:8080/api/test/function
```

### Using Postman
1. Create a new POST request to `http://localhost:8080/api/auth/login`
2. Set Content-Type to `application/json`
3. Add the request body with login credentials
4. Send the request

## Implementation Details

### Components Created
1. **AuthController** (`/auth/web/AuthController.java`) - REST endpoints
2. **AuthService** (`/auth/service/AuthService.java`) - Business logic
3. **LoginRequest** (`/auth/dto/LoginRequest.java`) - Request DTO
4. **LoginResponse** (`/auth/dto/LoginResponse.java`) - Response DTO
5. **TestController** (`/auth/web/TestController.java`) - Testing endpoints

### Database Function
The PostgreSQL function `usp_get_logindetails_json` returns a JSON object containing:
- Login status
- User details
- Shift times
- Available roles
- System parameters
- License key

## Error Handling

The API includes comprehensive error handling:
- Database connection errors
- Invalid credentials
- Missing required fields
- System exceptions

All errors are returned with appropriate HTTP status codes and error messages.

## Security Notes

⚠️ **Important**: This implementation stores passwords in plain text for demonstration purposes. In a production environment, you should:
- Hash passwords using BCrypt or similar
- Implement proper authentication tokens (JWT)
- Add input validation and sanitization
- Use HTTPS for all communications
