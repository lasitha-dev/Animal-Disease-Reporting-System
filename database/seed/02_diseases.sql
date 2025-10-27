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
INSERT INTO diseases (disease_name, disease_code, description, severity, is_notifiable, is_active) VALUES
('Foot and Mouth Disease', 'FMD-001', 'Highly contagious viral disease affecting cloven-hoofed animals', 'CRITICAL', TRUE, TRUE),
('Bovine Tuberculosis', 'BTB-001', 'Chronic bacterial disease affecting cattle', 'HIGH', TRUE, TRUE),
('Brucellosis', 'BRU-001', 'Bacterial infection causing abortion in cattle', 'HIGH', TRUE, TRUE),
('Anthrax', 'ANT-001', 'Acute bacterial disease affecting multiple species', 'CRITICAL', TRUE, TRUE),
('Black Quarter', 'BQ-001', 'Acute bacterial disease in cattle and buffalo', 'HIGH', TRUE, TRUE),
('Haemorrhagic Septicaemia', 'HS-001', 'Acute septicaemic disease in cattle and buffalo', 'HIGH', TRUE, TRUE)
ON CONFLICT (disease_name) DO NOTHING;

-- Buffalo Diseases
INSERT INTO diseases (disease_name, disease_code, description, severity, is_notifiable, is_active) VALUES
('Johnes Disease', 'JD-001', 'Chronic intestinal disease in ruminants', 'MEDIUM', FALSE, TRUE),
('Mastitis', 'MAS-001', 'Inflammation of mammary gland', 'MEDIUM', FALSE, TRUE)
ON CONFLICT (disease_name) DO NOTHING;

-- Goat and Sheep Diseases
INSERT INTO diseases (disease_name, disease_code, description, severity, is_notifiable, is_active) VALUES
('Peste des Petits Ruminants', 'PPR-001', 'Viral disease affecting small ruminants', 'CRITICAL', TRUE, TRUE),
('Contagious Caprine Pleuropneumonia', 'CCPP-001', 'Respiratory disease in goats', 'HIGH', TRUE, TRUE),
('Enterotoxaemia', 'ENT-001', 'Acute toxemia in ruminants', 'HIGH', FALSE, TRUE)
ON CONFLICT (disease_name) DO NOTHING;

-- Pig Diseases
INSERT INTO diseases (disease_name, disease_code, description, severity, is_notifiable, is_active) VALUES
('African Swine Fever', 'ASF-001', 'Highly contagious viral disease in pigs', 'CRITICAL', TRUE, TRUE),
('Classical Swine Fever', 'CSF-001', 'Contagious viral disease in pigs', 'CRITICAL', TRUE, TRUE),
('Porcine Reproductive Respiratory Syndrome', 'PRRS-001', 'Viral disease affecting reproduction', 'HIGH', FALSE, TRUE)
ON CONFLICT (disease_name) DO NOTHING;

-- Poultry Diseases
INSERT INTO diseases (disease_name, disease_code, description, severity, is_notifiable, is_active) VALUES
('Avian Influenza', 'AI-001', 'Highly contagious viral disease in birds', 'CRITICAL', TRUE, TRUE),
('Newcastle Disease', 'ND-001', 'Contagious viral disease in poultry', 'CRITICAL', TRUE, TRUE),
('Infectious Bursal Disease', 'IBD-001', 'Viral disease affecting young chickens', 'HIGH', FALSE, TRUE),
('Marek Disease', 'MD-001', 'Viral disease causing tumors in chickens', 'MEDIUM', FALSE, TRUE)
ON CONFLICT (disease_name) DO NOTHING;

-- Multi-species Diseases
INSERT INTO diseases (disease_name, disease_code, description, severity, is_notifiable, is_active) VALUES
('Rabies', 'RAB-001', 'Fatal viral disease affecting all mammals', 'CRITICAL', TRUE, TRUE),
('Tetanus', 'TET-001', 'Bacterial toxin disease affecting all species', 'HIGH', FALSE, TRUE),
('Internal Parasites', 'PAR-001', 'Various parasitic infections', 'LOW', FALSE, TRUE),
('External Parasites', 'PAR-002', 'Tick, lice, and mite infestations', 'LOW', FALSE, TRUE)
ON CONFLICT (disease_name) DO NOTHING;
