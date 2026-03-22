-- =====================================================
-- Common Animal Diseases Seed Data
-- =====================================================
-- Note: This seed data links diseases to specific animal types.
-- Run 01_animal_types.sql first to ensure animal types exist.
-- =====================================================

-- Cattle Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, animal_type_id, created_at) VALUES
(gen_random_uuid(), 'Foot and Mouth Disease', 'FMD-001', 'Highly contagious viral disease affecting cloven-hoofed animals', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Cattle'), NOW()),
(gen_random_uuid(), 'Bovine Tuberculosis', 'BTB-001', 'Chronic bacterial disease affecting cattle', 'HIGH', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Cattle'), NOW()),
(gen_random_uuid(), 'Brucellosis', 'BRU-001', 'Bacterial infection causing abortion in cattle', 'HIGH', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Cattle'), NOW()),
(gen_random_uuid(), 'Anthrax', 'ANT-001', 'Acute bacterial disease affecting multiple species', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Cattle'), NOW()),
(gen_random_uuid(), 'Black Quarter', 'BQ-001', 'Acute bacterial disease in cattle and buffalo', 'HIGH', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Cattle'), NOW()),
(gen_random_uuid(), 'Haemorrhagic Septicaemia', 'HS-001', 'Acute septicaemic disease in cattle and buffalo', 'HIGH', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Cattle'), NOW()),
(gen_random_uuid(), 'Johnes Disease', 'JD-001', 'Chronic intestinal disease in ruminants', 'MEDIUM', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Cattle'), NOW()),
(gen_random_uuid(), 'Mastitis', 'MAS-001', 'Inflammation of mammary gland', 'MEDIUM', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Cattle'), NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Buffalo Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, animal_type_id, created_at) VALUES
(gen_random_uuid(), 'Buffalo Pox', 'BPX-001', 'Viral skin disease in buffalo', 'MEDIUM', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Buffalo'), NOW()),
(gen_random_uuid(), 'Theileriosis', 'THE-001', 'Tick-borne blood parasite disease', 'HIGH', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Buffalo'), NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Goat Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, animal_type_id, created_at) VALUES
(gen_random_uuid(), 'Peste des Petits Ruminants', 'PPR-001', 'Viral disease affecting small ruminants', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Goat'), NOW()),
(gen_random_uuid(), 'Contagious Caprine Pleuropneumonia', 'CCPP-001', 'Respiratory disease in goats', 'HIGH', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Goat'), NOW()),
(gen_random_uuid(), 'Goat Pox', 'GPX-001', 'Viral skin disease in goats', 'MEDIUM', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Goat'), NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Sheep Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, animal_type_id, created_at) VALUES
(gen_random_uuid(), 'Sheep Pox', 'SPX-001', 'Viral skin disease in sheep', 'MEDIUM', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Sheep'), NOW()),
(gen_random_uuid(), 'Enterotoxaemia', 'ENT-001', 'Acute toxemia in ruminants', 'HIGH', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Sheep'), NOW()),
(gen_random_uuid(), 'Blue Tongue', 'BT-001', 'Viral disease transmitted by midges', 'HIGH', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Sheep'), NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Pig Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, animal_type_id, created_at) VALUES
(gen_random_uuid(), 'African Swine Fever', 'ASF-001', 'Highly contagious viral disease in pigs', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Pig'), NOW()),
(gen_random_uuid(), 'Classical Swine Fever', 'CSF-001', 'Contagious viral disease in pigs', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Pig'), NOW()),
(gen_random_uuid(), 'Porcine Reproductive Respiratory Syndrome', 'PRRS-001', 'Viral disease affecting reproduction', 'HIGH', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Pig'), NOW()),
(gen_random_uuid(), 'Swine Erysipelas', 'SE-001', 'Bacterial disease causing skin lesions', 'MEDIUM', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Pig'), NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Poultry Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, animal_type_id, created_at) VALUES
(gen_random_uuid(), 'Avian Influenza', 'AI-001', 'Highly contagious viral disease in birds', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Poultry'), NOW()),
(gen_random_uuid(), 'Newcastle Disease', 'ND-001', 'Contagious viral disease in poultry', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Poultry'), NOW()),
(gen_random_uuid(), 'Infectious Bursal Disease', 'IBD-001', 'Viral disease affecting young chickens', 'HIGH', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Poultry'), NOW()),
(gen_random_uuid(), 'Marek Disease', 'MD-001', 'Viral disease causing tumors in chickens', 'MEDIUM', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Poultry'), NOW()),
(gen_random_uuid(), 'Fowl Pox', 'FPX-001', 'Viral disease causing skin lesions in poultry', 'LOW', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Poultry'), NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- Horse Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, animal_type_id, created_at) VALUES
(gen_random_uuid(), 'Equine Influenza', 'EI-001', 'Highly contagious respiratory disease in horses', 'HIGH', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Horse'), NOW()),
(gen_random_uuid(), 'Strangles', 'STR-001', 'Bacterial upper respiratory infection in horses', 'MEDIUM', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Horse'), NOW()),
(gen_random_uuid(), 'African Horse Sickness', 'AHS-001', 'Viral disease transmitted by midges', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Horse'), NOW())
ON CONFLICT (disease_name) DO NOTHING;

-- General/Other Animal Type Diseases
INSERT INTO diseases (id, disease_name, disease_code, description, severity, is_notifiable, is_active, animal_type_id, created_at) VALUES
(gen_random_uuid(), 'Rabies', 'RAB-001', 'Fatal viral disease affecting all mammals', 'CRITICAL', TRUE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Other'), NOW()),
(gen_random_uuid(), 'Tetanus', 'TET-001', 'Bacterial toxin disease affecting all species', 'HIGH', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Other'), NOW()),
(gen_random_uuid(), 'Internal Parasites', 'PAR-001', 'Various parasitic infections', 'LOW', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Other'), NOW()),
(gen_random_uuid(), 'External Parasites', 'PAR-002', 'Tick, lice, and mite infestations', 'LOW', FALSE, TRUE, (SELECT id FROM animal_types WHERE type_name = 'Other'), NOW())
ON CONFLICT (disease_name) DO NOTHING;
