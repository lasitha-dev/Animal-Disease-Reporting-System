-- =====================================================
-- Farm Types Seed Data
-- =====================================================
-- Description: Common farm types for agricultural operations
-- Note: Run this after animal_types seed (01_animal_types.sql)
-- =====================================================

-- Insert common farm types
INSERT INTO farm_types (id, type_name, description, is_active, created_at) VALUES
(gen_random_uuid(), 'Dairy', 'Farms primarily focused on milk production from dairy cattle or buffalo', TRUE, NOW()),
(gen_random_uuid(), 'Beef', 'Farms focused on raising cattle for meat production', TRUE, NOW()),
(gen_random_uuid(), 'Poultry', 'Farms raising chickens, ducks, turkeys, or other birds for eggs or meat', TRUE, NOW()),
(gen_random_uuid(), 'Mixed', 'Farms with multiple types of livestock (cattle, poultry, goats, etc.)', TRUE, NOW()),
(gen_random_uuid(), 'Sheep', 'Farms specializing in sheep for wool, meat, or milk production', TRUE, NOW()),
(gen_random_uuid(), 'Goat', 'Farms raising goats for milk, meat, or fiber production', TRUE, NOW()),
(gen_random_uuid(), 'Pig', 'Farms focused on pig/swine production for meat', TRUE, NOW()),
(gen_random_uuid(), 'Aquaculture', 'Fish and aquatic animal farming operations', TRUE, NOW()),
(gen_random_uuid(), 'Horse', 'Farms for breeding, training, or maintaining horses', TRUE, NOW()),
(gen_random_uuid(), 'Rabbit', 'Farms raising rabbits for meat or fur', TRUE, NOW()),
(gen_random_uuid(), 'Apiary', 'Bee farms for honey and pollination services', TRUE, NOW()),
(gen_random_uuid(), 'Other', 'Other types of livestock or specialty farming operations', TRUE, NOW())
ON CONFLICT (type_name) DO NOTHING;

-- =====================================================
-- Verification Query
-- =====================================================
-- SELECT * FROM farm_types ORDER BY type_name;
