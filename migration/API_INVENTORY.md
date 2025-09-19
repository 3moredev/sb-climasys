## API Inventory (ASMX → REST draft)

Note: This lists representative mappings. Full coverage will be iterated as controllers are implemented.

Auth & Users
- POST /api/v1/auth/login → GetLoginDetails

Clinics & Shifts
- GET /api/v1/clinics/{clinicId}/shifts?doctorId=&day= → GetClinicShifts

Patients
- POST /api/v1/patients → Save_PatientRegistration
- PUT  /api/v1/patients/{id} → Save_FullRegistrationDetails
- GET  /api/v1/patients/{id} → Get_Patient_Details
- GET  /api/v1/patients/{id}/family → GetFamilyFolderDetails
- GET  /api/v1/patients/{id}/max-visit → Get_Patient_MaxVisit_No
- GET  /api/v1/patients/search?q=&status=pending → Search_PatientPending_Details

Appointments & Visits
- POST /api/v1/appointments → Book_Patient_Appointment|..._Doctor|..._Operator
- POST /api/v1/visits → Save_PatientAddToVisitList
- GET  /api/v1/visits/today?doctorId=&shiftId=&clinicId=&roleId= → GetTodaysVisitDetails
- DELETE /api/v1/visits/{visitId} → DeleteTodaysVisitRecord
- POST /api/v1/visits/{visitId}/save → TodaysVisit_Details_Save

Clinical: Profiles & Findings
- POST /api/v1/visits/{visitId}/profile → InsertPatientProfile
- POST /api/v1/visits/{visitId}/complaints → InsertComplaintsGrid
- POST /api/v1/visits/{visitId}/diagnoses → InsertDignosisGrid
- POST /api/v1/visits/{visitId}/dressings → InsertDressingGrid

Lab Tests
- GET  /api/v1/lab/tests?doctorId= → BindLabTest
- POST /api/v1/visits/{visitId}/lab-tests → InsertLabTestGrid
- POST /api/v1/visits/{visitId}/lab-results → Save_TestReport
- GET  /api/v1/visits/{visitId}/lab-results/previous → Get_LastTestReports_Details

Prescriptions & Medicines
- POST /api/v1/visits/{visitId}/medicines → InsertMedicineGrid
- POST /api/v1/visits/{visitId}/prescriptions → InsertPrescriptionGrid

Reference Data
- GET /api/v1/reference/genders → GetGenderData
- GET /api/v1/reference/blood-groups → GetBloodGroup_Details
- GET /api/v1/reference/impressions?doctorId= → GetImpressionFinding_Details

Referral Doctors
- POST /api/v1/referrals → Save_Referral_Doctor_List
- GET  /api/v1/referrals/by-mobile?mobile= → Get_Referral_Doctor_Details_For_Mobile_No_Check
- PATCH /api/v1/referrals/visit → UpdateReferredDoctor

History
- GET /api/v1/patients/{id}/visits/dates → GetPreviousVisitDates
- GET /api/v1/visits/{visitId}/details → GetPreviousPatientVisitData
- GET /api/v1/visits/{visitId}/labs → GetPreviousPatientVisitLabResult

Misc
- GET /api/v1/areas/{id}/name → GetAreaName_By_AreaId
- GET /api/v1/areas/search?q= → GetAreaDetails_by_QuickRegistration
- GET /api/v1/folders/check?no= → Check_Folder_No
- GET /api/v1/patients/check?id=&date=&shiftId= → Check_Patient_Id

To be continued for Billing, IPD/Discharge, Admin, MR, Kiosk.

