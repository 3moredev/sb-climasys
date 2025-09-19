# SQL Server Connection Setup Guide

## 🎯 Objective
Configure the Python extraction script to connect to your SQL Server database.

## 📋 Step 1: Update Connection Details

You need to update the connection details in `extract_real_sql_server_data.py`. Here's what you need to change:

### Current Configuration (Lines 20-26):
```python
DB_CONFIG = {
    'driver': '{SQL Server}',  # Or '{ODBC Driver 17 for SQL Server}'
    'server': 'localhost',     # Your SQL Server hostname/IP
    'database': 'Climasys',    # Your SQL Server database name
    'uid': 'sa',              # Your SQL Server username
    'pwd': 'root'             # Your SQL Server password
}
```

### What You Need to Update:

#### 1. **Server** (Line 22)
- Change `'localhost'` to your SQL Server hostname or IP address
- Examples:
  - `'localhost'` (if SQL Server is on same machine)
  - `'192.168.1.100'` (if SQL Server is on another machine)
  - `'SERVER-NAME'` (if using hostname)

#### 2. **Database** (Line 23)
- Change `'Climasys'` to your actual SQL Server database name
- This should match the database name in your SQL Server

#### 3. **Username** (Line 24)
- Change `'sa'` to your SQL Server username
- Examples:
  - `'sa'` (system administrator)
  - `'your_username'` (your specific username)

#### 4. **Password** (Line 25)
- Change `'root'` to your SQL Server password
- Use the actual password for the username above

#### 5. **Driver** (Line 21) - Optional
- Keep `'{SQL Server}'` unless you have issues
- Alternative: `'{ODBC Driver 17 for SQL Server}'`

## 📋 Step 2: Test Connection

After updating the configuration, test the connection:

```bash
python extract_real_sql_server_data.py
```

## 📋 Step 3: Common Connection Issues

### Issue 1: "SQL Server does not exist or access denied"
**Solutions:**
- Check if SQL Server is running
- Verify the server name/IP address
- Check if SQL Server is configured to accept remote connections
- Verify Windows Firewall settings

### Issue 2: "Login failed for user"
**Solutions:**
- Verify username and password
- Check if the user has access to the database
- Try using Windows Authentication instead

### Issue 3: "Cannot open database"
**Solutions:**
- Verify the database name is correct
- Check if the user has access to the database
- Ensure the database exists

## 📋 Step 4: Alternative Connection Methods

### Windows Authentication
If you want to use Windows Authentication instead of SQL Server Authentication:

```python
DB_CONFIG = {
    'driver': '{SQL Server}',
    'server': 'localhost',
    'database': 'Climasys',
    'trusted_connection': 'yes'  # Use Windows Authentication
}
```

### Different Port
If your SQL Server uses a different port:

```python
DB_CONFIG = {
    'driver': '{SQL Server}',
    'server': 'localhost,1433',  # Add port number
    'database': 'Climasys',
    'uid': 'sa',
    'pwd': 'root'
}
```

## 📋 Step 5: Verify Your Settings

Before running the extraction, verify:
- ✅ SQL Server is running
- ✅ You can connect using SQL Server Management Studio
- ✅ The database name is correct
- ✅ The username and password are correct
- ✅ The user has access to the database

## 🚀 Next Steps

Once the connection is working:
1. **Test connection:** `python extract_real_sql_server_data.py`
2. **Extract data:** The script will automatically extract all tables
3. **Import data:** `python import_real_sql_server_data.py`
4. **Verify import:** `python check_data_status.py`

## 📞 Need Help?

If you're unsure about your connection details:
1. Check your SQL Server Management Studio connection
2. Look at the connection properties in SSMS
3. Ask your database administrator for the correct details
