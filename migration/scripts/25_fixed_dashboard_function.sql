-- Fixed Dashboard function for PostgreSQL
CREATE OR REPLACE FUNCTION usp_get_dashboard_data()
RETURNS JSON
LANGUAGE plpgsql
AS $$
DECLARE
    result JSON;
    perday INTEGER;
    dt_year INTEGER;
    installation_date DATE;
    total_patients INTEGER;
    last_days INTEGER;
    female_patients INTEGER;
    male_patients INTEGER;
    patients_visits INTEGER;
    patients_per_day INTEGER;
    prescription_count INTEGER;
    license_end_date DATE;
    main_doctor_id VARCHAR(30);
BEGIN
    -- Calculate per day visits (handle case when no visits exist)
    SELECT COALESCE(COUNT(DISTINCT visit_date), 1) INTO perday 
    FROM patient_visits 
    WHERE delete_flag = false;
    
    -- If no visits exist, set perday to 1 to avoid division by zero
    IF perday = 0 THEN
        perday := 1;
    END IF;
    
    -- Get license information
    SELECT 
        EXTRACT(YEAR FROM lk.installation_date)::INTEGER,
        lk.installation_date,
        lk.valid_to
    INTO dt_year, installation_date, license_end_date
    FROM license_key lk
    LIMIT 1;
    
    -- Get patient statistics
    SELECT COUNT(*) INTO total_patients FROM patient_master;
    
    SELECT COUNT(*) INTO last_days 
    FROM patient_master 
    WHERE created_on > (CURRENT_DATE - INTERVAL '30 days');
    
    SELECT COUNT(*) INTO female_patients 
    FROM patient_master 
    WHERE gender_id = 'F';
    
    SELECT COUNT(*) INTO male_patients 
    FROM patient_master 
    WHERE gender_id = 'M';
    
    SELECT COUNT(*) INTO patients_visits 
    FROM patient_visits 
    WHERE delete_flag = false;
    
    -- Calculate patients per day (handle division by zero)
    IF perday > 0 THEN
        SELECT (COUNT(*) / perday)::INTEGER INTO patients_per_day 
        FROM patient_visits 
        WHERE delete_flag = false;
    ELSE
        patients_per_day := 0;
    END IF;
    
    -- Get prescription count (handle case when table doesn't exist)
    BEGIN
        SELECT COUNT(*) INTO prescription_count 
        FROM visit_prescription_overwrite;
    EXCEPTION
        WHEN UNDEFINED_TABLE THEN
            prescription_count := 0;
    END;
    
    -- Get main doctor ID (handle case when column doesn't exist)
    BEGIN
        SELECT doctor_id INTO main_doctor_id 
        FROM system_params 
        WHERE param_name = 'IS_MAIN_DOCTOR' 
        LIMIT 1;
    EXCEPTION
        WHEN UNDEFINED_COLUMN THEN
            main_doctor_id := NULL;
    END;

    -- Build result JSON
    result := json_build_object(
        'dtYear', COALESCE(dt_year, EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER),
        'installationDate', COALESCE(installation_date::TEXT, 'N/A'),
        'totalPatients', total_patients,
        'newPatientsLast30Days', last_days,
        'femalePatients', female_patients,
        'malePatients', male_patients,
        'totalPatientVisits', patients_visits,
        'patientsPerDay', patients_per_day,
        'prescriptionCount', prescription_count,
        'licenseEndDate', COALESCE(license_end_date::TEXT, 'N/A'),
        'mainDoctorId', COALESCE(main_doctor_id, 'N/A')
    );
    
    RETURN result;
END;
$$;
