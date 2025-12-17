-- ============================================================================
-- Dummy Data for OPD Daily Collection
-- ============================================================================
-- This script inserts complete dummy data including:
-- 1. Patient master records (10 patients)
-- 2. Patient visit records (10 completed visits for today)
-- All records are for testing the OPD Daily Collection report
-- ============================================================================

-- ============================================================================
-- STEP 1: Insert dummy patients into patient_master table
-- ============================================================================

INSERT INTO patient_master (
    id, clinic_id, first_name, middle_name, last_name, 
    gender_id, date_of_birth, folder_no, mobile_no,
    created_on, createdby_name, delete_flag
) VALUES
('PAT001', 'CLINIC001', 'AMIT', 'SOMRA', 'JAIN', 1, '1985-03-15', 'F001', '9876543201', CURRENT_TIMESTAMP, 'admin', false),
('PAT002', 'CLINIC001', 'RUPINA', 'J', 'SHAHJI', 2, '1990-07-22', 'F002', '9876543202', CURRENT_TIMESTAMP, 'admin', false),
('PAT003', 'CLINIC001', 'PRIYA', '', 'SHARMA', 2, '1988-11-10', 'F003', '9876543203', CURRENT_TIMESTAMP, 'admin', false),
('PAT004', 'CLINIC001', 'RAHUL', '', 'VERMA', 1, '1975-05-18', 'F004', '9876543204', CURRENT_TIMESTAMP, 'admin', false),
('PAT005', 'CLINIC001', 'SNEHA', '', 'PATEL', 2, '1992-09-25', 'F005', '9876543205', CURRENT_TIMESTAMP, 'admin', false),
('PAT006', 'CLINIC001', 'VIKRAM', '', 'SINGH', 1, '1980-12-30', 'F006', '9876543206', CURRENT_TIMESTAMP, 'admin', false),
('PAT007', 'CLINIC001', 'ANJALI', '', 'DESAI', 2, '1995-02-14', 'F007', '9876543207', CURRENT_TIMESTAMP, 'admin', false),
('PAT008', 'CLINIC001', 'KARAN', '', 'MEHTA', 1, '1987-08-05', 'F008', '9876543208', CURRENT_TIMESTAMP, 'admin', false),
('PAT009', 'CLINIC001', 'MEERA', '', 'IYER', 2, '1991-04-20', 'F009', '9876543209', CURRENT_TIMESTAMP, 'admin', false),
('PAT010', 'CLINIC001', 'SURESH', '', 'KUMAR', 1, '1978-06-12', 'F010', '9876543210', CURRENT_TIMESTAMP, 'admin', false)
ON CONFLICT (id, clinic_id) DO NOTHING;

-- ============================================================================
-- STEP 2: Ensure gender translations exist (if not already present)
-- ============================================================================

INSERT INTO gender_translations (gender_id, language_id, gender_description)
VALUES 
(1, 1, 'Male'),
(2, 1, 'Female'),
(3, 1, 'Other')
ON CONFLICT (gender_id, language_id) DO NOTHING;

-- ============================================================================
-- STEP 3: Ensure payment types exist (if not already present)
-- ============================================================================

INSERT INTO payment_type_master (id, payment_description)
VALUES 
(1, 'Cash'),
(2, 'UPI'),
(3, 'Card'),
(4, 'Cheque'),
(5, 'Online')
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- STEP 4: Insert 10 dummy patient visits for OPD Daily Collection
-- ============================================================================
-- All visits have status_id = 5 (Completed) and are for today's date
-- This ensures they appear in the OPD Daily Collection report

INSERT INTO patient_visits (
    doctor_id, clinic_id, shift_id, patient_id, patient_visit_no, visit_date,
    folder_no, visit_time, financial_year, status_id,
    fees_to_collect, fees_collected, original_billed_amount,
    discount, original_discount,
    in_person, payment_by_id, payment_remark, is_follow_up,
    comment, delete_flag, created_on, createdby_name
) VALUES
-- Record 1: AMIT SOMRA JAIN (Follow up) - Cash payment
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT001', 1, '2025-12-15 10:00:21', 
 'F001', '10:00:21', 2025, 5,
 500.00, 500.00, 500.00,
 0.00, 0.00,
 true, 1, 'Cash', true,
 'Follow up visit completed', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 2: RUPINA J SHAHJI (Follow up) - Cash payment
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT002', 1, '2025-12-15 10:55:58',
 'F002', '10:55:58', 2025, 5,
 500.00, 500.00, 500.00,
 0.00, 0.00,
 true, 1, 'Cash', true,
 'Follow up visit completed', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 3: PRIYA SHARMA (New) - UPI payment with discount
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT003', 1, '2025-12-15 09:30:15',
 'F003', '09:30:15', 2025, 5,
 800.00, 750.00, 800.00,
 50.00, 50.00,
 true, 2, 'UPI', false,
 'New patient consultation', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 4: RAHUL VERMA (Follow up) - Cash payment
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT004', 2, '2025-12-15 11:20:30',
 'F004', '11:20:30', 2025, 5,
 600.00, 600.00, 600.00,
 0.00, 0.00,
 true, 1, 'Cash', true,
 'Regular checkup', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 5: SNEHA PATEL (New) - Card payment with discount
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT005', 1, '2025-12-15 12:15:45',
 'F005', '12:15:45', 2025, 5,
 1000.00, 900.00, 1000.00,
 100.00, 100.00,
 true, 3, 'Card', false,
 'First consultation', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 6: VIKRAM SINGH (Follow up) - Cash payment
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT006', 3, '2025-12-15 14:00:00',
 'F006', '14:00:00', 2025, 5,
 500.00, 500.00, 500.00,
 0.00, 0.00,
 true, 1, 'Cash', true,
 'Follow up after medication', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 7: ANJALI DESAI (New) - UPI payment
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT007', 1, '2025-12-15 15:30:20',
 'F007', '15:30:20', 2025, 5,
 700.00, 700.00, 700.00,
 0.00, 0.00,
 true, 2, 'UPI', false,
 'New patient - general consultation', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 8: KARAN MEHTA (Follow up) - Partial payment (Cash)
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT008', 2, '2025-12-15 16:45:10',
 'F008', '16:45:10', 2025, 5,
 500.00, 450.00, 500.00,
 0.00, 0.00,
 true, 1, 'Cash', true,
 'Follow up visit - partial payment', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 9: MEERA IYER (New) - Card payment
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT009', 1, '2025-12-15 17:20:35',
 'F009', '17:20:35', 2025, 5,
 900.00, 900.00, 900.00,
 0.00, 0.00,
 true, 3, 'Card', false,
 'New patient consultation', false, CURRENT_TIMESTAMP, 'admin'),

-- Record 10: SURESH KUMAR (Follow up) - Cash payment
('Dr. Tongaonkar', 'CLINIC001', 1, 'PAT010', 4, '2025-12-15 18:00:00',
 'F010', '18:00:00', 2025, 5,
 600.00, 600.00, 600.00,
 0.00, 0.00,
 true, 1, 'Cash', true,
 'Regular follow up', false, CURRENT_TIMESTAMP, 'admin')
ON CONFLICT (doctor_id, clinic_id, shift_id, patient_id, patient_visit_no, visit_date) DO NOTHING;

-- ============================================================================
-- Summary of inserted data:
-- ============================================================================
-- Total Patients: 10 (PAT001 to PAT010)
-- Total Visits: 10 (all completed, status_id = 5)
-- Visit Date: 2025-12-15 (today)
-- Doctor: Dr. Tongaonkar
-- Clinic: CLINIC001
-- 
-- Payment Distribution:
-- - Cash: 6 records (PAT001, PAT002, PAT004, PAT006, PAT008, PAT010)
-- - UPI: 2 records (PAT003, PAT007)
-- - Card: 2 records (PAT005, PAT009)
--
-- Visit Type Distribution:
-- - New Patients: 5 (PAT003, PAT005, PAT007, PAT009)
-- - Follow-up: 5 (PAT001, PAT002, PAT004, PAT006, PAT008, PAT010)
--
-- Total Collection: 6,600.00
-- Total Fees to Collect: 6,600.00
-- Total Discount: 150.00
-- ============================================================================
