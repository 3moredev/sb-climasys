-- Database initialization script for Climasys
-- This script creates a simple login function for testing

-- Create a simple login function (PostgreSQL version)
CREATE OR REPLACE FUNCTION usp_get_logindetails_json(
    p_login_id VARCHAR(60),
    p_password VARCHAR(100),
    p_todays_day VARCHAR(20),
    p_language_id INTEGER
)
RETURNS JSON
LANGUAGE plpgsql
AS $$
DECLARE
    result_json JSON;
BEGIN
    -- For now, return a simple test response
    -- In a real implementation, you would validate against actual database tables
    IF p_login_id = 'test_user' AND p_password = 'test_password' THEN
        result_json := json_build_object(
            'login_status', 1,
            'user_details', json_build_array(
                json_build_object(
                    'user_id', 1,
                    'doctor_id', 'DOC001',
                    'clinic_id', 'CLINIC001',
                    'login_id', p_login_id,
                    'first_name', 'Test',
                    'password', p_password,
                    'role_name', 'Doctor',
                    'role_id', 1,
                    'doctor_name', 'Dr. Test User',
                    'clinic_name', 'Test Clinic',
                    'language_id', p_language_id,
                    'is_active', true,
                    'financial_year', 2024,
                    'model_id', 1,
                    'config_id', 1,
                    'is_enabled', true
                )
            ),
            'shift_times', json_build_array(
                json_build_object(
                    'shift_id', 1,
                    'description', 'Morning Shift - Monday - 09 - 17'
                )
            ),
            'available_roles', json_build_array(
                json_build_object(
                    'role_id', 2,
                    'role_name', 'Nurse'
                )
            ),
            'system_params', json_build_array(
                json_build_object(
                    'param_name', 'test_param',
                    'param_value', 'test_value',
                    'doctor_id', 'DOC001'
                )
            ),
            'license_key', 'TEST_LICENSE_KEY_12345'
        );
    ELSE
        result_json := json_build_object('login_status', 0);
    END IF;

    RETURN result_json;
EXCEPTION
    WHEN OTHERS THEN
        RETURN json_build_object(
            'login_status', -1,
            'error_code', SQLSTATE,
            'error_message', SQLERRM
        );
END;
$$;
