-- Create database if not exists
CREATE DATABASE IF NOT EXISTS pds_project;

USE pds_project;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    range_per_game VARCHAR(255),
    region VARCHAR(255),
    preference VARCHAR(255),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create user_roles table for the collection
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    preferred_roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, preferred_roles),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert sample users
INSERT IGNORE INTO users (id, username, email, password_hash, range_per_game, region, preference, email_verified) VALUES
(1, 'sniper_pro', 'sniper@example.com', '$2a$10$hashedpassword', '100-200', 'EU', 'Competitive', true),
(2, 'support_main', 'support@example.com', '$2a$10$hashedpassword', '50-100', 'NA', 'Casual', true),
(3, 'tank_player', 'tank@example.com', '$2a$10$hashedpassword', '75-150', 'ASIA', 'Ranked', false),
(4, 'mage_expert', 'mage@example.com', '$2a$10$hashedpassword', '120-250', 'EU', 'Competitive', true);

-- Insert sample roles for users (using your actual enum values)
INSERT IGNORE INTO user_roles (user_id, preferred_roles) VALUES
(1, 'SNIPER'),
(1, 'MARKSMAN'),
(2, 'SUPPORT'),
(2, 'MAGE'),
(3, 'TANK'),
(3, 'WARRIOR'),
(4, 'MAGE'),
(4, 'SUPPORT'),
(4, 'ASSASSIN');