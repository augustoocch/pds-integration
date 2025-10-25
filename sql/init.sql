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
    `rank` VARCHAR(255),
    mmr INT,
    latency INT
);

-- User roles table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, roles),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert sample users
INSERT IGNORE INTO users (id, username, email, password_hash, range_per_game, region, preference, email_verified, `rank`, mmr, latency) VALUES
(1, 'sniper_pro', 'sniper@example.com', '$2a$10$hashedpassword', '100-200', 'EU', 'Competitive', true, 'Gold', 150, 50),
(2, 'support_main', 'support@example.com', '$2a$10$hashedpassword', '50-100', 'NA', 'Casual', true, 'Silver', 80, 70),
(3, 'tank_player', 'tank@example.com', '$2a$10$hashedpassword', '75-150', 'ASIA', 'Ranked', false, 'Bronze', 110, 120),
(4, 'mage_expert', 'mage@example.com', '$2a$10$hashedpassword', '120-250', 'EU', 'Competitive', true, 'Platinum', 200, 40);

-- Insert sample roles for users
INSERT IGNORE INTO user_roles (user_id, roles) VALUES
(1, 'SNIPER'),
(1, 'MARKSMAN'),
(2, 'SUPPORT'),
(2, 'MAGE'),
(3, 'TANK'),
(3, 'WARRIOR'),
(4, 'MAGE'),
(4, 'SUPPORT'),
(4, 'ASSASSIN');

-- Scrim table
CREATE TABLE IF NOT EXISTS scrim (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game VARCHAR(255),
    format VARCHAR(255),
    players INT,
    region VARCHAR(255),
    latency INT,
    est_duration INT,
    mode VARCHAR(255),
    state_type VARCHAR(50),
    mmr_min INT,
    mmr_max INT
);

-- Scrim confirmed users (ElementCollection)
CREATE TABLE IF NOT EXISTS scrim_confirmed_users (
    scrim_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (scrim_id, user_id),
    CONSTRAINT fk_scrim FOREIGN KEY (scrim_id) REFERENCES scrim(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Scrim roles (ElementCollection)
CREATE TABLE IF NOT EXISTS scrim_roles (
    scrim_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (scrim_id, role),
    FOREIGN KEY (scrim_id) REFERENCES scrim(id) ON DELETE CASCADE
);

-- Scrim participants (ManyToMany)
CREATE TABLE IF NOT EXISTS scrim_participants (
    scrim_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (scrim_id, user_id),
    FOREIGN KEY (scrim_id) REFERENCES scrim(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Notification subscribers
CREATE TABLE IF NOT EXISTS notification_subscribers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    event VARCHAR(100) NOT NULL
);