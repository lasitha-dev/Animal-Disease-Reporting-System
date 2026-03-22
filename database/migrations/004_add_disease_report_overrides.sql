-- Migration: Add disease report override columns
-- Description: Allows vets to override disease name, severity, description, and notifiable status per report
-- without affecting the admin-managed disease data

-- Add override columns to disease_reports table
ALTER TABLE disease_reports 
ADD COLUMN IF NOT EXISTS override_disease_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS override_severity VARCHAR(20),
ADD COLUMN IF NOT EXISTS override_description TEXT,
ADD COLUMN IF NOT EXISTS override_notifiable BOOLEAN;

-- Add comments for documentation
COMMENT ON COLUMN disease_reports.override_disease_name IS 'Vet-specified disease name override for this report only';
COMMENT ON COLUMN disease_reports.override_severity IS 'Vet-specified severity override (LOW, MEDIUM, HIGH, CRITICAL) for this report only';
COMMENT ON COLUMN disease_reports.override_description IS 'Vet-specified description override for this report only';
COMMENT ON COLUMN disease_reports.override_notifiable IS 'Vet-specified notifiable status override for this report only';
