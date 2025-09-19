# Business APIs Documentation

This document provides a comprehensive list of all business-focused APIs generated from stored procedures in the Climasys system.

## Base URL
All APIs are available under the base URL: `http://localhost:8080/api`

## Patient Management APIs

### Patient Reports (`/patients/reports`)

#### Comprehensive Reports
- **GET** `/patients/reports/comprehensive?dateFrom={date}&dateTo={date}` - Get comprehensive patient report for a date range
- **GET** `/patients/reports/distribution-by-area?dateFrom={date}&dateTo={date}` - Get patient distribution by area/region
- **GET** `/patients/reports/family-fee-analysis?dateFrom={date}&dateTo={date}` - Get family-based fee analysis
- **GET** `/patients/reports/operator-family-fee-analysis?dateFrom={date}&dateTo={date}` - Get operator-wise family fee analysis
- **GET** `/patients/reports/consolidated-fee-report?dateFrom={date}&dateTo={date}` - Get consolidated fee report
- **GET** `/patients/reports/discharge-summary?dateFrom={date}&dateTo={date}` - Get discharge summary by date range

### Patient Data Management (`/patients/data`)

#### Family Management
- **GET** `/patients/data/family/{familyId}` - Get family information and details
- **GET** `/patients/data/family-folder/{familyId}` - Get family folder details for record management

#### Patient Information
- **GET** `/patients/data/admission-card/{patientId}` - Get admission card data for patient admission

#### Data Validation
- **GET** `/patients/data/validate-billing?patientId={id}&visitId={id}` - Validate patient billing data before processing
- **GET** `/patients/data/validate-discharge?patientId={id}&visitId={id}` - Validate patient discharge data
- **GET** `/patients/data/validate-invoice?patientId={id}&visitId={id}` - Validate patient invoice data

## Doctor Management APIs

### Doctor Operations (`/doctors`)

#### Doctor Information
- **GET** `/doctors/all` - Get all available doctors in the system
- **GET** `/doctors/adhoc-available` - Get doctors available for adhoc appointments
- **GET** `/doctors/for-patient/{patientId}` - Get doctors assigned to a specific patient
- **GET** `/doctors/{doctorId}/details` - Get detailed information about a specific doctor
- **GET** `/doctors/count` - Get total count of doctors in the system

#### Doctor Workflow
- **GET** `/doctors/{doctorId}/ready-for-submission` - Get doctors who are ready to submit their work
- **GET** `/doctors/status-reference` - Get doctor status reference data
- **GET** `/doctors/{doctorId}/todays-visits` - Get today's visits for a specific doctor
- **GET** `/doctors/{doctorId}/fees-to-collect` - Get fees to be collected by a doctor

### Doctor Queue Management (`/doctors/queue`)

#### Waiting List Management
- **GET** `/doctors/queue/{doctorId}/waiting-patients` - Get patients waiting for a specific doctor
- **GET** `/doctors/queue/{doctorId}/waiting-status` - Get waiting status for a specific doctor

### Clinic Management (`/clinics`)

#### Clinic Information
- **GET** `/clinics/{clinicId}/details` - Get clinic details and information
- **GET** `/clinics/{clinicId}/shifts` - Get clinic shifts and schedules
- **GET** `/clinics/{clinicId}/shift-timings?shiftDay={day}` - Get clinic shift timings for a specific day

## Billing and Financial APIs

### Billing Calculations (`/billing/calculations`)

#### Amount Calculations
- **GET** `/billing/calculations/visit-amount?patientId={id}&visitId={id}` - Calculate total amount for a patient visit
- **GET** `/billing/calculations/billing-status?patientId={id}&visitId={id}` - Get billing status for a patient visit
- **GET** `/billing/calculations/default-fees/{doctorId}` - Get default fees for a doctor
- **GET** `/billing/calculations/default-prescription-count/{doctorId}` - Get default prescription count for a doctor
- **GET** `/billing/calculations/hospital-bill?patientId={id}&visitId={id}` - Generate hospital bill for a patient visit

### Billing Master Data (`/billing/master-data`)

#### Master Data Management
- **GET** `/billing/master-data/categories/{doctorId}` - Get billing categories grouped by doctor
- **GET** `/billing/master-data/companies/{doctorId}` - Get companies for billing

#### Data Deletion
- **DELETE** `/billing/master-data/charges/{chargeId}` - Delete billing charges
- **DELETE** `/billing/master-data/keyword-charges/{keywordChargeId}` - Delete bill keyword charges
- **DELETE** `/billing/master-data/sub-charges/{subChargeId}` - Delete bill sub charges
- **DELETE** `/billing/master-data/billing-details/{billingDetailId}` - Delete master billing detail
- **DELETE** `/billing/master-data/companies/{companyId}` - Delete master company

## Lab and Test Management APIs

### Lab Tests (`/lab/tests`)

#### Test Information
- **GET** `/lab/tests/{testId}` - Get lab test information by test ID
- **GET** `/lab/tests/{testId}/with-parameters` - Get lab test with parameters
- **GET** `/lab/tests/{testId}/details` - Get lab test details
- **GET** `/lab/tests/{testId}/details-v1` - Get lab test details (version 1)
- **GET** `/lab/tests/{testId}/details-v12` - Get lab test details (version 12)
- **GET** `/lab/tests/by-name/{testName}` - Get lab test ID by test name
- **GET** `/lab/tests/{testId}/parameters` - Get lab test parameters
- **GET** `/lab/tests/for-appointment-deletion/{appointmentId}` - Get lab test data for appointment deletion

### Lab Reports (`/lab/reports`)

#### Report Generation
- **GET** `/lab/reports/summary?dateFrom={date}&dateTo={date}` - Get labs summary report for a date range
- **GET** `/lab/reports/download-summary?dateFrom={date}&dateTo={date}` - Get lab summary for download report

### Lab Data Management (`/lab/data-management`)

#### Data Operations
- **DELETE** `/lab/data-management/test-records/{testId}` - Delete lab test records
- **DELETE** `/lab/data-management/test-parameters/{parameterId}` - Delete lab test parameter

## Appointment and Scheduling APIs

### Appointment Scheduling (`/appointments`)

#### Appointment Management
- **GET** `/appointments/{doctorId}/future` - Get all future appointments for a doctor
- **GET** `/appointments/{doctorId}/future-for-date?appointmentDate={date}` - Get future appointments for a specific date
- **GET** `/appointments/{doctorId}/holidays` - Get holiday details for a doctor
- **GET** `/appointments/gender-options` - Get gender options for appointments

### Patient Examinations (`/examinations`)

#### Clinical Data
- **GET** `/examinations/gynecological/ovulation/{patientId}` - Get gynecological ovulation data for a patient
- **GET** `/examinations/gynecological/delivery-registration/{patientId}` - Get gynecological delivery registration data
- **GET** `/examinations/abdominal?patientId={id}&visitId={id}` - Get abdominal examination data

## Medicine and Prescription APIs

### Medicine Master Data (`/medicine/master-data`)

#### Medicine Information
- **GET** `/medicine/master-data/active-medicines/{doctorId}` - Get active medicines for a doctor
- **GET** `/medicine/master-data/active-prescriptions/{doctorId}` - Get active prescriptions for a doctor
- **GET** `/medicine/master-data/bld-medicine?patientId={id}&visitId={id}` - Get BLD (Before Last Date) medicine data
- **GET** `/medicine/master-data/bld-prescription?patientId={id}&visitId={id}` - Get BLD (Before Last Date) prescription data

#### Master Data
- **GET** `/medicine/master-data/categories/{doctorId}` - Get medicine categories for a doctor
- **GET** `/medicine/master-data/diseases/{doctorId}` - Get disease master data
- **GET** `/medicine/master-data/findings/{doctorId}` - Get findings master data
- **GET** `/medicine/master-data/keywords/{doctorId}` - Get keyword master data
- **GET** `/medicine/master-data/keywords-hospital/{doctorId}` - Get keyword master data for hospital

## Insurance Management APIs

### Insurance Companies (`/insurance/companies`)

#### Company Management
- **PUT** `/insurance/companies/{companyId}?companyName={name}&userId={id}&doctorId={id}` - Edit insurance company information
- **POST** `/insurance/companies?companyName={name}&companyId={id}&userId={id}&doctorId={id}` - Insert new insurance company
- **DELETE** `/insurance/companies/{companyId}` - Delete insurance company

#### Insurance Operations
- **GET** `/insurance/companies/check-discharge-print?patientId={id}&visitId={id}` - Check if discharge printing is enabled for insurance

## Usage Examples

### Example 1: Get comprehensive patient report
```bash
curl -X GET "http://localhost:8080/api/patients/reports/comprehensive?dateFrom=2024-01-01&dateTo=2024-12-31"
```

### Example 2: Get doctor details
```bash
curl -X GET "http://localhost:8080/api/doctors/DOCTOR001/details"
```

### Example 3: Calculate visit amount
```bash
curl -X GET "http://localhost:8080/api/billing/calculations/visit-amount?patientId=PATIENT001&visitId=VISIT001"
```

### Example 4: Get lab test details
```bash
curl -X GET "http://localhost:8080/api/lab/tests/TEST001/details"
```

### Example 5: Get future appointments
```bash
curl -X GET "http://localhost:8080/api/appointments/DOCTOR001/future"
```

### Example 6: Get active medicines
```bash
curl -X GET "http://localhost:8080/api/medicine/master-data/active-medicines/DOCTOR001"
```

### Example 7: Get family information
```bash
curl -X GET "http://localhost:8080/api/patients/data/family/FAMILY001"
```

### Example 8: Get clinic shifts
```bash
curl -X GET "http://localhost:8080/api/clinics/CLINIC001/shifts"
```

## API Response Format

All APIs return data in the following format:

### Success Response
```json
[
  {
    "field1": "value1",
    "field2": "value2",
    "field3": 123
  },
  {
    "field1": "value3",
    "field2": "value4",
    "field3": 456
  }
]
```

### Error Response
```json
{
  "error": "Error message",
  "procedure": "Procedure name"
}
```

## HTTP Status Codes

- **200 OK** - Successful execution
- **400 Bad Request** - Invalid parameters or stored procedure error
- **500 Internal Server Error** - Server error

## Date Format

All date parameters should be in `YYYY-MM-DD` format (ISO 8601).

## Authentication

Currently, all APIs are open for development. In production, proper authentication and authorization should be implemented.

## CORS Support

All APIs support CORS for cross-origin requests from web applications.

## Database Connection

The APIs connect to the PostgreSQL database configured in `application.yml`:
- Database: `climasys_dev`
- Host: `localhost`
- Port: `5432`
- Username: `postgres`
- Password: `root`

## Notes

1. All stored procedures are executed using the PostgreSQL database
2. Delete operations are soft deletes where applicable
3. All APIs return data in JSON format
4. The APIs are organized by business functionality for better maintainability
5. Each API endpoint has a clear, business-focused name that describes its purpose
6. Parameters are validated and properly typed
7. Error handling is consistent across all endpoints
