-- =============================================================================
-- Migration: 008_add_foreign_key_indexes.sql
-- Purpose: Add indexes for unindexed foreign keys to improve JOIN performance
-- Date: 2026-02-04
-- Issue: Supabase advisor detected 12+ unindexed foreign keys
-- Status: Already applied via Supabase MCP
-- =============================================================================

-- Animals table foreign keys
CREATE INDEX IF NOT EXISTS idx_animals_animal_type_id ON animals(animal_type_id);
CREATE INDEX IF NOT EXISTS idx_animals_farm_id ON animals(farm_id);
CREATE INDEX IF NOT EXISTS idx_animals_created_by ON animals(created_by);
CREATE INDEX IF NOT EXISTS idx_animals_updated_by ON animals(updated_by);

-- Farms table - farm_type_id and updated_by
CREATE INDEX IF NOT EXISTS idx_farms_farm_type_id ON farms(farm_type_id);
CREATE INDEX IF NOT EXISTS idx_farms_updated_by ON farms(updated_by);

-- Diseases table
CREATE INDEX IF NOT EXISTS idx_diseases_created_by ON diseases(created_by);
CREATE INDEX IF NOT EXISTS idx_diseases_updated_by ON diseases(updated_by);

-- Disease reports - confirmed_by
CREATE INDEX IF NOT EXISTS idx_disease_reports_confirmed_by ON disease_reports(confirmed_by);

-- Farm types configuration table
CREATE INDEX IF NOT EXISTS idx_farm_types_created_by ON farm_types(created_by);
CREATE INDEX IF NOT EXISTS idx_farm_types_updated_by ON farm_types(updated_by);

-- Disease animal types junction table
CREATE INDEX IF NOT EXISTS idx_disease_animal_types_disease_id ON disease_animal_types(disease_id);
CREATE INDEX IF NOT EXISTS idx_disease_animal_types_animal_type_id ON disease_animal_types(animal_type_id);


-- Update query planner statistics
ANALYZE animals;
ANALYZE farms;
ANALYZE diseases;
ANALYZE disease_reports;
ANALYZE farm_animals;
ANALYZE farm_types;
ANALYZE animal_types;
ANALYZE disease_animal_types;
ANALYZE users;
