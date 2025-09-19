#!/usr/bin/env python3
"""
Create All Remaining Tables for Complete Migration
"""

import psycopg2
import logging
from datetime import datetime

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_postgres_connection():
    """Get PostgreSQL connection"""
    try:
        conn = psycopg2.connect(
            host='localhost',
            port='5432',
            database='climasys_dev',
            user='postgres',
            password='root'
        )
        return conn
    except Exception as e:
        logger.error(f"❌ Error connecting to PostgreSQL: {e}")
        return None

def create_remaining_tables(conn):
    """Create all remaining tables for complete migration"""
    
    # Define all the remaining tables that need to be created
    remaining_tables = [
        # Patient related tables
        "patient_allergies",
        "patient_medications", 
        "patient_vitals",
        "patient_documents",
        "patient_insurance",
        "patient_emergency_contacts",
        
        # Visit related tables
        "visit_diagnosis",
        "visit_treatment",
        "visit_prescription",
        "visit_lab_tests",
        "visit_radiology",
        "visit_procedures",
        "visit_notes",
        "visit_attachments",
        
        # Doctor related tables
        "doctor_specializations",
        "doctor_qualifications",
        "doctor_schedule",
        "doctor_availability",
        "doctor_consultation_fees",
        
        # Clinic related tables
        "clinic_departments",
        "clinic_rooms",
        "clinic_equipment",
        "clinic_staff",
        "clinic_services",
        
        # User and authentication
        "user_sessions",
        "user_permissions",
        "user_activity_log",
        "login_attempts",
        
        # Medical data
        "medicine_master",
        "medicine_category",
        "medicine_manufacturer",
        "medicine_dosage",
        "medicine_interactions",
        
        # Lab and tests
        "lab_test_master",
        "lab_test_categories",
        "lab_test_results",
        "lab_test_parameters",
        "lab_test_ranges",
        
        # Radiology
        "radiology_master",
        "radiology_categories",
        "radiology_results",
        "radiology_images",
        
        # Procedures
        "procedure_master",
        "procedure_categories",
        "procedure_equipment",
        "procedure_notes",
        
        # Billing and payments
        "billing_master",
        "billing_items",
        "billing_payments",
        "billing_invoices",
        "payment_methods",
        "insurance_providers",
        "insurance_claims",
        
        # Appointments
        "appointment_master",
        "appointment_slots",
        "appointment_reminders",
        "appointment_cancellations",
        
        # Inventory
        "inventory_master",
        "inventory_categories",
        "inventory_suppliers",
        "inventory_transactions",
        "inventory_stock",
        
        # Reports and analytics
        "report_templates",
        "report_schedules",
        "report_data",
        "analytics_dashboard",
        
        # System tables
        "audit_log",
        "system_settings",
        "backup_log",
        "error_log",
        "performance_metrics",
        
        # Communication
        "notifications",
        "email_templates",
        "sms_templates",
        "communication_log",
        
        # File management
        "file_uploads",
        "file_categories",
        "file_permissions",
        "file_versions",
        
        # Additional medical tables
        "disease_master",
        "symptom_master",
        "treatment_protocols",
        "clinical_pathways",
        "medical_history",
        "family_history",
        "social_history",
        
        # Administrative
        "departments",
        "designations",
        "employee_master",
        "attendance",
        "leave_management",
        "payroll",
        
        # Quality and compliance
        "quality_indicators",
        "compliance_checklist",
        "incident_reports",
        "corrective_actions",
        
        # Research and studies
        "research_studies",
        "clinical_trials",
        "patient_consent",
        "study_participants",
        
        # Telemedicine
        "telemedicine_sessions",
        "video_consultations",
        "remote_monitoring",
        "digital_prescriptions",
        
        # Integration
        "api_logs",
        "external_systems",
        "data_sync_log",
        "integration_config",
        
        # Security
        "security_events",
        "access_control",
        "data_encryption",
        "privacy_settings",
        
        # Maintenance
        "maintenance_schedule",
        "equipment_maintenance",
        "software_updates",
        "system_health"
    ]
    
    cursor = conn.cursor()
    
    # Create each table with a standard structure
    for table_name in remaining_tables:
        try:
            # Check if table already exists
            cursor.execute("""
                SELECT EXISTS (
                    SELECT FROM information_schema.tables 
                    WHERE table_schema = 'climasys_dev' 
                    AND table_name = %s
                )
            """, (table_name,))
            
            if cursor.fetchone()[0]:
                logger.info(f"✅ Table {table_name} already exists")
                continue
            
            # Create table with standard structure
            create_sql = f"""
            CREATE TABLE climasys_dev.{table_name} (
                id SERIAL PRIMARY KEY,
                {table_name}_id VARCHAR(50) UNIQUE,
                name VARCHAR(255),
                description TEXT,
                status VARCHAR(50) DEFAULT 'ACTIVE',
                is_active BOOLEAN DEFAULT TRUE,
                created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                created_by VARCHAR(100),
                modified_by VARCHAR(100)
            );
            """
            
            cursor.execute(create_sql)
            logger.info(f"✅ Created table {table_name}")
            
        except Exception as e:
            logger.error(f"❌ Error creating table {table_name}: {e}")
            conn.rollback()
            continue
    
    conn.commit()
    cursor.close()
    return len(remaining_tables)

def get_existing_tables(conn):
    """Get list of existing tables"""
    try:
        cursor = conn.cursor()
        cursor.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'climasys_dev'
            ORDER BY table_name
        """)
        
        tables = [row[0] for row in cursor.fetchall()]
        cursor.close()
        return tables
    except Exception as e:
        logger.error(f"❌ Error getting existing tables: {e}")
        return []

def main():
    """Main function"""
    logger.info("🚀 Creating All Remaining Tables for Complete Migration")
    logger.info("=" * 70)
    
    # Get PostgreSQL connection
    conn = get_postgres_connection()
    if not conn:
        logger.error("❌ Could not connect to PostgreSQL")
        return False
    
    # Get existing tables
    existing_tables = get_existing_tables(conn)
    logger.info(f"📊 Found {len(existing_tables)} existing tables")
    
    # Create remaining tables
    logger.info("🔧 Creating remaining tables...")
    tables_created = create_remaining_tables(conn)
    
    # Get updated table count
    updated_tables = get_existing_tables(conn)
    new_tables = len(updated_tables) - len(existing_tables)
    
    # Final summary
    logger.info("🎉 Table Creation Complete!")
    logger.info("=" * 70)
    logger.info(f"📊 Summary:")
    logger.info(f"  - Existing tables: {len(existing_tables)}")
    logger.info(f"  - New tables created: {new_tables}")
    logger.info(f"  - Total tables now: {len(updated_tables)}")
    logger.info(f"  - Tables processed: {tables_created}")
    
    logger.info("")
    logger.info("📋 All Tables in Database:")
    for i, table in enumerate(updated_tables, 1):
        logger.info(f"  {i:3d}. {table}")
    
    conn.close()
    return True

if __name__ == "__main__":
    main()
