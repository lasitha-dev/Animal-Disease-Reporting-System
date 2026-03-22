-- =============================================================================
-- Migration: 007_add_performance_indexes.sql
-- Purpose: Add database indexes to improve query performance for Supabase cloud
-- Date: 2026-02-04
-- =============================================================================

-- Disease Reports table indexes (most queried table)
CREATE INDEX IF NOT EXISTS idx_disease_reports_farm_id 
    ON disease_reports(farm_id);

CREATE INDEX IF NOT EXISTS idx_disease_reports_report_date 
    ON disease_reports(report_date);

CREATE INDEX IF NOT EXISTS idx_disease_reports_outcome 
    ON disease_reports(outcome);

CREATE INDEX IF NOT EXISTS idx_disease_reports_created_at 
    ON disease_reports(created_at);

CREATE INDEX IF NOT EXISTS idx_disease_reports_animal_type_id 
    ON disease_reports(animal_type_id);

CREATE INDEX IF NOT EXISTS idx_disease_reports_disease_id 
    ON disease_reports(disease_id);

CREATE INDEX IF NOT EXISTS idx_disease_reports_reported_by 
    ON disease_reports(reported_by);

-- Composite index for common analytics queries (date range + outcome)
CREATE INDEX IF NOT EXISTS idx_disease_reports_date_outcome 
    ON disease_reports(report_date, outcome);


-- Farms table indexes
CREATE INDEX IF NOT EXISTS idx_farms_created_by 
    ON farms(created_by);

CREATE INDEX IF NOT EXISTS idx_farms_is_active 
    ON farms(is_active);

CREATE INDEX IF NOT EXISTS idx_farms_province 
    ON farms(province);

-- Partial index for farms with GPS coordinates (used in map queries)
CREATE INDEX IF NOT EXISTS idx_farms_gps_coords 
    ON farms(gps_latitude, gps_longitude) 
    WHERE gps_latitude IS NOT NULL AND gps_longitude IS NOT NULL;

-- Composite index for common farm listing queries
CREATE INDEX IF NOT EXISTS idx_farms_active_created 
    ON farms(is_active, created_by);


-- Farm Animals table index (for joining with farms)
CREATE INDEX IF NOT EXISTS idx_farm_animals_farm_id 
    ON farm_animals(farm_id);

CREATE INDEX IF NOT EXISTS idx_farm_animals_animal_type_id 
    ON farm_animals(animal_type_id);


-- =============================================================================
-- Verification: Run this query to confirm indexes were created
-- SELECT indexname, tablename FROM pg_indexes WHERE schemaname = 'public' ORDER BY tablename;
-- =============================================================================
