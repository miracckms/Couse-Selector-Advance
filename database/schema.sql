-- ============================================
-- Course Selector Database Schema
-- CSE344 Spring 2026 Project
-- Yeditepe University
-- ============================================

-- Create Database
CREATE DATABASE IF NOT EXISTS course_selector_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE course_selector_db;

-- ============================================
-- Table: users
-- Description: Stores user information for students and staff
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    student_id VARCHAR(20),
    department VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: user_roles
-- Description: Stores user roles (ROLE_USER, ROLE_ADMIN, etc.)
-- ============================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_role (user_id, role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: saved_schedules
-- Description: Stores user's saved course schedules
-- ============================================
CREATE TABLE IF NOT EXISTS saved_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    season_id BIGINT NOT NULL,
    season_name VARCHAR(100),
    total_credits INT,
    total_ects INT,
    is_favorite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_schedules (user_id, updated_at DESC),
    INDEX idx_user_season (user_id, season_id),
    INDEX idx_favorites (user_id, is_favorite)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table: schedule_courses
-- Description: Stores courses within a saved schedule
-- ============================================
CREATE TABLE IF NOT EXISTS schedule_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    course_name VARCHAR(200),
    section INT NOT NULL,
    credits INT,
    ects INT,
    instructor VARCHAR(200),
    department_id BIGINT,
    department_name VARCHAR(100),
    time_slots TEXT COMMENT 'JSON format: [{"day":"MON","start":"09:00","end":"10:30","room":"C104"}]',
    FOREIGN KEY (schedule_id) REFERENCES saved_schedules(id) ON DELETE CASCADE,
    INDEX idx_schedule_courses (schedule_id),
    INDEX idx_course_code (course_code, section)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Insert Sample Data (for testing)
-- ============================================

-- Sample User (password: password123)
-- Password is BCrypt encoded
INSERT INTO users (username, email, password, first_name, last_name, student_id, department, is_active)
VALUES 
('testuser', 'test@yeditepe.edu.tr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test', 'User', 'U20210000001', 'Computer Engineering', TRUE),
('admin', 'admin@yeditepe.edu.tr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'User', 'U20210000002', 'Computer Engineering', TRUE);

-- Sample Roles
INSERT INTO user_roles (user_id, role)
VALUES 
(1, 'ROLE_USER'),
(2, 'ROLE_USER'),
(2, 'ROLE_ADMIN');

-- ============================================
-- Views (Optional - for reporting/analytics)
-- ============================================

-- View: User schedule statistics
CREATE OR REPLACE VIEW user_schedule_stats AS
SELECT 
    u.id as user_id,
    u.username,
    u.email,
    COUNT(ss.id) as total_schedules,
    SUM(CASE WHEN ss.is_favorite THEN 1 ELSE 0 END) as favorite_schedules,
    MAX(ss.updated_at) as last_schedule_update
FROM users u
LEFT JOIN saved_schedules ss ON u.id = ss.user_id
GROUP BY u.id, u.username, u.email;

-- View: Popular courses (most saved)
CREATE OR REPLACE VIEW popular_courses AS
SELECT 
    sc.course_code,
    sc.course_name,
    COUNT(DISTINCT sc.schedule_id) as times_saved,
    COUNT(DISTINCT ss.user_id) as unique_users,
    AVG(sc.credits) as avg_credits
FROM schedule_courses sc
JOIN saved_schedules ss ON sc.schedule_id = ss.id
GROUP BY sc.course_code, sc.course_name
ORDER BY times_saved DESC;

-- ============================================
-- Stored Procedures (Optional)
-- ============================================

DELIMITER //

-- Procedure: Get user's schedule summary
CREATE PROCEDURE GetUserScheduleSummary(IN userId BIGINT)
BEGIN
    SELECT 
        ss.id,
        ss.name,
        ss.season_name,
        ss.total_credits,
        ss.total_ects,
        COUNT(sc.id) as course_count,
        ss.is_favorite,
        ss.created_at,
        ss.updated_at
    FROM saved_schedules ss
    LEFT JOIN schedule_courses sc ON ss.id = sc.schedule_id
    WHERE ss.user_id = userId
    GROUP BY ss.id
    ORDER BY ss.updated_at DESC;
END //

-- Procedure: Clean old schedules (older than 2 years and not favorite)
CREATE PROCEDURE CleanOldSchedules()
BEGIN
    DELETE FROM saved_schedules
    WHERE is_favorite = FALSE 
    AND updated_at < DATE_SUB(NOW(), INTERVAL 2 YEAR);
    
    SELECT ROW_COUNT() as deleted_schedules;
END //

DELIMITER ;

-- ============================================
-- Indexes for Performance Optimization
-- ============================================

-- Additional composite indexes for common queries
CREATE INDEX idx_schedule_user_season ON saved_schedules(user_id, season_id, updated_at DESC);
CREATE INDEX idx_course_instructor ON schedule_courses(instructor);
CREATE INDEX idx_user_active ON users(is_active, created_at);

-- ============================================
-- Database Information
-- ============================================
SELECT 'Database schema created successfully!' as message;
SELECT VERSION() as mysql_version;
SELECT DATABASE() as current_database;
