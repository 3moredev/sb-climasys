-- =====================================================
-- Enhanced SQL Server Data Extraction Queries
-- Climasys Database Migration
-- =====================================================
-- 
-- This script provides comprehensive data extraction queries
-- for migrating from SQL Server to PostgreSQL.
-- 
-- Instructions:
-- 1. Run these queries on your SQL Server instance
-- 2. Export results to CSV files with the specified names
-- 3. Place CSV files in the migration/scripts/csv_data directory
-- 4. Run the PostgreSQL import script
--
-- =====================================================

-- =====================================================
-- REFERENCE DATA EXTRACTION
-- =====================================================

-- Language Master
SELECT 
    Language_ID,
    Language_Name,
    Language_Code,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Language_Master]
WHERE Is_Active = 1
ORDER BY Language_ID;

-- Role Master
SELECT 
    Role_ID,
    Role_Name,
    Role_Description,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Role_Master]
WHERE Is_Active = 1
ORDER BY Role_ID;

-- Gender Master
SELECT 
    Gender_ID,
    Gender_Name,
    Gender_Code,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Gender_Master]
WHERE Is_Active = 1
ORDER BY Gender_ID;

-- Blood Group Master
SELECT 
    Blood_Group_ID,
    Blood_Group_Name,
    Blood_Group_Code,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Blood_Group_Master]
WHERE Is_Active = 1
ORDER BY Blood_Group_ID;

-- =====================================================
-- CORE MASTER TABLES
-- =====================================================

-- Doctor Master
SELECT 
    Doctor_ID,
    First_Name,
    Middle_Name,
    Last_Name,
    Qualification,
    Specialization,
    Phone,
    Email,
    Address,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Doctor_Master]
WHERE Is_Active = 1
ORDER BY Doctor_ID;

-- Clinic Master
SELECT 
    Clinic_ID,
    Doctor_ID,
    Clinic_Name,
    Clinic_Address,
    Clinic_Phone,
    Clinic_Email,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Clinic_Master]
WHERE Is_Active = 1
ORDER BY Clinic_ID;

-- User Master
SELECT 
    ID,
    Doctor_ID,
    Login_Id,
    Password,
    First_Name,
    Middle_Name,
    Last_Name,
    Language_Id,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[User_Master]
WHERE Is_Active = 1
ORDER BY ID;

-- User Role
SELECT 
    UR.ID,
    UR.User_ID,
    UR.Role_Id,
    UR.Clinic_ID,
    CASE WHEN UR.Is_Default_Clinic = 1 THEN 'true' ELSE 'false' END as Is_Default_Clinic,
    CASE WHEN UR.Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    UR.Created_On,
    UR.Updated_On
FROM [Climasys-00010].[dbo].[User_Role] UR
INNER JOIN [Climasys-00010].[dbo].[User_Master] UM ON UR.User_ID = UM.ID
WHERE UR.Is_Active = 1 AND UM.Is_Active = 1
ORDER BY UR.ID;

-- Patient Master
SELECT 
    ID,
    Doctor_ID,
    Patient_ID,
    First_Name,
    Middle_Name,
    Last_Name,
    Gender_ID,
    Date_Of_Birth,
    Age,
    Blood_Group_ID,
    Phone,
    Email,
    Address,
    Emergency_Contact,
    Emergency_Phone,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Master]
WHERE Is_Active = 1
ORDER BY ID;

-- Patient Visits
SELECT 
    ID,
    Patient_ID,
    Doctor_ID,
    Visit_Date,
    Visit_Time,
    Visit_Type,
    Chief_Complaint,
    Diagnosis,
    Treatment,
    Prescription,
    Follow_Up_Date,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Visits]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- MEDICINE AND PRESCRIPTION TABLES
-- =====================================================

-- Medicine Master
SELECT 
    Medicine_ID,
    Doctor_ID,
    Medicine_Name,
    Medicine_Type,
    Dosage,
    Unit,
    Price,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Medicine_Master]
WHERE Is_Active = 1
ORDER BY Medicine_ID;

-- Prescription Master
SELECT 
    Prescription_ID,
    Doctor_ID,
    Prescription_Name,
    Prescription_Type,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Prescription_Master]
WHERE Is_Active = 1
ORDER BY Prescription_ID;

-- Visit Prescription Overwrite
SELECT 
    ID,
    Visit_ID,
    Prescription_ID,
    Medicine_ID,
    Dosage,
    Frequency,
    Duration,
    Instructions,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Visit_Prescription_Overwrite]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- SYSTEM CONFIGURATION TABLES
-- =====================================================

-- System Parameters
SELECT 
    Parameter_ID,
    Parameter_Name,
    Parameter_Value,
    Parameter_Type,
    Parameter_Description,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[System_Params]
WHERE Is_Active = 1
ORDER BY Parameter_ID;

-- License Key
SELECT 
    License_ID,
    License_Key,
    License_Type,
    Expiry_Date,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[License_Key]
WHERE Is_Active = 1
ORDER BY License_ID;

-- =====================================================
-- SHIFT AND SCHEDULE TABLES
-- =====================================================

-- Shift Master
SELECT 
    Shift_ID,
    Shift_Name,
    Start_Time,
    End_Time,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Shift_Master]
WHERE Is_Active = 1
ORDER BY Shift_ID;

-- Doctor Clinic Shift
SELECT 
    ID,
    Doctor_ID,
    Clinic_ID,
    Shift_ID,
    Day_Of_Week,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Doctor_Clinic_Shift]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- MODEL AND CONFIGURATION TABLES
-- =====================================================

-- Model
SELECT 
    Model_ID,
    Model_Name,
    Model_Type,
    Model_Description,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Model]
WHERE Is_Active = 1
ORDER BY Model_ID;

-- Doctor Model
SELECT 
    ID,
    Doctor_ID,
    Model_ID,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Doctor_Model]
WHERE Is_Active = 1
ORDER BY ID;

-- Model Config Params
SELECT 
    ID,
    Model_ID,
    Parameter_Name,
    Parameter_Value,
    Parameter_Type,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Model_Config_Params]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- BILLING AND FINANCIAL TABLES
-- =====================================================

-- Billing Master (if exists)
SELECT 
    Billing_ID,
    Patient_ID,
    Visit_ID,
    Doctor_ID,
    Total_Amount,
    Paid_Amount,
    Balance_Amount,
    Billing_Date,
    Payment_Status,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Billing_Master]
WHERE Is_Active = 1
ORDER BY Billing_ID;

-- Billing Details (if exists)
SELECT 
    Billing_Detail_ID,
    Billing_ID,
    Service_Type,
    Service_Name,
    Quantity,
    Unit_Price,
    Total_Price,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Billing_Details]
WHERE Is_Active = 1
ORDER BY Billing_Detail_ID;

-- =====================================================
-- LAB AND INVESTIGATION TABLES
-- =====================================================

-- Lab Test Master (if exists)
SELECT 
    Test_ID,
    Doctor_ID,
    Test_Name,
    Test_Type,
    Test_Description,
    Normal_Range,
    Unit,
    Price,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Lab_Test_Master]
WHERE Is_Active = 1
ORDER BY Test_ID;

-- Patient Lab Tests (if exists)
SELECT 
    ID,
    Patient_ID,
    Visit_ID,
    Test_ID,
    Test_Date,
    Test_Result,
    Test_Status,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Lab_Tests]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- INSURANCE TABLES
-- =====================================================

-- Insurance Company Master (if exists)
SELECT 
    Company_ID,
    Company_Name,
    Company_Code,
    Contact_Person,
    Phone,
    Email,
    Address,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Insurance_Company_Master]
WHERE Is_Active = 1
ORDER BY Company_ID;

-- Patient Insurance (if exists)
SELECT 
    ID,
    Patient_ID,
    Company_ID,
    Policy_Number,
    Policy_Type,
    Expiry_Date,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Insurance]
WHERE Is_Active = 1
ORDER BY ID;

-- =====================================================
-- EXPORT INSTRUCTIONS
-- =====================================================

/*
EXPORT INSTRUCTIONS:

1. Run each query above separately
2. Export results to CSV files with these exact names:
   - language_master.csv
   - role_master.csv
   - gender_master.csv
   - blood_group_master.csv
   - doctor_master.csv
   - clinic_master.csv
   - user_master.csv
   - user_role.csv
   - patient_master.csv
   - patient_visits.csv
   - medicine_master.csv
   - prescription_master.csv
   - visit_prescription_overwrite.csv
   - system_params.csv
   - license_key.csv
   - shift_master.csv
   - doctor_clinic_shift.csv
   - model.csv
   - doctor_model.csv
   - model_config_params.csv
   - billing_master.csv (if exists)
   - billing_details.csv (if exists)
   - lab_test_master.csv (if exists)
   - patient_lab_tests.csv (if exists)
   - insurance_company_master.csv (if exists)
   - patient_insurance.csv (if exists)

3. Place all CSV files in: migration/scripts/csv_data/

4. Run the PostgreSQL import script to complete the migration

NOTES:
- Some tables may not exist in your SQL Server database
- Skip queries for tables that don't exist
- Ensure CSV files have headers
- Check for special characters that might need escaping
- Verify data types match the PostgreSQL schema
*/

