# Swagger/OpenAPI Documentation

This Spring Boot application includes comprehensive API documentation using SpringDoc OpenAPI 3.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Features

### API Documentation
- **Interactive API Explorer**: Test API endpoints directly from the browser
- **Request/Response Examples**: See example requests and responses for each endpoint
- **Schema Documentation**: Detailed documentation of all DTOs and data models
- **Authentication Support**: Ready for JWT token authentication

### Available API Groups

1. **Authentication** (`/api/auth`)
   - User login with credentials
   - Health check endpoint

2. **Administration** (`/api/admin`)
   - User management
   - Medicine management
   - Clinic management

3. **Reference Data** (`/api/reference`)
   - Gender data
   - Blood group data
   - Other lookup values

4. **Clinical** (`/api/clinical`)
   - Patient complaints
   - Diagnosis management
   - Treatment records
   - Vital signs

5. **Billing** (`/api/billing`)
   - Payment processing
   - Receipt generation

## Configuration

The Swagger configuration is defined in:
- `OpenApiConfig.java` - Main OpenAPI configuration
- `application.yml` - SpringDoc properties

## Customization

You can customize the API documentation by:
1. Modifying `OpenApiConfig.java` for global settings
2. Adding more `@Operation` annotations to controllers
3. Adding `@Schema` annotations to DTOs for better field documentation

## Testing APIs

1. Start the application: `mvn spring-boot:run`
2. Open http://localhost:8080/swagger-ui.html
3. Click on any endpoint to expand it
4. Click "Try it out" to test the endpoint
5. Fill in the required parameters and click "Execute"

## Security

For endpoints that require authentication, you can:
1. Click the "Authorize" button in Swagger UI
2. Enter your JWT token in the format: `Bearer <your-token>`
3. All subsequent requests will include the authorization header
