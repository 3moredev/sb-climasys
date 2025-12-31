-- =====================================================
-- Create sub_category_master table
-- =====================================================
-- This table stores charges sub-category master data
-- =====================================================

CREATE TABLE IF NOT EXISTS sub_category_master (
    id BIGSERIAL PRIMARY KEY,
    charges_sub_category VARCHAR(255) NOT NULL UNIQUE,
    sort_order INTEGER DEFAULT 0
);

-- Create index on charges_sub_category for faster lookups
CREATE INDEX IF NOT EXISTS idx_sub_category_master_charges_sub_category ON sub_category_master(charges_sub_category);

-- Create index on sort_order for ordering
CREATE INDEX IF NOT EXISTS idx_sub_category_master_sort_order ON sub_category_master(sort_order);

-- Add comments
COMMENT ON TABLE sub_category_master IS 'Master table for charges sub-categories';
COMMENT ON COLUMN sub_category_master.id IS 'Primary key, auto-generated';
COMMENT ON COLUMN sub_category_master.charges_sub_category IS 'The charges sub-category name (unique)';
COMMENT ON COLUMN sub_category_master.sort_order IS 'Sort order for displaying sub-categories';

