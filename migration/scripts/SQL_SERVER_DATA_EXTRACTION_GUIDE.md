# SQL Server Data Extraction Guide

## 🎯 Objective
Extract the **real data** from your SQL Server database and import it into PostgreSQL to replace the sample data.

## 📋 Critical Tables to Extract

The following tables contain the actual data from your SQL Server database:

### Core Tables
- `user_master` - **Real user accounts and credentials**
- `patient_master` - **Actual patient records**
- `doctor_master` - **Real doctor information**
- `clinic_master` - **Actual clinic data**
- `patient_visits` - **Real visit records**

### Reference Tables
- `role_master` - **User roles and permissions**
- `gender_master` - **Gender options**
- `blood_group_master` - **Blood group types**
- `language_master` - **Language settings**
- `shift_master` - **Work shifts**
- `model` - **System models**
- `model_config_params` - **Model configurations**

## 🔧 Method 1: Using SQL Server Management Studio (Recommended)

### Step 1: Connect to SQL Server
1. Open SQL Server Management Studio (SSMS)
2. Connect to your SQL Server instance
3. Navigate to your `Climasys` database

### Step 2: Export Each Table
For each table, run the following query and export as CSV:

```sql
-- Export user_master (REAL USER DATA)
SELECT * FROM dbo.user_master;

-- Export patient_master (REAL PATIENT DATA)
SELECT * FROM dbo.patient_master;

-- Export doctor_master (REAL DOCTOR DATA)
SELECT * FROM dbo.doctor_master;

-- Export clinic_master (REAL CLINIC DATA)
SELECT * FROM dbo.clinic_master;

-- Export patient_visits (REAL VISIT DATA)
SELECT * FROM dbo.patient_visits;

-- Export role_master
SELECT * FROM dbo.role_master;

-- Export gender_master
SELECT * FROM dbo.gender_master;

-- Export blood_group_master
SELECT * FROM dbo.blood_group_master;

-- Export language_master
SELECT * FROM dbo.language_master;

-- Export shift_master
SELECT * FROM dbo.shift_master;

-- Export model
SELECT * FROM dbo.model;

-- Export model_config_params
SELECT * FROM dbo.model_config_params;
```

### Step 3: Save Files
Save each query result as CSV with these exact filenames:
- `user_master.csv`
- `patient_master.csv`
- `doctor_master.csv`
- `clinic_master.csv`
- `patient_visits.csv`
- `role_master.csv`
- `gender_master.csv`
- `blood_group_master.csv`
- `language_master.csv`
- `shift_master.csv`
- `model.csv`
- `model_config_params.csv`

### Step 4: Create Directory
Create a folder called `real_sql_server_data` in your migration scripts directory and place all CSV files there.

## 🔧 Method 2: Using PowerShell Script

Create a PowerShell script to automate the extraction:

```powershell
# SQL Server Data Extraction Script
$server = "YOUR_SERVER_NAME"
$database = "Climasys"
$username = "YOUR_USERNAME"
$password = "YOUR_PASSWORD"

$tables = @(
    "user_master",
    "patient_master", 
    "doctor_master",
    "clinic_master",
    "patient_visits",
    "role_master",
    "gender_master",
    "blood_group_master",
    "language_master",
    "shift_master",
    "model",
    "model_config_params"
)

$connectionString = "Server=$server;Database=$database;User Id=$username;Password=$password;"

foreach ($table in $tables) {
    $query = "SELECT * FROM dbo.$table"
    $outputFile = "real_sql_server_data\$table.csv"
    
    Write-Host "Extracting $table..."
    
    # Use sqlcmd to export data
    sqlcmd -S $server -d $database -U $username -P $password -Q $query -o $outputFile -s","
}
```

## 🔧 Method 3: Using Python Script (If SQL Server is accessible)

If you can access SQL Server from your development environment, update the connection details in `extract_real_sql_server_data.py`:

```python
DB_CONFIG = {
    'driver': '{SQL Server}',  # Or '{ODBC Driver 17 for SQL Server}'
    'server': 'YOUR_SERVER_IP',     # Your SQL Server hostname/IP
    'database': 'Climasys',         # Your SQL Server database name
    'uid': 'YOUR_USERNAME',         # Your SQL Server username
    'pwd': 'YOUR_PASSWORD'          # Your SQL Server password
}
```

Then run:
```bash
python extract_real_sql_server_data.py
```

## 📥 Import Real Data to PostgreSQL

Once you have the CSV files, run the import script:

```bash
python import_real_sql_server_data.py
```

This script will:
1. Clear the existing sample data from PostgreSQL tables
2. Import the real data from CSV files
3. Verify the import was successful
4. Show final record counts

## ⚠️ Important Notes

### Data Backup
- **Backup your PostgreSQL database** before importing real data
- The import script will **DELETE existing data** and replace it with real data

### Data Validation
- Verify the CSV files contain the expected data
- Check for any encoding issues (UTF-8 recommended)
- Ensure date formats are compatible

### Foreign Key Constraints
- The import script handles foreign key constraints
- If you encounter constraint violations, you may need to import tables in a specific order

## 🔍 Verification Steps

After importing, verify the data:

```bash
python check_data_status.py
```

This will show you the real record counts from your SQL Server data.

## 📞 Support

If you encounter issues:
1. Check the CSV file format and encoding
2. Verify PostgreSQL connection
3. Review the import logs for specific errors
4. Ensure all required columns are present in the CSV files

## 🎯 Expected Results

After successful import, you should see:
- **Real user accounts** in `user_master`
- **Actual patient records** in `patient_master`
- **Real doctor information** in `doctor_master`
- **Actual clinic data** in `clinic_master`
- **Real visit records** in `patient_visits`

The sample data will be completely replaced with your actual SQL Server data.
