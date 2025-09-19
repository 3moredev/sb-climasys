# Manual Data Extraction Guide

## 🎯 Quick Start: Extract Real Data from SQL Server

Since you want to populate PostgreSQL with actual data from your SQL Server database, here are the steps:

### Option 1: Automated Extraction (Recommended)

1. **Set up SQL Server connection string:**
   ```powershell
   $env:SQLSERVER_CONNECTION_STRING = "DRIVER={ODBC Driver 17 for SQL Server};SERVER=your_server_name;DATABASE=Climasys-00010;UID=your_username;PWD=your_password"
   ```

2. **Run the automated migration:**
   ```powershell
   python 15_migrate_real_data.py
   ```

### Option 2: Manual CSV Export (If automated fails)

If you can't connect directly to SQL Server, you can manually export data:

#### Step 1: Export Data from SQL Server

Run these SQL queries in SQL Server Management Studio and save results as CSV:

```sql
-- Export Doctor Master
SELECT * FROM [dbo].[Doctor_Master]

-- Export Clinic Master  
SELECT * FROM [dbo].[Clinic_Master]

-- Export User Master
SELECT * FROM [dbo].[User_Master]

-- Export Patient Master
SELECT * FROM [dbo].[Patient_Master]

-- Export Patient Visits
SELECT * FROM [dbo].[Patient_Visits]

-- Export System Params
SELECT * FROM [dbo].[System_Params]

-- Export User Role
SELECT * FROM [dbo].[User_Role]

-- Export Blood Group Master
SELECT * FROM [dbo].[Blood_Group_Master]

-- Export Gender Master
SELECT * FROM [dbo].[Gender_Master]

-- Export Language Master
SELECT * FROM [dbo].[Language_Master]

-- Export License Key
SELECT * FROM [dbo].[License_Key]

-- Export Role Master
SELECT * FROM [dbo].[Role_Master]

-- Export Shift Master
SELECT * FROM [dbo].[Shift_Master]

-- Export Doctor Clinic Shift
SELECT * FROM [dbo].[Doctor_Clinic_Shift]

-- Export Doctor Model
SELECT * FROM [dbo].[Doctor_Model]

-- Export Model
SELECT * FROM [dbo].[Model]

-- Export Model Config Params
SELECT * FROM [dbo].[Model_Config_Params]

-- Export Visit Prescription Overwrite
SELECT * FROM [dbo].[Visit_Prescription_Overwrite]
```

#### Step 2: Save CSV Files

1. Create a folder called `extracted_data` in the migration scripts directory
2. Save each query result as CSV with these exact names:
   - `Doctor_Master.csv`
   - `Clinic_Master.csv`
   - `User_Master.csv`
   - `Patient_Master.csv`
   - `Patient_Visits.csv`
   - `System_Params.csv`
   - `User_Role.csv`
   - `Blood_Group_Master.csv`
   - `Gender_Master.csv`
   - `Language_Master.csv`
   - `License_Key.csv`
   - `Role_Master.csv`
   - `Shift_Master.csv`
   - `Doctor_Clinic_Shift.csv`
   - `Doctor_Model.csv`
   - `Model.csv`
   - `Model_Config_Params.csv`
   - `Visit_Prescription_Overwrite.csv`

#### Step 3: Import to PostgreSQL

```powershell
python 14_import_to_postgresql.py
```

### Option 3: Quick Test with Sample Data

If you want to test first with a smaller dataset:

1. **Export just a few records:**
   ```sql
   -- Export first 10 doctors
   SELECT TOP 10 * FROM [dbo].[Doctor_Master]
   
   -- Export first 10 patients
   SELECT TOP 10 * FROM [dbo].[Patient_Master]
   
   -- Export first 10 visits
   SELECT TOP 10 * FROM [dbo].[Patient_Visits]
   ```

2. **Save as CSV files and import**

### 🔧 Troubleshooting

#### SQL Server Connection Issues:
- Make sure SQL Server is running
- Check if TCP/IP is enabled
- Verify username/password
- Try using Windows Authentication instead

#### CSV Import Issues:
- Check CSV file encoding (should be UTF-8)
- Ensure column names match PostgreSQL table structure
- Remove any special characters that might cause issues

#### PostgreSQL Connection Issues:
- Make sure PostgreSQL is running
- Check if the `climasys_dev` schema exists
- Verify username/password

### 📊 Expected Results

After successful import, you should see:
- Real patient data in the dashboard
- Actual doctor and clinic information
- Real visit records and statistics
- Proper user authentication data

### 🚀 Next Steps

1. **Test the dashboard:**
   ```powershell
   # Start backend server
   cd ..\..\backend-spring
   mvn spring-boot:run
   
   # Test dashboard API
   Invoke-WebRequest -Uri "http://localhost:8080/api/reports/dashboard" -Method GET
   ```

2. **Verify data:**
   - Check if patient counts are correct
   - Verify doctor information
   - Test login functionality

### 📞 Need Help?

If you encounter issues:
1. Check the log files (`sqlserver_extraction.log`, `postgresql_import.log`)
2. Verify your connection strings
3. Ensure all required tables exist in both databases
4. Check for data type mismatches

The migration scripts are designed to handle most common issues automatically, but manual intervention may be needed for complex data transformations.
