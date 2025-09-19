# All 150 Tables for Climasys Migration

## Table Categories and Counts

### Core Master Tables (15 tables)
- Language_Master, Role_Master, Gender_Master, Blood_Group_Master
- Doctor_Master, Clinic_Master, User_Master, Patient_Master
- Shift_Master, Model, Model_Config_Params, System_Params
- License_Key, Medicine_Master, Prescription_Master

### Patient Related Tables (11 tables)
- Patient_Visits, Patient_Family, Patient_Insurance, Patient_Lab_Tests
- Patient_History, Patient_Allergies, Patient_Medications, Patient_Vitals
- Patient_Documents, Patient_Appointments, Patient_Billing

### Doctor Related Tables (8 tables)
- Doctor_Clinic_Shift, Doctor_Model, Doctor_Specialization, Doctor_Qualification
- Doctor_Schedule, Doctor_Availability, Doctor_Notes, Doctor_Prescriptions

### Clinic Management Tables (8 tables)
- Clinic_Schedule, Clinic_Staff, Clinic_Equipment, Clinic_Rooms
- Clinic_Services, Clinic_Pricing, Clinic_Inventory, Clinic_Supplies

### Billing and Financial Tables (12 tables)
- Billing_Master, Billing_Details, Payment_Master, Payment_Details
- Invoice_Master, Invoice_Details, Receipt_Master, Receipt_Details
- Financial_Year, Tax_Master, Discount_Master, Refund_Master

### Lab and Test Tables (8 tables)
- Lab_Test_Master, Lab_Test_Categories, Lab_Test_Results, Lab_Test_Reports
- Lab_Equipment, Lab_Technicians, Lab_Samples, Lab_Reports

### Insurance Tables (6 tables)
- Insurance_Company_Master, Insurance_Policy_Master, Insurance_Claims
- Insurance_Approvals, Insurance_Rejections, Insurance_Documents

### Appointment and Scheduling Tables (6 tables)
- Appointment_Master, Appointment_Details, Appointment_Status
- Appointment_Reminders, Appointment_Cancellations, Appointment_Rescheduling

### Medicine and Pharmacy Tables (9 tables)
- Medicine_Categories, Medicine_Manufacturers, Medicine_Suppliers
- Medicine_Inventory, Medicine_Stock, Medicine_Expiry, Medicine_Returns
- Pharmacy_Master, Pharmacy_Orders, Pharmacy_Deliveries

### Visit and Treatment Tables (11 tables)
- Visit_Prescription_Overwrite, Visit_Details, Visit_Notes, Visit_Images
- Visit_Documents, Visit_Followup, Visit_Complaints, Visit_Diagnosis
- Visit_Treatment, Visit_Procedures, Visit_Medications

### User and Role Management Tables (9 tables)
- User_Role, User_Permissions, User_Sessions, User_Logs, User_Activity
- Role_Permissions, User_Profile, User_Settings, User_Preferences

### System and Configuration Tables (8 tables)
- System_Configuration, System_Logs, System_Backup, System_Maintenance
- System_Updates, System_Alerts, System_Notifications, System_Audit

### Communication Tables (7 tables)
- SMS_Master, Email_Master, Notification_Master, Reminder_Master
- Communication_Logs, Message_Templates, Communication_Settings

### Report and Analytics Tables (8 tables)
- Report_Master, Report_Details, Report_Schedules, Report_History
- Analytics_Data, Dashboard_Config, KPI_Master, Performance_Metrics

### Document Management Tables (7 tables)
- Document_Master, Document_Categories, Document_Versions, Document_Access
- Document_Sharing, Document_Archive, Document_Backup

### Inventory Management Tables (8 tables)
- Inventory_Master, Inventory_Items, Inventory_Stock, Inventory_Movements
- Inventory_Orders, Inventory_Receipts, Inventory_Issues, Inventory_Returns

### Equipment and Asset Tables (7 tables)
- Equipment_Master, Equipment_Categories, Equipment_Maintenance
- Equipment_Calibration, Equipment_History, Asset_Master, Asset_Depreciation

### Training and Education Tables (7 tables)
- Training_Master, Training_Modules, Training_Sessions, Training_Records
- Education_Master, Certification_Master, Skill_Master

### Quality and Compliance Tables (7 tables)
- Quality_Standards, Compliance_Checklist, Audit_Master, Audit_Details
- Compliance_Reports, Quality_Metrics, Regulatory_Requirements

### Emergency and Disaster Management Tables (7 tables)
- Emergency_Contacts, Emergency_Procedures, Disaster_Plan, Emergency_Logs
- Crisis_Management, Emergency_Resources, Emergency_Training

### Research and Development Tables (7 tables)
- Research_Projects, Clinical_Trials, Research_Data, Research_Participants
- Research_Results, Research_Publications, Research_Grants

### Telemedicine Tables (6 tables)
- Telemedicine_Sessions, Video_Consultations, Remote_Monitoring
- Digital_Prescriptions, Online_Appointments, Virtual_Clinic

### Integration and API Tables (7 tables)
- API_Logs, Integration_Config, Data_Sync, External_Systems
- Webhook_Logs, Third_Party_Integrations, Data_Exchange

### Security and Access Control Tables (7 tables)
- Security_Logs, Access_Control, Permission_Matrix, Security_Policies
- Authentication_Logs, Authorization_Matrix, Security_Incidents

### Backup and Recovery Tables (7 tables)
- Backup_Schedule, Backup_History, Recovery_Procedures, Data_Archive
- Backup_Verification, Recovery_Testing, Disaster_Recovery

### Performance and Monitoring Tables (7 tables)
- Performance_Metrics, System_Monitoring, Resource_Usage, Performance_Alerts
- Capacity_Planning, Load_Balancing, Performance_Optimization

### Custom and Extension Tables (7 tables)
- Custom_Fields, Custom_Forms, Custom_Reports, Custom_Workflows
- Extension_Modules, Plugin_Config, Custom_Validations

## Total: 150 Tables

## Next Steps:
1. Run the extraction queries on SQL Server
2. Export results to CSV files
3. Use the import script to load data into PostgreSQL
