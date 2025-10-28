-- =====================================================
-- Farm Types Seed Data
-- =====================================================
-- Description: Common farm types for agricultural operations
-- Note: Run this after animal_types seed (01_animal_types.sql)
-- =====================================================

-- Insert common farm types
INSERT INTO farm_types (type_name, description, is_active) VALUES
('Dairy', 'Farms primarily focused on milk production from dairy cattle or buffalo', TRUE),
('Beef', 'Farms focused on raising cattle for meat production', TRUE),
('Poultry', 'Farms raising chickens, ducks, turkeys, or other birds for eggs or meat', TRUE),
('Mixed', 'Farms with multiple types of livestock (cattle, poultry, goats, etc.)', TRUE),
('Sheep', 'Farms specializing in sheep for wool, meat, or milk production', TRUE),
('Goat', 'Farms raising goats for milk, meat, or fiber production', TRUE),
('Pig', 'Farms focused on pig/swine production for meat', TRUE),
('Aquaculture', 'Fish and aquatic animal farming operations', TRUE),
('Horse', 'Farms for breeding, training, or maintaining horses', TRUE),
('Rabbit', 'Farms raising rabbits for meat or fur', TRUE),
('Apiary', 'Bee farms for honey and pollination services', TRUE),
('Other', 'Other types of livestock or specialty farming operations', TRUE)
ON CONFLICT (type_name) DO NOTHING;

-- =====================================================
-- Verification Query
-- =====================================================
-- SELECT * FROM farm_types ORDER BY type_name;
