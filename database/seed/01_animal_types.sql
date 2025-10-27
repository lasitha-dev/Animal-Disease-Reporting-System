-- =====================================================
-- Animal Types Seed Data
-- =====================================================

INSERT INTO animal_types (type_name, description, is_active) VALUES
('Cattle', 'Domestic bovine animals including cows, bulls, and calves', TRUE),
('Buffalo', 'Water buffalo used for dairy and agriculture', TRUE),
('Goat', 'Domestic goats for meat and milk production', TRUE),
('Sheep', 'Domestic sheep for wool and meat', TRUE),
('Pig', 'Domestic swine for meat production', TRUE),
('Poultry', 'Domestic fowl including chickens, ducks, and turkeys', TRUE),
('Horse', 'Equine animals used for transportation and agriculture', TRUE),
('Other', 'Other livestock animals not categorized above', TRUE)
ON CONFLICT (type_name) DO NOTHING;
