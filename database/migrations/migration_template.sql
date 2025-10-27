-- =====================================================
-- Database Migration Script Template
-- =====================================================
-- Migration: [MIGRATION_NAME]
-- Date: [DATE]
-- Description: [DESCRIPTION]
-- =====================================================

-- Start transaction
BEGIN;

-- =====================================================
-- Migration SQL statements go here
-- =====================================================

-- Example: Add new column
-- ALTER TABLE table_name ADD COLUMN column_name datatype;

-- Example: Create new table
-- CREATE TABLE IF NOT EXISTS new_table (
--     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--     name VARCHAR(100) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- Example: Create index
-- CREATE INDEX IF NOT EXISTS idx_table_column ON table_name(column_name);

-- =====================================================
-- Rollback SQL (for reference - run manually if needed)
-- =====================================================

-- Example: Drop column
-- ALTER TABLE table_name DROP COLUMN column_name;

-- Example: Drop table
-- DROP TABLE IF EXISTS new_table;

-- Example: Drop index
-- DROP INDEX IF EXISTS idx_table_column;

-- Commit transaction
COMMIT;
