# SQL Server to PostgreSQL Stored Procedure Conversion

## Original File: `login_sp.txt`
## Converted Files:
- `login_sp_postgres.sql` - Single function with multiple result sets
- `login_sp_postgres_simple.sql` - Multiple functions with JSON response

## Key Conversion Changes

### 1. **Function Declaration**
**SQL Server:**
```sql
CREATE PROCEDURE [dbo].[USP_Get_LoginDetails]
    @p_nvar_Login_Id NVARCHAR(60),
    @p_nvar_Password NVARCHAR(100),
    @P_nvar_TodaysDay NVARCHAR(20),
    @P_nvar_Language_Id INT
```

**PostgreSQL:**
```sql
CREATE OR REPLACE FUNCTION usp_get_logindetails(
    p_nvar_login_id VARCHAR(60),
    p_nvar_password VARCHAR(100),
    p_nvar_todaysday VARCHAR(20),
    p_nvar_language_id INTEGER
)
```

### 2. **Data Types**
- `NVARCHAR` → `VARCHAR`
- `INT` → `INTEGER`
- `DATETIME` → `TIMESTAMP`
- `BIT` → `BOOLEAN`

### 3. **String Functions**
- `ISNULL()` → `COALESCE()`
- `CONVERT()` → `TO_CHAR()`, `::TYPE` casting
- `DATEDIFF()` → `EXTRACT()`
- `GETDATE()` → `CURRENT_DATE`, `CURRENT_TIME`

### 4. **Date/Time Functions**
- `DatePart(Month, Getdate())` → `EXTRACT(MONTH FROM CURRENT_DATE)`
- `DatePart(Year, Getdate())` → `EXTRACT(YEAR FROM CURRENT_DATE)`
- `DATEDIFF(hour, ...)` → `EXTRACT(HOUR FROM ...)`

### 5. **Error Handling**
**SQL Server:**
```sql
BEGIN TRY
    -- code
END TRY
BEGIN CATCH
    SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
END CATCH
```

**PostgreSQL:**
```sql
BEGIN
    -- code
EXCEPTION
    WHEN OTHERS THEN
        -- error handling
END;
```

### 6. **System Functions**
- `SET NOCOUNT ON` → Not needed in PostgreSQL
- `sys.objects` → `information_schema.tables`
- `OBJECT_ID()` → `information_schema` queries

### 7. **Multiple Result Sets**
PostgreSQL doesn't support multiple result sets in a single function like SQL Server. Two approaches:

#### Approach 1: Single Function with RETURN QUERY
- Returns all data in a single table with many columns
- Uses NULL values for columns not applicable to each result set

#### Approach 2: Multiple Functions (Recommended)
- Separate function for each logical result set
- Main function returns JSON with all results
- More maintainable and follows PostgreSQL best practices

## Usage Examples

### Using the Simple Version (Recommended):
```sql
-- Call the main function
SELECT usp_get_logindetails_main('username', 'password', 'Monday', 1);

-- Or call individual functions
SELECT * FROM get_user_details('username', 1);
SELECT * FROM get_shift_times('Monday');
SELECT * FROM get_available_roles('username', 1);
```

### Using the Single Function Version:
```sql
SELECT * FROM usp_get_logindetails('username', 'password', 'Monday', 1);
```

## Notes for Application Integration

1. **Spring Boot Integration**: Update your Java controllers to handle the new function signatures
2. **Result Set Handling**: The simple version returns JSON, making it easier to parse in Java
3. **Error Handling**: PostgreSQL exceptions are handled differently than SQL Server
4. **Performance**: Consider indexing on frequently queried columns
5. **Security**: Review and update GRANT statements for your specific user roles

## Testing Recommendations

1. Test each function individually
2. Verify data types match your application expectations
3. Test error scenarios
4. Performance test with realistic data volumes
5. Validate date/time calculations work correctly in your timezone
