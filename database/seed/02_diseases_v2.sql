-- =====================================================
-- Common Animal Diseases Seed Data (Updated for multiple animal types)
-- =====================================================
-- Note: This seed data links diseases to animal types via the 
-- disease_animal_types junction table (many-to-many relationship).
-- Run 01_animal_types.sql first to ensure animal types exist.
-- =====================================================

-- First, insert the diseases WITHOUT animal_type_id
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, created_at) VALUES
-- Cattle Diseases
(gen_random_uuid(), 'Foot and Mouth Disease', 'FMD-001', 'Highly contagious viral disease affecting cloven-hoofed animals', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Bovine Tuberculosis', 'BTB-001', 'Chronic bacterial disease affecting cattle', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Brucellosis', 'BRU-001', 'Bacterial infection causing abortion in cattle', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Anthrax', 'ANT-001', 'Acute bacterial disease affecting multiple species', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Black Quarter', 'BQ-001', 'Acute bacterial disease in cattle and buffalo', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Haemorrhagic Septicaemia', 'HS-001', 'Acute septicaemic disease in cattle and buffalo', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Johnes Disease', 'JD-001', 'Chronic intestinal disease in ruminants', 'MEDIUM', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Mastitis', 'MAS-001', 'Inflammation of mammary gland', 'MEDIUM', FALSE, TRUE, NOW()),
-- Buffalo Diseases
(gen_random_uuid(), 'Buffalo Pox', 'BPX-001', 'Viral skin disease in buffalo', 'MEDIUM', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Theileriosis', 'THE-001', 'Tick-borne blood parasite disease', 'HIGH', FALSE, TRUE, NOW()),
-- Goat Diseases
(gen_random_uuid(), 'Peste des Petits Ruminants', 'PPR-001', 'Viral disease affecting small ruminants', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Contagious Caprine Pleuropneumonia', 'CCPP-001', 'Respiratory disease in goats', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Goat Pox', 'GPX-001', 'Viral skin disease in goats', 'MEDIUM', TRUE, TRUE, NOW()),
-- Sheep Diseases
(gen_random_uuid(), 'Sheep Pox', 'SPX-001', 'Viral skin disease in sheep', 'MEDIUM', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Enterotoxaemia', 'ENT-001', 'Acute toxemia in ruminants', 'HIGH', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Blue Tongue', 'BT-001', 'Viral disease transmitted by midges', 'HIGH', TRUE, TRUE, NOW()),
-- Pig Diseases
(gen_random_uuid(), 'African Swine Fever', 'ASF-001', 'Highly contagious viral disease in pigs', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Classical Swine Fever', 'CSF-001', 'Contagious viral disease in pigs', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Porcine Reproductive Respiratory Syndrome', 'PRRS-001', 'Viral disease affecting reproduction', 'HIGH', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Swine Erysipelas', 'SE-001', 'Bacterial disease causing skin lesions', 'MEDIUM', FALSE, TRUE, NOW()),
-- Poultry Diseases
(gen_random_uuid(), 'Avian Influenza', 'AI-001', 'Highly contagious viral disease in birds', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Newcastle Disease', 'ND-001', 'Contagious viral disease in poultry', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Infectious Bursal Disease', 'IBD-001', 'Viral disease affecting young chickens', 'HIGH', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Marek Disease', 'MD-001', 'Viral disease causing tumors in chickens', 'MEDIUM', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Fowl Pox', 'FPX-001', 'Viral disease causing skin lesions in poultry', 'LOW', FALSE, TRUE, NOW()),
-- Horse Diseases
(gen_random_uuid(), 'Equine Influenza', 'EI-001', 'Highly contagious respiratory disease in horses', 'HIGH', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Strangles', 'STR-001', 'Bacterial upper respiratory infection in horses', 'MEDIUM', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'African Horse Sickness', 'AHS-001', 'Viral disease transmitted by midges', 'CRITICAL', TRUE, TRUE, NOW()),
-- General/Multi-species Diseases
(gen_random_uuid(), 'Rabies', 'RAB-001', 'Fatal viral disease affecting all mammals', 'CRITICAL', TRUE, TRUE, NOW()),
(gen_random_uuid(), 'Tetanus', 'TET-001', 'Bacterial toxin disease affecting all species', 'HIGH', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'Internal Parasites', 'PAR-001', 'Various parasitic infections', 'LOW', FALSE, TRUE, NOW()),
(gen_random_uuid(), 'External Parasites', 'PAR-002', 'Tick, lice, and mite infestations', 'LOW', FALSE, TRUE, NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- =====================================================
-- Link diseases to animal types (many-to-many)
-- =====================================================

-- Cattle diseases
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Foot and Mouth Disease' AND at.type_name = 'Cattle'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Bovine Tuberculosis' AND at.type_name = 'Cattle'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Brucellosis' AND at.type_name = 'Cattle'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Mastitis' AND at.type_name = 'Cattle'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Johnes Disease' AND at.type_name = 'Cattle'
ON CONFLICT DO NOTHING;

-- Diseases affecting BOTH Cattle AND Buffalo
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Anthrax' AND at.type_name IN ('Cattle', 'Buffalo', 'Goat', 'Sheep', 'Horse')
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Black Quarter' AND at.type_name IN ('Cattle', 'Buffalo')
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Haemorrhagic Septicaemia' AND at.type_name IN ('Cattle', 'Buffalo')
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Foot and Mouth Disease' AND at.type_name IN ('Buffalo', 'Goat', 'Sheep', 'Pig')
ON CONFLICT DO NOTHING;

-- Buffalo diseases
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Buffalo Pox' AND at.type_name = 'Buffalo'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Theileriosis' AND at.type_name IN ('Cattle', 'Buffalo')
ON CONFLICT DO NOTHING;

-- Goat diseases
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Peste des Petits Ruminants' AND at.type_name IN ('Goat', 'Sheep')
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Contagious Caprine Pleuropneumonia' AND at.type_name = 'Goat'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Goat Pox' AND at.type_name = 'Goat'
ON CONFLICT DO NOTHING;

-- Sheep diseases
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Sheep Pox' AND at.type_name = 'Sheep'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Enterotoxaemia' AND at.type_name IN ('Sheep', 'Goat', 'Cattle')
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Blue Tongue' AND at.type_name IN ('Sheep', 'Cattle', 'Goat')
ON CONFLICT DO NOTHING;

-- Pig diseases
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'African Swine Fever' AND at.type_name = 'Pig'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Classical Swine Fever' AND at.type_name = 'Pig'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Porcine Reproductive Respiratory Syndrome' AND at.type_name = 'Pig'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Swine Erysipelas' AND at.type_name = 'Pig'
ON CONFLICT DO NOTHING;

-- Poultry diseases
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Avian Influenza' AND at.type_name = 'Poultry'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Newcastle Disease' AND at.type_name = 'Poultry'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Infectious Bursal Disease' AND at.type_name = 'Poultry'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Marek Disease' AND at.type_name = 'Poultry'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Fowl Pox' AND at.type_name = 'Poultry'
ON CONFLICT DO NOTHING;

-- Horse diseases
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Equine Influenza' AND at.type_name = 'Horse'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Strangles' AND at.type_name = 'Horse'
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'African Horse Sickness' AND at.type_name = 'Horse'
ON CONFLICT DO NOTHING;

-- Multi-species diseases (affect ALL mammal types)
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Rabies' AND at.type_name IN ('Cattle', 'Buffalo', 'Goat', 'Sheep', 'Pig', 'Horse', 'Other')
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Tetanus' AND at.type_name IN ('Cattle', 'Buffalo', 'Goat', 'Sheep', 'Pig', 'Horse', 'Other')
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Internal Parasites' AND at.type_name IN ('Cattle', 'Buffalo', 'Goat', 'Sheep', 'Pig', 'Horse', 'Poultry', 'Other')
ON CONFLICT DO NOTHING;

INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'External Parasites' AND at.type_name IN ('Cattle', 'Buffalo', 'Goat', 'Sheep', 'Pig', 'Horse', 'Poultry', 'Other')
ON CONFLICT DO NOTHING;

-- Mastitis also affects Buffalo and Goat
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Mastitis' AND at.type_name IN ('Buffalo', 'Goat')
ON CONFLICT DO NOTHING;

-- Brucellosis affects multiple species
INSERT INTO disease_animal_types (disease_id, animal_type_id)
SELECT d.id, at.id FROM diseases d, animal_types at 
WHERE d.disease_name = 'Brucellosis' AND at.type_name IN ('Buffalo', 'Goat', 'Sheep', 'Pig')
ON CONFLICT DO NOTHING;

