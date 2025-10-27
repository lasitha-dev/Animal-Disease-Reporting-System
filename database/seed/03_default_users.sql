-- =====================================================
-- Default Admin User Seed Data
-- =====================================================
-- Default password: Admin@123 (hashed with BCrypt)
-- NOTE: Change this password immediately after first login
-- =====================================================

-- BCrypt hash for "Admin@123"
-- Use this for default admin password: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG

INSERT INTO users (
    username, 
    password, 
    email, 
    first_name, 
    last_name, 
    phone_number, 
    role, 
    district, 
    province, 
    is_active
) VALUES (
    'admin',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    'admin@adrs.gov.lk',
    'System',
    'Administrator',
    '+94112345678',
    'ADMIN',
    'Colombo',
    'Western',
    TRUE
)
ON CONFLICT (username) DO NOTHING;

-- Sample Field Veterinary Officer
INSERT INTO users (
    username, 
    password, 
    email, 
    first_name, 
    last_name, 
    phone_number, 
    role, 
    district, 
    province, 
    is_active
) VALUES (
    'field_vet_01',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    'field.vet@adrs.gov.lk',
    'Field',
    'Veterinarian',
    '+94771234567',
    'FIELD_VET',
    'Kandy',
    'Central',
    TRUE
)
ON CONFLICT (username) DO NOTHING;

-- Sample District Veterinary Officer
INSERT INTO users (
    username, 
    password, 
    email, 
    first_name, 
    last_name, 
    phone_number, 
    role, 
    district, 
    province, 
    is_active
) VALUES (
    'district_vet_01',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    'district.vet@adrs.gov.lk',
    'District',
    'Veterinarian',
    '+94762345678',
    'DISTRICT_VET',
    'Colombo',
    'Western',
    TRUE
)
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- IMPORTANT SECURITY NOTE:
-- =====================================================
-- All users have the default password: Admin@123
-- Users MUST change their passwords on first login
-- This is for development/testing only
-- DO NOT use these credentials in production
-- =====================================================
