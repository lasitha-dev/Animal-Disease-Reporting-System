-- =====================================================
-- Migration: Add animal_type_id to diseases table
-- Description: Links each disease to a specific animal type
-- Date: 2025-11-27
-- =====================================================

-- Add the animal_type_id column to diseases table
ALTER TABLE diseases 
ADD COLUMN IF NOT EXISTS animal_type_id UUID;

-- Add foreign key constraint
ALTER TABLE diseases 
ADD CONSTRAINT fk_disease_animal_type 
FOREIGN KEY (animal_type_id) 
REFERENCES animal_types(id)
ON DELETE SET NULL;

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_diseases_animal_type_id 
ON diseases(animal_type_id);

-- =====================================================
-- Update existing diseases with appropriate animal types
-- =====================================================

-- Update Cattle diseases
UPDATE diseases d
SET animal_type_id = (SELECT id FROM animal_types WHERE type_name = 'Cattle' LIMIT 1)
WHERE d.disease_name IN (
    'Foot and Mouth Disease',
    'Bovine Tuberculosis',
    'Brucellosis',
    'Anthrax',
    'Black Quarter',
    'Haemorrhagic Septicaemia',
    'Johnes Disease',
    'Mastitis'
);

-- Update Buffalo diseases (same as cattle for most diseases)
UPDATE diseases d
SET animal_type_id = (SELECT id FROM animal_types WHERE type_name = 'Buffalo' LIMIT 1)
WHERE d.disease_name IN (
    'Black Quarter',
    'Haemorrhagic Septicaemia'
)
AND animal_type_id IS NULL;

-- Update Goat diseases
UPDATE diseases d
SET animal_type_id = (SELECT id FROM animal_types WHERE type_name = 'Goat' LIMIT 1)
WHERE d.disease_name IN (
    'Peste des Petits Ruminants',
    'Contagious Caprine Pleuropneumonia',
    'Enterotoxaemia'
);

-- Update Sheep diseases
UPDATE diseases d
SET animal_type_id = (SELECT id FROM animal_types WHERE type_name = 'Sheep' LIMIT 1)
WHERE d.disease_name = 'Enterotoxaemia'
AND animal_type_id IS NULL;

-- Update Pig diseases
UPDATE diseases d
SET animal_type_id = (SELECT id FROM animal_types WHERE type_name = 'Pig' LIMIT 1)
WHERE d.disease_name IN (
    'African Swine Fever',
    'Classical Swine Fever',
    'Porcine Reproductive Respiratory Syndrome'
);

-- Update Poultry diseases
UPDATE diseases d
SET animal_type_id = (SELECT id FROM animal_types WHERE type_name = 'Poultry' LIMIT 1)
WHERE d.disease_name IN (
    'Avian Influenza',
    'Newcastle Disease',
    'Infectious Bursal Disease',
    'Marek Disease'
);

-- =====================================================
-- Verification Query (run manually to check results)
-- =====================================================
-- SELECT d.disease_name, d.disease_code, at.type_name as animal_type
-- FROM diseases d
-- LEFT JOIN animal_types at ON d.animal_type_id = at.id
-- ORDER BY at.type_name, d.disease_name;
