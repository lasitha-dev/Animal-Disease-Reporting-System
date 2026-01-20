-- Migration: Add created_by_vet column to diseases table
-- Description: Tracks whether a disease was created by a vet (via "Other" option) or by admin
-- This allows vets to add new diseases to the system when reporting

-- Add created_by_vet column to diseases table
ALTER TABLE diseases 
ADD COLUMN IF NOT EXISTS created_by_vet BOOLEAN NOT NULL DEFAULT FALSE;

-- Add index for filtering by creation source
CREATE INDEX IF NOT EXISTS idx_diseases_created_by_vet ON diseases(created_by_vet);

-- Add comment for documentation
COMMENT ON COLUMN diseases.created_by_vet IS 'True if disease was created by a vet via the "Other" option during disease reporting';
