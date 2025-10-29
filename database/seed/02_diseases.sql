-- =====================================================
-- Common Animal Diseases Seed Data
-- =====================================================
-- Note: affected_animal_types should be filled with actual UUID references
-- This is a sample - adjust based on actual animal_types IDs
-- =====================================================

-- Get animal type IDs for reference (for manual insertion later)
-- SELECT id, type_name FROM animal_types;

-- Sample diseases (you'll need to update affected_animal_types with actual UUIDs)

-- Cattle Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, created_at) VALUES
(gen_random_uuid(), 'Foot and Mouth Disease', 'FMD-001', 'Highly contagious viral disease affecting cloven-hoofed animals', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Bovine Tuberculosis', 'BTB-001', 'Chronic bacterial disease affecting cattle', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Brucellosis', 'BRU-001', 'Bacterial infection causing abortion in cattle', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Anthrax', 'ANT-001', 'Acute bacterial disease affecting multiple species', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Black Quarter', 'BQ-001', 'Acute bacterial disease in cattle and buffalo', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Haemorrhagic Septicaemia', 'HS-001', 'Acute septicaemic disease in cattle and buffalo', 'HIGH', TRUE, TRUE, NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Buffalo Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, created_at) VALUES
(gen_random_uuid(), 'Johnes Disease', 'JD-001', 'Chronic intestinal disease in ruminants', 'MEDIUM', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Mastitis', 'MAS-001', 'Inflammation of mammary gland', 'MEDIUM', FALSE, TRUE, NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Goat and Sheep Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, created_at) VALUES
(gen_random_uuid(), 'Peste des Petits Ruminants', 'PPR-001', 'Viral disease affecting small ruminants', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Contagious Caprine Pleuropneumonia', 'CCPP-001', 'Respiratory disease in goats', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Enterotoxaemia', 'ENT-001', 'Acute toxemia in ruminants', 'HIGH', FALSE, TRUE, NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Pig Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, created_at) VALUES
(gen_random_uuid(), 'African Swine Fever', 'ASF-001', 'Highly contagious viral disease in pigs', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Classical Swine Fever', 'CSF-001', 'Contagious viral disease in pigs', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Porcine Reproductive Respiratory Syndrome', 'PRRS-001', 'Viral disease affecting reproduction', 'HIGH', FALSE, TRUE, NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Poultry Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, created_at) VALUES
(gen_random_uuid(), 'Avian Influenza', 'AI-001', 'Highly contagious viral disease in birds', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Newcastle Disease', 'ND-001', 'Contagious viral disease in poultry', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Infectious Bursal Disease', 'IBD-001', 'Viral disease affecting young chickens', 'HIGH', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Marek Disease', 'MD-001', 'Viral disease causing tumors in chickens', 'MEDIUM', FALSE, TRUE, NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Multi-species Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, created_at) VALUES
(gen_random_uuid(), 'Rabies', 'RAB-001', 'Fatal viral disease affecting all mammals', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Tetanus', 'TET-001', 'Bacterial toxin disease affecting all species', 'HIGH', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Internal Parasites', 'PAR-001', 'Various parasitic infections', 'LOW', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'External Parasites', 'PAR-002', 'Tick, lice, and mite infestations', 'LOW', FALSE, TRUE, NOW())
ON CONFLICT (disease_name) DO NOTHING;
