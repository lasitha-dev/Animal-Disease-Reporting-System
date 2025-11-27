-- =====================================================
-- Migration: Add Farm Types Lookup Table
-- Version: 001
-- Date: 2025-10-28
-- Description: Creates farm_types lookup table and migrates
--              existing farm_type VARCHAR data to use FK reference
-- =====================================================

BEGIN;

-- =====================================================
-- STEP 1: Create farm_types lookup table
-- =====================================================

CREATE TABLE IF NOT EXISTS farm_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_farm_types_is_active ON farm_types(is_active);
CREATE INDEX IF NOT EXISTS idx_farm_types_type_name ON farm_types(type_name);

-- Add trigger for updated_at
CREATE TRIGGER update_farm_types_updated_at BEFORE UPDATE ON farm_types
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- STEP 2: Populate farm_types with existing data
-- =====================================================

-- Extract unique farm types from existing farms table and insert into farm_types
INSERT INTO farm_types (type_name, description, is_active)
SELECT DISTINCT 
    farm_type,
    'Migrated from existing data',
    TRUE
FROM farms
WHERE farm_type IS NOT NULL AND farm_type != ''
ON CONFLICT (type_name) DO NOTHING;

-- =====================================================
-- STEP 3: Add farm_type_id column to farms table
-- =====================================================

-- Add new column (nullable initially for migration)
ALTER TABLE farms ADD COLUMN IF NOT EXISTS farm_type_id UUID;

-- Add foreign key constraint
ALTER TABLE farms ADD CONSTRAINT fk_farms_farm_type_id 
    FOREIGN KEY (farm_type_id) REFERENCES farm_types(id) ON DELETE SET NULL;

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_farms_farm_type_id ON farms(farm_type_id);

-- =====================================================
-- STEP 4: Migrate existing data to use farm_type_id
-- =====================================================

-- Update farms to reference farm_types by ID
UPDATE farms f
SET farm_type_id = ft.id
FROM farm_types ft
WHERE f.farm_type = ft.type_name;

-- =====================================================
-- STEP 5: Make farm_type_id NOT NULL and drop old column
-- =====================================================

-- Make farm_type_id required
ALTER TABLE farms ALTER COLUMN farm_type_id SET NOT NULL;

-- Drop the old VARCHAR column
ALTER TABLE farms DROP COLUMN IF EXISTS farm_type;

-- Remove old index if it exists
DROP INDEX IF EXISTS idx_farms_farm_type;

COMMIT;

-- =====================================================
-- ROLLBACK SCRIPT (run separately if needed)
-- =====================================================

/*
BEGIN;

-- Recreate farm_type VARCHAR column
ALTER TABLE farms ADD COLUMN farm_type VARCHAR(50);

-- Populate from farm_types
UPDATE farms f
SET farm_type = ft.type_name
FROM farm_types ft
WHERE f.farm_type_id = ft.id;

-- Make it NOT NULL
ALTER TABLE farms ALTER COLUMN farm_type SET NOT NULL;

-- Drop the foreign key and column
ALTER TABLE farms DROP CONSTRAINT IF EXISTS fk_farms_farm_type_id;
ALTER TABLE farms DROP COLUMN IF EXISTS farm_type_id;

-- Drop the farm_types table
DROP TABLE IF EXISTS farm_types CASCADE;

COMMIT;
*/
