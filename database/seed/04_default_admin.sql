-- Seed script for creating default admin user
-- This script creates an initial admin user for the system
-- Default credentials: admin / admin123
-- IMPORTANT: Change the password immediately after first login in production

-- Create default admin user
-- Password is 'admin123' hashed with BCrypt
INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, active, created_at, updated_at)
VALUES (
    'admin',
    'admin@adrs.com',
    '$2a$10$8.UnVuG5wVKqj3U8nZ0Nv.XQQoXPfvCiLW5JvWHOCvgQJVdXr5H7K',
    'System',
    'Administrator',
    '+94771234567',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Log the creation
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE username = 'admin') THEN
        RAISE NOTICE 'Default admin user created or already exists';
        RAISE NOTICE 'Username: admin';
        RAISE NOTICE 'Password: admin123';
        RAISE NOTICE 'IMPORTANT: Change this password immediately in production!';
    END IF;
END $$;
