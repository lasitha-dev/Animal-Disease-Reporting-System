-- Migration: Add province and district columns to users table
-- Date: 2025-10-30
-- Description: Adds location tracking for users with province and district fields

-- Add province column
ALTER TABLE users 
ADD COLUMN province VARCHAR(30);

-- Add district column
ALTER TABLE users 
ADD COLUMN district VARCHAR(30);

-- Add comments for documentation
COMMENT ON COLUMN users.province IS 'Province where the user is located (e.g., WESTERN, CENTRAL)';
COMMENT ON COLUMN users.district IS 'District where the user is located (e.g., COLOMBO, KANDY)';
