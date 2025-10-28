-- Fix admin password with proper BCrypt hash
-- Password: admin123

UPDATE users 
SET password = '$2a$10$.jQ4luc8o9T/HGQutUSzuOq/Ft.wZ8ku.S91Z./3xcYSV2t//2dbu' 
WHERE username = 'admin';

-- Verify the update
SELECT username, length(password) as pwd_len, 
       substring(password, 1, 10) as pwd_start 
FROM users 
WHERE username = 'admin';
