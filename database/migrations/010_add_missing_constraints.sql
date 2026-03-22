-- =====================================================
-- Migration 010: Add Missing Constraints and Tables
-- =====================================================
-- This migration adds:
-- 1. Unique constraint on farms.farm_name
-- 2. Case-insensitive unique indexes on users.username and users.email
-- 3. farm_animals table (previously only created by JPA auto-DDL)
-- 4. Missing columns on disease_reports (animal_type_id, affected_count, image_path, overrides)
-- =====================================================

-- 1. Add unique constraint on farm_name
-- Prevents duplicate farm names at the database level
ALTER TABLE farms ADD CONSTRAINT uk_farms_farm_name UNIQUE (farm_name);

-- 2. Add case-insensitive unique indexes for users
-- Ensures 'Admin' and 'admin' are treated as duplicates
-- These functional indexes work alongside the existing UNIQUE constraints
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_username_lower ON users (LOWER(username));
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email_lower ON users (LOWER(email));

-- 3. Create farm_animals table
-- This table was previously only created by JPA/Hibernate auto-DDL
-- Tracks animal types and counts per farm
CREATE TABLE IF NOT EXISTS farm_animals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    farm_id UUID NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
    animal_type_id UUID NOT NULL REFERENCES animal_types(id),
    count INTEGER NOT NULL DEFAULT 0 CHECK (count >= 0),
    CONSTRAINT uk_farm_animals_farm_animal_type UNIQUE (farm_id, animal_type_id)
);

CREATE INDEX IF NOT EXISTS idx_farm_animals_farm_id ON farm_animals(farm_id);
CREATE INDEX IF NOT EXISTS idx_farm_animals_animal_type_id ON farm_animals(animal_type_id);

-- 4. Add missing columns to disease_reports
-- The JPA entity has evolved beyond the original schema

-- Add animal_type_id column (references animal_types instead of individual animals)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'disease_reports' AND column_name = 'animal_type_id'
    ) THEN
        ALTER TABLE disease_reports ADD COLUMN animal_type_id UUID REFERENCES animal_types(id);
    END IF;
END $$;

-- Add affected_count column
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'disease_reports' AND column_name = 'affected_count'
    ) THEN
        ALTER TABLE disease_reports ADD COLUMN affected_count INTEGER DEFAULT 0;
    END IF;
END $$;

-- Add image_path column
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'disease_reports' AND column_name = 'image_path'
    ) THEN
        ALTER TABLE disease_reports ADD COLUMN image_path VARCHAR(500);
    END IF;
END $$;

-- Add override columns for vet-specific disease information
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'disease_reports' AND column_name = 'override_disease_name'
    ) THEN
        ALTER TABLE disease_reports ADD COLUMN override_disease_name VARCHAR(255);
        ALTER TABLE disease_reports ADD COLUMN override_severity VARCHAR(20);
        ALTER TABLE disease_reports ADD COLUMN override_description TEXT;
        ALTER TABLE disease_reports ADD COLUMN override_notifiable BOOLEAN;
    END IF;
END $$;

-- Add index for duplicate report detection
CREATE INDEX IF NOT EXISTS idx_disease_reports_duplicate_check 
    ON disease_reports(disease_id, farm_id, report_date, reported_by);

COMMIT;
