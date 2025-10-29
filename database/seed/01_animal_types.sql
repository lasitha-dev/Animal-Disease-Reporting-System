-- =====================================================
-- Animal Types Seed Data
-- =====================================================

INSERT INTO animal_types (id, type_name, description, is_active, created_at) VALUES
(gen_random_uuid(), 'Cattle', 'Domestic bovine animals including cows, bulls, and calves', TRUE, NOW()),
(gen_random_uuid(), 'Buffalo', 'Water buffalo used for dairy and agriculture', TRUE, NOW()),
(gen_random_uuid(), 'Goat', 'Domestic goats for meat and milk production', TRUE, NOW()),
(gen_random_uuid(), 'Sheep', 'Domestic sheep for wool and meat', TRUE, NOW()),
(gen_random_uuid(), 'Pig', 'Domestic swine for meat production', TRUE, NOW()),
(gen_random_uuid(), 'Poultry', 'Domestic fowl including chickens, ducks, and turkeys', TRUE, NOW()),
(gen_random_uuid(), 'Horse', 'Equine animals used for transportation and agriculture', TRUE, NOW()),
(gen_random_uuid(), 'Other', 'Other livestock animals not categorized above', TRUE, NOW())
ON CONFLICT (type_name) DO NOTHING;
