-- Dashboard function for PostgreSQL
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
    -- Calculate per day visits
    SELECT COALESCE(COUNT(DISTINCT visit_date), 1) INTO perday 
    FROM patient_visits 
    WHERE delete_flag = false;
    
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
    
    SELECT (COUNT(*) / perday)::INTEGER INTO patients_per_day 
    FROM patient_visits 
    WHERE delete_flag = false;
    
    SELECT COUNT(*) INTO prescription_count 
    FROM visit_prescription_overwrite;
    
    -- Get main doctor ID
    SELECT doctor_id INTO main_doctor_id 
    FROM system_params 
    WHERE is_main_doctor = true 
    LIMIT 1;
    
    -- Build result JSON
    result := json_build_object(
        'dtYear', COALESCE(dt_year, EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER),
        'installationDate', COALESCE(installation_date::TEXT, 'N/A'),
        'totalPatients', total_patients,
        'lastdays', last_days,
        'female', female_patients,
        'male', male_patients,
        'patientsVisits', patients_visits,
        'perday', patients_per_day,
        'prescription', prescription_count,
        'licenseEndDate', COALESCE(license_end_date::TEXT, 'N/A'),
        'mainDoctorId', COALESCE(main_doctor_id, 'N/A')
    );
    
    RETURN result;
END;
$$;
