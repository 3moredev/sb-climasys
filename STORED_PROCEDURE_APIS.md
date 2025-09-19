# Stored Procedure APIs Documentation

This document provides a comprehensive list of all available APIs generated from stored procedures in the Climasys system.

## Base URL
All APIs are available under the base URL: `http://localhost:8080/api`

## Generic Stored Procedure Controller

### Execute Stored Procedure
- **POST** `/stored-procs/execute/{procedureName}` - Execute a stored procedure with parameters
- **GET** `/stored-procs/execute/{procedureName}` - Execute a stored procedure without parameters
- **POST** `/stored-procs/execute/{procedureName}/resultset/{resultSetName}` - Get specific result set
- **POST** `/stored-procs/execute/{procedureName}/first-resultset` - Get first result set

## Patient Management APIs

### Base URL: `/patients/stored-procs`

#### Reports
- **GET** `/all-patients-report?dateFrom={date}&dateTo={date}` - Get all patients report
- **GET** `/area-wise-patients?dateFrom={date}&dateTo={date}` - Get area-wise patients
- **GET** `/consolidated-family-fees?dateFrom={date}&dateTo={date}` - Get consolidated family fees
- **GET** `/consolidated-family-fees-operator?dateFrom={date}&dateTo={date}` - Get consolidated family fees for operator
- **GET** `/consolidated-fees?dateFrom={date}&dateTo={date}` - Get consolidated fees
- **GET** `/discharge-data-by-date?dateFrom={date}&dateTo={date}` - Get discharge data by date

#### Family Management
- **GET** `/family-details/{familyId}` - Get family details
- **GET** `/family-folder-details/{familyId}` - Get family folder details

#### Patient Data
- **GET** `/admission-card-data/{patientId}` - Get admission card data
- **GET** `/check-bill-data?patientId={id}&visitId={id}` - Check patient bill data
- **GET** `/check-discharge-data?patientId={id}&visitId={id}` - Check patient discharge data
- **GET** `/check-invoice-data?patientId={id}&visitId={id}` - Check patient invoice data

## Doctor Management APIs

### Base URL: `/doctors/stored-procs`

#### Doctor Information
- **GET** `/all-doctors` - Get all doctors
- **GET** `/all-doctors-adhoc` - Get all doctors for adhoc
- **GET** `/all-doctors-for-patient/{patientId}` - Get all doctors for patient
- **GET** `/doctor-details/{doctorId}` - Get doctor details
- **GET** `/doctor-count` - Get doctor count

#### Doctor Status & Visits
- **GET** `/waiting-patient/{doctorId}` - Get doctor waiting patient
- **GET** `/waiting-status/{doctorId}` - Get doctor waiting status
- **GET** `/doctors-before-submit/{doctorId}` - Get doctors before submit
- **GET** `/status-reference` - Get doctor status reference
- **GET** `/todays-visit/{doctorId}` - Get doctor today's visit
- **GET** `/fees-to-collect/{doctorId}` - Get fees to collect doctor

#### Clinic Management
- **GET** `/clinic-details/{clinicId}` - Get clinic details
- **GET** `/clinic-shifts/{clinicId}` - Get clinic shifts
- **GET** `/clinic-shifts-time/{clinicId}?shiftDay={day}` - Get clinic shifts time

## Lab Management APIs

### Base URL: `/lab/stored-procs`

#### Lab Tests
- **GET** `/lab-test/{testId}` - Get lab test
- **GET** `/lab-test-parameter/{testId}` - Get lab test and parameter
- **GET** `/lab-test-details/{testId}` - Get lab test details
- **GET** `/lab-test-details1/{testId}` - Get lab test details 1
- **GET** `/lab-test-details12/{testId}` - Get lab test details 12
- **GET** `/lab-test-id/{testName}` - Get lab test ID
- **GET** `/lab-test-parameter/{testId}` - Get lab test parameter

#### Lab Reports
- **GET** `/labs-summary-report?dateFrom={date}&dateTo={date}` - Get labs summary report
- **GET** `/lab-summary-download-report?dateFrom={date}&dateTo={date}` - Get lab summary for download report

#### Lab Data Management
- **GET** `/lab-test-delete-appointment/{appointmentId}` - Get lab test data for delete appointment
- **DELETE** `/lab-test-records/{testId}` - Delete lab test records
- **DELETE** `/lab-test-parameter/{parameterId}` - Delete lab test parameter

## Billing Management APIs

### Base URL: `/billing/stored-procs`

#### Billing Information
- **GET** `/amount?patientId={id}&visitId={id}` - Get amount
- **GET** `/amount-status?patientId={id}&visitId={id}` - Get amount status
- **GET** `/default-fees/{doctorId}` - Get default fees
- **GET** `/default-prescription-count/{doctorId}` - Get default prescription count
- **GET** `/hospital-bill?patientId={id}&visitId={id}` - Get hospital bill

#### Billing Categories & Companies
- **GET** `/category-data-billing-group/{doctorId}` - Get category data billing group
- **GET** `/company/{doctorId}` - Get company

#### IPD Management
- **GET** `/ipd-todays-dash-status/{doctorId}` - Get IPD today's dash status completed

#### Billing Deletions
- **DELETE** `/bill-charges/{chargeId}` - Delete bill charges
- **DELETE** `/bill-keyword-charges/{keywordChargeId}` - Delete bill keyword charges
- **DELETE** `/bill-sub-charges/{subChargeId}` - Delete bill sub charges
- **DELETE** `/master-billing-detail/{billingDetailId}` - Delete master billing detail
- **DELETE** `/master-company/{companyId}` - Delete master company

## Medicine/Prescription Management APIs

### Base URL: `/medicine/stored-procs`

#### Medicine Information
- **GET** `/active-medicine/{doctorId}` - Get active medicine
- **GET** `/active-prescription/{doctorId}` - Get active prescription
- **GET** `/bld-medicine?patientId={id}&visitId={id}` - Get BLD medicine
- **GET** `/bld-prescription?patientId={id}&visitId={id}` - Get BLD prescription

#### Master Data
- **GET** `/category-data/{doctorId}` - Get category data
- **GET** `/disease-data/{doctorId}` - Get disease data
- **GET** `/findings-data/{doctorId}` - Get findings data
- **GET** `/keyword-master-data/{doctorId}` - Get keyword master data
- **GET** `/keyword-master-data-hospital/{doctorId}` - Get keyword master data hospital

#### Medicine Deletions
- **DELETE** `/master-disease/{diseaseId}` - Delete master disease
- **DELETE** `/master-findings/{findingsId}` - Delete master findings
- **DELETE** `/master-keyword/{keywordId}` - Delete master keyword
- **DELETE** `/master-medicine/{medicineId}` - Delete master medicine
- **DELETE** `/master-prescription/{prescriptionId}` - Delete master prescription
- **DELETE** `/master-procedure/{procedureId}` - Delete master procedure
- **DELETE** `/master-symptom/{symptomId}` - Delete master symptom
- **DELETE** `/prescription-medicine/{prescriptionMedicineId}` - Delete prescription medicine
- **DELETE** `/prescription-medicine-overwrite/{prescriptionMedicineId}` - Delete prescription medicine overwrite

## Appointment Management APIs

### Base URL: `/appointments/stored-procs`

#### Appointments
- **GET** `/future-appointments-all-new/{doctorId}` - Get future appointments all new
- **GET** `/future-appointments-for-date/{doctorId}?appointmentDate={date}` - Get future appointments for given date
- **GET** `/holiday-details/{doctorId}` - Get holiday details

#### Patient Data
- **GET** `/gender` - Get gender
- **GET** `/gynec-ovulation-data/{patientId}` - Get gynec ovulation data
- **GET** `/gynec-delivery-registration/{patientId}` - Get gynec delivery registration
- **GET** `/abdominal-examination-data?patientId={id}&visitId={id}` - Get abdominal examination data

#### Deletions
- **DELETE** `/abdominal-findings/{findingsId}` - Delete abdominal findings
- **DELETE** `/attached-reminders/{reminderId}` - Delete attached reminders
- **DELETE** `/attached-treatment-files/{fileId}` - Delete attached treatment files
- **DELETE** `/category/{categoryId}` - Delete category
- **DELETE** `/detail/{detailId}` - Delete detail
- **DELETE** `/discharge-data/{dischargeId}` - Delete discharge data
- **DELETE** `/internal-medicine/{medicineId}` - Delete internal medicine
- **DELETE** `/internal-medicine-overwrite/{medicineId}` - Delete internal medicine overwrite
- **DELETE** `/invoice-keyword-medicines/{invoiceId}` - Delete invoice keyword medicines
- **DELETE** `/invoice-medicines/{invoiceId}` - Delete invoice medicines
- **DELETE** `/ipd-document/{documentId}` - Delete IPD document
- **DELETE** `/mr/{mrId}` - Delete MR
- **DELETE** `/mr-medicine/{mrMedicineId}` - Delete MR medicine
- **DELETE** `/parameters/{parameterId}` - Delete parameters
- **DELETE** `/patient-document/{documentId}` - Delete patient document
- **DELETE** `/patient-document-treatment/{documentId}` - Delete patient document treatment
- **DELETE** `/reminders/{reminderId}` - Delete reminders
- **DELETE** `/sub-category/{subCategoryId}` - Delete sub category
- **DELETE** `/treatment-detail/{treatmentDetailId}` - Delete treatment detail

## Insurance Management APIs

### Base URL: `/insurance/stored-procs`

#### Insurance Company Management
- **PUT** `/edit-company?companyName={name}&companyId={id}&userId={id}&doctorId={id}` - Edit insurance company master
- **POST** `/insert-company?companyName={name}&companyId={id}&userId={id}&doctorId={id}` - Insert insurance company master
- **DELETE** `/delete-company/{companyId}` - Delete insurance company

#### Insurance Checks
- **GET** `/check-enable-print-discharge?patientId={id}&visitId={id}` - Check enable print discharge

## Usage Examples

### Example 1: Get all patients report
```bash
curl -X GET "http://localhost:8080/api/patients/stored-procs/all-patients-report?dateFrom=2024-01-01&dateTo=2024-12-31"
```

### Example 2: Get doctor details
```bash
curl -X GET "http://localhost:8080/api/doctors/stored-procs/doctor-details/DOCTOR001"
```

### Example 3: Execute generic stored procedure
```bash
curl -X POST "http://localhost:8080/api/stored-procs/execute/USP_Get_DashboardData" \
  -H "Content-Type: application/json" \
  -d '{}'
```

### Example 4: Get lab test details
```bash
curl -X GET "http://localhost:8080/api/lab/stored-procs/lab-test-details/TEST001"
```

## Error Handling

All APIs return appropriate HTTP status codes:
- **200 OK** - Successful execution
- **400 Bad Request** - Invalid parameters or stored procedure error
- **500 Internal Server Error** - Server error

Error responses include:
```json
{
  "error": "Error message",
  "procedure": "Procedure name"
}
```

## Notes

1. All stored procedures are executed using the PostgreSQL database
2. Date parameters should be in YYYY-MM-DD format
3. All APIs support CORS for cross-origin requests
4. The generic stored procedure controller allows execution of any stored procedure by name
5. Delete operations are soft deletes where applicable
6. All APIs return data in JSON format

## Authentication

Currently, all APIs are open for development. In production, proper authentication and authorization should be implemented.

## Database Connection

The APIs connect to the PostgreSQL database configured in `application.yml`:
- Database: `climasys_dev`
- Host: `localhost`
- Port: `5432`
- Username: `postgres`
- Password: `root`
