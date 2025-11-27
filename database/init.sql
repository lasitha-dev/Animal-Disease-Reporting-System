-- =====================================================
-- Animal Disease Reporting System - Database Initialization
-- =====================================================
-- Database: PostgreSQL
-- Version: 1.0.0
-- =====================================================

-- Create database (run this separately if needed)
-- CREATE DATABASE adrsdb;

-- Use the database
-- \c adrsdb;

-- Enable UUID extension (PostgreSQL-specific command)
-- Note: VS Code SQL linter may show errors - this is valid PostgreSQL syntax
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- USERS AND ROLES TABLE
-- =====================================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'FIELD_VET', 'DISTRICT_VET')),
    district VARCHAR(50),
    province VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

-- =====================================================
-- FARM MANAGEMENT TABLES
-- =====================================================

-- Farm Types table (reference/lookup table)
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

-- Farms table
CREATE TABLE IF NOT EXISTS farms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    farm_name VARCHAR(100) NOT NULL,
    farm_type_id UUID NOT NULL REFERENCES farm_types(id),
    owner_name VARCHAR(100) NOT NULL,
    owner_contact VARCHAR(20),
    address TEXT NOT NULL,
    district VARCHAR(50) NOT NULL,
    province VARCHAR(50) NOT NULL,
    gps_latitude DECIMAL(10, 8),
    gps_longitude DECIMAL(11, 8),
    total_animals INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- =====================================================
-- ANIMAL MANAGEMENT TABLES
-- =====================================================

-- Animal Types table (reference/lookup table)
CREATE TABLE IF NOT EXISTS animal_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Animals table
CREATE TABLE IF NOT EXISTS animals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    farm_id UUID NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
    animal_type_id UUID NOT NULL REFERENCES animal_types(id),
    tag_number VARCHAR(50) UNIQUE,
    breed VARCHAR(50),
    age_years INTEGER,
    age_months INTEGER,
    sex VARCHAR(10) CHECK (sex IN ('MALE', 'FEMALE', 'UNKNOWN')),
    health_status VARCHAR(20) DEFAULT 'HEALTHY',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- =====================================================
-- DISEASE MANAGEMENT TABLES
-- =====================================================

-- Diseases table (master list)
CREATE TABLE IF NOT EXISTS diseases (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    disease_name VARCHAR(100) UNIQUE NOT NULL,
    disease_code VARCHAR(20) UNIQUE,
    description TEXT,
    affected_animal_types TEXT[], -- Array of animal type IDs
    severity VARCHAR(20) CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    is_notifiable BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- Disease Reports table
CREATE TABLE IF NOT EXISTS disease_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    disease_id UUID NOT NULL REFERENCES diseases(id),
    farm_id UUID NOT NULL REFERENCES farms(id),
    reported_by UUID NOT NULL REFERENCES users(id),
    report_date DATE NOT NULL DEFAULT CURRENT_DATE,
    symptoms TEXT,
    diagnosis TEXT,
    treatment TEXT,
    outcome VARCHAR(20) CHECK (outcome IN ('RECOVERED', 'DIED', 'ONGOING', 'EUTHANIZED')),
    notes TEXT,
    is_confirmed BOOLEAN DEFAULT FALSE,
    confirmed_by UUID REFERENCES users(id),
    confirmed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Users indexes
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_district ON users(district);
CREATE INDEX IF NOT EXISTS idx_users_province ON users(province);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);

-- Farms indexes
CREATE INDEX IF NOT EXISTS idx_farms_district ON farms(district);
CREATE INDEX IF NOT EXISTS idx_farms_province ON farms(province);
CREATE INDEX IF NOT EXISTS idx_farms_farm_type_id ON farms(farm_type_id);
CREATE INDEX IF NOT EXISTS idx_farms_is_active ON farms(is_active);
CREATE INDEX IF NOT EXISTS idx_farms_gps ON farms(gps_latitude, gps_longitude);

-- Farm Types indexes
CREATE INDEX IF NOT EXISTS idx_farm_types_is_active ON farm_types(is_active);
CREATE INDEX IF NOT EXISTS idx_farm_types_type_name ON farm_types(type_name);

-- Animals indexes
CREATE INDEX IF NOT EXISTS idx_animals_farm_id ON animals(farm_id);
CREATE INDEX IF NOT EXISTS idx_animals_animal_type_id ON animals(animal_type_id);
CREATE INDEX IF NOT EXISTS idx_animals_is_active ON animals(is_active);

-- Diseases indexes
CREATE INDEX IF NOT EXISTS idx_diseases_is_active ON diseases(is_active);
CREATE INDEX IF NOT EXISTS idx_diseases_severity ON diseases(severity);

-- Disease Reports indexes
CREATE INDEX IF NOT EXISTS idx_disease_reports_animal_id ON disease_reports(animal_id);
CREATE INDEX IF NOT EXISTS idx_disease_reports_disease_id ON disease_reports(disease_id);
CREATE INDEX IF NOT EXISTS idx_disease_reports_farm_id ON disease_reports(farm_id);
CREATE INDEX IF NOT EXISTS idx_disease_reports_reported_by ON disease_reports(reported_by);
CREATE INDEX IF NOT EXISTS idx_disease_reports_report_date ON disease_reports(report_date);
CREATE INDEX IF NOT EXISTS idx_disease_reports_is_confirmed ON disease_reports(is_confirmed);

-- =====================================================
-- AUDIT TRIGGERS
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to all tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_farm_types_updated_at BEFORE UPDATE ON farm_types
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_farms_updated_at BEFORE UPDATE ON farms
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_animal_types_updated_at BEFORE UPDATE ON animal_types
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_animals_updated_at BEFORE UPDATE ON animals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_diseases_updated_at BEFORE UPDATE ON diseases
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_disease_reports_updated_at BEFORE UPDATE ON disease_reports
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- INITIAL DATA SETUP
-- =====================================================

-- Note: Initial data will be loaded from seed files
-- See database/seed/ directory for data insertion scripts

COMMIT;
