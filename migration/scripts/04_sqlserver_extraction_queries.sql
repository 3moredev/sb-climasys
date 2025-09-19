-- =====================================================
-- SQL Server Data Extraction Queries
-- =====================================================
-- 
-- These queries should be run on your SQL Server instance
-- to extract data from the 'Climasys-00010' schema.
-- 
-- Run these queries and export the results to CSV files,
-- then use the data to populate the PostgreSQL database.
--
-- =====================================================

-- =====================================================
-- DOCTOR MASTER EXTRACTION
-- =====================================================

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
WHERE Is_Active = 1;

-- =====================================================
-- CLINIC MASTER EXTRACTION
-- =====================================================

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
WHERE Is_Active = 1;

-- =====================================================
-- USER MASTER EXTRACTION
-- =====================================================

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
WHERE Is_Active = 1;

-- =====================================================
-- USER ROLE EXTRACTION
-- =====================================================

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
WHERE UR.Is_Active = 1 AND UM.Is_Active = 1;

-- =====================================================
-- PATIENT MASTER EXTRACTION
-- =====================================================

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
    Emergency_Contact_Name,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Master]
WHERE Is_Active = 1;

-- =====================================================
-- PATIENT VISITS EXTRACTION
-- =====================================================

SELECT 
    ID,
    Visit_ID,
    Patient_ID,
    Doctor_ID,
    Clinic_ID,
    Visit_Date,
    Visit_Time,
    Chief_Complaint,
    Diagnosis,
    Treatment,
    Prescription,
    Follow_Up_Date,
    Attended_By_ID,
    CASE WHEN Delete_Flag = 0 THEN 'false' ELSE 'true' END as Delete_Flag,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Patient_Visits]
WHERE Delete_Flag = 0;

-- =====================================================
-- SHIFT MASTER EXTRACTION
-- =====================================================

SELECT 
    Shift_ID,
    Description,
    Shift_Day,
    Start_Time,
    End_Time,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On
FROM [Climasys-00010].[dbo].[Shift_Master]
WHERE Is_Active = 1;

-- =====================================================
-- DOCTOR CLINIC SHIFT EXTRACTION
-- =====================================================

SELECT 
    ID,
    Doctor_ID,
    Clinic_ID,
    Shift_ID,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On
FROM [Climasys-00010].[dbo].[Doctor_Clinic_Shift]
WHERE Is_Active = 1;

-- =====================================================
-- MEDICINE MASTER EXTRACTION
-- =====================================================

SELECT 
    Medicine_ID,
    Medicine_Name,
    Generic_Name,
    Manufacturer,
    Unit,
    Dosage_Form,
    Strength,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On
FROM [Climasys-00010].[dbo].[Medicine_Master]
WHERE Is_Active = 1;

-- =====================================================
-- VISIT PRESCRIPTION OVERWRITE EXTRACTION
-- =====================================================

SELECT 
    ID,
    Visit_ID,
    Patient_ID,
    Doctor_ID,
    Medicine_ID,
    Medicine_Name,
    Dosage,
    Frequency,
    Duration,
    Instructions,
    Created_On
FROM [Climasys-00010].[dbo].[Visit_Prescription_Overwrite];

-- =====================================================
-- SYSTEM PARAMETERS EXTRACTION
-- =====================================================

SELECT 
    ID,
    Doctor_ID,
    Param_Name,
    Param_Value,
    Param_Description,
    CASE WHEN IS_MAIN_DOCTOR = 1 THEN 'true' ELSE 'false' END as IS_MAIN_DOCTOR,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[System_Params];

-- =====================================================
-- LICENSE KEY EXTRACTION
-- =====================================================

SELECT 
    ID,
    License_Key,
    Installation_Date,
    Valid_To,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On
FROM [Climasys-00010].[dbo].[License_Key]
WHERE Is_Active = 1;

-- =====================================================
-- MODEL MASTER EXTRACTION
-- =====================================================

SELECT 
    Model_ID,
    Model_Name,
    Model_Description,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On
FROM [Climasys-00010].[dbo].[Model]
WHERE Is_Active = 1;

-- =====================================================
-- DOCTOR MODEL EXTRACTION
-- =====================================================

SELECT 
    ID,
    Doctor_ID,
    Clinic_ID,
    Model_ID,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On
FROM [Climasys-00010].[dbo].[Doctor_Model]
WHERE Is_Active = 1;

-- =====================================================
-- MODEL CONFIG PARAMS EXTRACTION
-- =====================================================

SELECT 
    Config_ID,
    Model_ID,
    Param_Name,
    Param_Value,
    CASE WHEN IS_Enabled = 1 THEN 'true' ELSE 'false' END as IS_Enabled,
    Created_On
FROM [Climasys-00010].[dbo].[Model_Config_Params];

-- =====================================================
-- ROLE MASTER EXTRACTION
-- =====================================================

SELECT 
    Role_ID,
    Role_Name,
    Role_Description,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Role_Master]
WHERE Is_Active = 1;

-- =====================================================
-- LANGUAGE MASTER EXTRACTION
-- =====================================================

SELECT 
    Language_ID,
    Language_Name,
    Language_Code,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active,
    Created_On,
    Updated_On
FROM [Climasys-00010].[dbo].[Language_Master]
WHERE Is_Active = 1;

-- =====================================================
-- GENDER MASTER EXTRACTION
-- =====================================================

SELECT 
    Gender_ID,
    Gender_Name,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active
FROM [Climasys-00010].[dbo].[Gender_Master]
WHERE Is_Active = 1;

-- =====================================================
-- BLOOD GROUP MASTER EXTRACTION
-- =====================================================

SELECT 
    Blood_Group_ID,
    Blood_Group_Name,
    CASE WHEN Is_Active = 1 THEN 'true' ELSE 'false' END as Is_Active
FROM [Climasys-00010].[dbo].[Blood_Group_Master]
WHERE Is_Active = 1;

-- =====================================================
-- DATA EXPORT INSTRUCTIONS
-- =====================================================

/*
To export data from SQL Server:

1. Run each query above in SQL Server Management Studio
2. Right-click on the results and select "Save Results As..."
3. Choose CSV format and save with descriptive filenames:
   - doctor_master.csv
   - clinic_master.csv
   - user_master.csv
   - user_role.csv
   - patient_master.csv
   - patient_visits.csv
   - shift_master.csv
   - doctor_clinic_shift.csv
   - medicine_master.csv
   - visit_prescription_overwrite.csv
   - system_params.csv
   - license_key.csv
   - model_master.csv
   - doctor_model.csv
   - model_config_params.csv
   - role_master.csv
   - language_master.csv
   - gender_master.csv
   - blood_group_master.csv

4. Use these CSV files to populate the PostgreSQL database
   using the migration functions in 03_migrate_from_sqlserver.sql

Alternative: Use SQL Server's bcp utility or SSIS for bulk export
*/
