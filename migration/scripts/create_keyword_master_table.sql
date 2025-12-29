-- =====================================================
-- Create keyword_master table
-- =====================================================
-- This table stores keyword/operation master data
-- =====================================================

CREATE TABLE IF NOT EXISTS keyword_master (
    id BIGSERIAL PRIMARY KEY,
    keyword_value VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT true
);

-- Create index on keyword_value for faster lookups
CREATE INDEX IF NOT EXISTS idx_keyword_master_keyword_value ON keyword_master(keyword_value);

-- Create index on is_active for filtering active keywords
CREATE INDEX IF NOT EXISTS idx_keyword_master_is_active ON keyword_master(is_active);

-- Add comments
COMMENT ON TABLE keyword_master IS 'Master table for keywords/operations';
COMMENT ON COLUMN keyword_master.id IS 'Primary key, auto-generated';
COMMENT ON COLUMN keyword_master.keyword_value IS 'The keyword/operation value (unique)';
COMMENT ON COLUMN keyword_master.description IS 'Description of the keyword/operation';
COMMENT ON COLUMN keyword_master.is_active IS 'Whether the keyword is active';

