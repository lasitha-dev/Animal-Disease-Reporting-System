-- =====================================================
-- Migration: Change diseases to support multiple animal types
-- Description: Converts from single animal_type_id to a many-to-many
--              relationship via a join table (disease_animal_types)
-- Date: 2026-01-22
-- =====================================================

-- Step 1: Create the join table for disease-animal type many-to-many relationship
CREATE TABLE IF NOT EXISTS disease_animal_types (
    disease_id UUID NOT NULL REFERENCES diseases(id) ON DELETE CASCADE,
    animal_type_id UUID NOT NULL REFERENCES animal_types(id) ON DELETE CASCADE,
    PRIMARY KEY (disease_id, animal_type_id)
);

-- Step 2: Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_disease_animal_types_disease_id 
ON disease_animal_types(disease_id);

CREATE INDEX IF NOT EXISTS idx_disease_animal_types_animal_type_id 
ON disease_animal_types(animal_type_id);

-- Step 3: Migrate existing data from animal_type_id column to the join table
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT id, animal_type_id 
FROM diseases 
WHERE animal_type_id IS NOT NULL
ON CONFLICT DO NOTHING;

-- Step 4: Drop the old foreign key constraint
ALTER TABLE diseases 
DROP CONSTRAINT IF EXISTS fk_disease_animal_type;

-- Step 5: Drop the old animal_type_id column (after data migration)
-- Note: Uncomment this after verifying migration is successful
-- ALTER TABLE diseases DROP COLUMN IF EXISTS animal_type_id;

-- =====================================================
-- Rollback Script (if needed)
-- =====================================================
-- DROP TABLE IF EXISTS disease_animal_types;
-- ALTER TABLE diseases ADD COLUMN IF NOT EXISTS animal_type_id UUID;
-- ALTER TABLE diseases ADD CONSTRAINT fk_disease_animal_type 
--     FOREIGN KEY (animal_type_id) REFERENCES animal_types(id) ON DELETE SET NULL;
