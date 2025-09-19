# SQL Server Management Studio (SSMS) Data Extraction Steps

## 🎯 Objective
Extract your real SQL Server data and import it into PostgreSQL to replace the empty tables.

## 📋 Step-by-Step Instructions

### Step 1: Open SQL Server Management Studio
1. Launch **SQL Server Management Studio (SSMS)**
2. Connect to your SQL Server instance
3. Navigate to your **Climasys** database

### Step 2: Create the Output Directory
1. Navigate to your migration scripts folder: `C:\Climasys\climasys2.0\migration\scripts`
2. Ensure the `real_sql_server_data` folder exists (it should already be created)

### Step 3: Extract Data from Each Table

For each table below, follow these steps:

#### 3.1: Run the Query
1. Open a new query window in SSMS
2. Copy and paste the SQL query for each table
3. Execute the query (F5 or click Execute)

#### 3.2: Export to CSV
1. Right-click on the results grid
2. Select **"Save Results As..."**
3. Choose **CSV format**
4. Save to: `C:\Climasys\climasys2.0\migration\scripts\real_sql_server_data\`
5. Use the exact filename specified

## 📊 Tables to Extract

### 1. User Master (Real User Accounts)
```sql
SELECT * FROM dbo.user_master;
```
**Save as:** `user_master.csv`

### 2. Patient Master (Real Patient Records)
```sql
SELECT * FROM dbo.patient_master;
```
**Save as:** `patient_master.csv`

### 3. Doctor Master (Real Doctor Information)
```sql
SELECT * FROM dbo.doctor_master;
```
**Save as:** `doctor_master.csv`

### 4. Clinic Master (Real Clinic Data)
```sql
SELECT * FROM dbo.clinic_master;
```
**Save as:** `clinic_master.csv`

### 5. Patient Visits (Real Visit Records)
```sql
SELECT * FROM dbo.patient_visits;
```
**Save as:** `patient_visits.csv`

### 6. Role Master (User Roles)
```sql
SELECT * FROM dbo.role_master;
```
**Save as:** `role_master.csv`

### 7. Gender Master (Gender Options)
```sql
SELECT * FROM dbo.gender_master;
```
**Save as:** `gender_master.csv`

### 8. Blood Group Master (Blood Group Types)
```sql
SELECT * FROM dbo.blood_group_master;
```
**Save as:** `blood_group_master.csv`

### 9. Language Master (Language Settings)
```sql
SELECT * FROM dbo.language_master;
```
**Save as:** `language_master.csv`

### 10. Shift Master (Work Shifts)
```sql
SELECT * FROM dbo.shift_master;
```
**Save as:** `shift_master.csv`

### 11. Model (System Models)
```sql
SELECT * FROM dbo.model;
```
**Save as:** `model.csv`

### 12. Model Config Params (Model Configurations)
```sql
SELECT * FROM dbo.model_config_params;
```
**Save as:** `model_config_params.csv`

## ⚠️ Important Notes

### File Naming
- Use **exact filenames** as specified above
- Save in **CSV format**
- Place all files in the `real_sql_server_data` folder

### Data Quality
- Verify each CSV file contains the expected data
- Check for any encoding issues
- Ensure date formats are preserved

### Large Tables
- If any table has many records, the export might take time
- Consider exporting in smaller batches if needed

## 🚀 After Extraction

Once you have all CSV files:

1. **Verify files exist:**
   ```bash
   dir real_sql_server_data\*.csv
   ```

2. **Import the data:**
   ```bash
   python import_real_sql_server_data.py
   ```

3. **Verify the import:**
   ```bash
   python check_data_status.py
   ```

## 📞 Troubleshooting

### If you can't find a table:
- Check if the table name is correct
- Verify you're connected to the right database
- Some tables might have different names

### If export fails:
- Try exporting with different options
- Check file permissions
- Ensure the output folder exists

### If data looks wrong:
- Verify the query results in SSMS first
- Check for any filters or WHERE clauses
- Ensure you're exporting the complete dataset

## 🎯 Expected Results

After successful extraction and import:
- **Real user accounts** in `user_master`
- **Actual patient records** in `patient_master`
- **Real doctor information** in `doctor_master`
- **Actual clinic data** in `clinic_master`
- **Real visit history** in `patient_visits`
- **All reference data** properly populated

Your PostgreSQL database will then contain your actual SQL Server data instead of empty tables!
