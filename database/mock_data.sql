-- Insert mock data for testing
-- This allows testing the system without Yeditepe API access

USE course_selector_db;

-- Insert Academic Seasons
INSERT IGNORE INTO academic_seasons (id, name, name_en, name_tr, active, start_date, end_date, last_synced_at, created_at, updated_at) VALUES
(1, 'Spring 2026', 'Spring 2026', 'Bahar 2026', 1, '2026-02-01', '2026-06-30', NOW(), NOW(), NOW()),
(2, 'Fall 2025', 'Fall 2025', 'Güz 2025', 0, '2025-09-01', '2026-01-31', NOW(), NOW(), NOW());

-- Insert Departments
INSERT IGNORE INTO departments (id, name, name_en, name_tr, code, faculty_id, faculty_name, last_synced_at, created_at, updated_at) VALUES
(1, 'Computer Engineering', 'Computer Engineering', 'Bilgisayar Mühendisliği', 'COMP', 1, 'Engineering', NOW(), NOW(), NOW()),
(2, 'Electrical Engineering', 'Electrical Engineering', 'Elektrik Mühendisliği', 'EE', 1, 'Engineering', NOW(), NOW(), NOW()),
(3, 'Industrial Engineering', 'Industrial Engineering', 'Endüstri Mühendisliği', 'IE', 1, 'Engineering', NOW(), NOW(), NOW()),
(4, 'Mathematics', 'Mathematics', 'Matematik', 'MATH', 2, 'Science', NOW(), NOW(), NOW()),
(5, 'Business Administration', 'Business Administration', 'İşletme', 'BA', 3, 'Business', NOW(), NOW(), NOW());

-- Insert Sample Courses for Computer Engineering - Spring 2026
INSERT IGNORE INTO courses (season_id, department_id, code, section_number, name, name_en, name_tr, credit, ects, full_quota, quota, info, instructor, department_name, last_synced_at, created_at, updated_at) VALUES
-- CSE 344
(1, 1, 'CSE344', 1, 'System Programming', 'System Programming', 'Sistem Programlama', 4, 6, 40, 35, 'Linux system programming', 'Dr. Ali Yılmaz', 'Computer Engineering', NOW(), NOW(), NOW()),
(1, 1, 'CSE344', 2, 'System Programming', 'System Programming', 'Sistem Programlama', 4, 6, 40, 12, 'Linux system programming', 'Dr. Ayşe Demir', 'Computer Engineering', NOW(), NOW(), NOW()),

-- CSE 241
(1, 1, 'CSE241', 1, 'Object Oriented Programming', 'Object Oriented Programming', 'Nesne Yönelimli Programlama', 4, 6, 50, 45, 'Java programming', 'Dr. Mehmet Öz', 'Computer Engineering', NOW(), NOW(), NOW()),
(1, 1, 'CSE241', 2, 'Object Oriented Programming', 'Object Oriented Programming', 'Nesne Yönelimli Programlama', 4, 6, 50, 20, 'Java programming', 'Dr. Fatma Kaya', 'Computer Engineering', NOW(), NOW(), NOW()),

-- CSE 102
(1, 1, 'CSE102', 1, 'Computer Programming', 'Computer Programming', 'Bilgisayar Programlama', 4, 6, 60, 55, 'C programming basics', 'Dr. Can Arslan', 'Computer Engineering', NOW(), NOW(), NOW()),
(1, 1, 'CSE102', 2, 'Computer Programming', 'Computer Programming', 'Bilgisayar Programlama', 4, 6, 60, 30, 'C programming basics', 'Dr. Zeynep Aydın', 'Computer Engineering', NOW(), NOW(), NOW()),

-- MATH 101
(1, 4, 'MATH101', 1, 'Calculus I', 'Calculus I', 'Analiz I', 4, 6, 80, 70, 'Introduction to calculus', 'Dr. Serkan Çelik', 'Mathematics', NOW(), NOW(), NOW()),

-- BA 101
(1, 5, 'BA101', 1, 'Introduction to Business', 'Introduction to Business', 'İşletmeye Giriş', 3, 5, 100, 85, 'Business fundamentals', 'Prof. Deniz Yıldız', 'Business Administration', NOW(), NOW(), NOW());

-- Insert Course Sections (Time slots)
INSERT INTO course_sections (course_id, day, start_time, end_time, building, room, type, created_at, updated_at) VALUES
-- CSE344 Section 1
((SELECT id FROM courses WHERE code = 'CSE344' AND section_number = 1 LIMIT 1), 'MON', '09:00', '11:00', 'ENG-A', '101', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE344' AND section_number = 1 LIMIT 1), 'WED', '09:00', '11:00', 'ENG-A', '101', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE344' AND section_number = 1 LIMIT 1), 'FRI', '14:00', '16:00', 'ENG-A', 'LAB1', 'LAB', NOW(), NOW()),

-- CSE344 Section 2
((SELECT id FROM courses WHERE code = 'CSE344' AND section_number = 2 LIMIT 1), 'TUE', '11:00', '13:00', 'ENG-B', '205', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE344' AND section_number = 2 LIMIT 1), 'THU', '11:00', '13:00', 'ENG-B', '205', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE344' AND section_number = 2 LIMIT 1), 'FRI', '09:00', '11:00', 'ENG-B', 'LAB2', 'LAB', NOW(), NOW()),

-- CSE241 Section 1
((SELECT id FROM courses WHERE code = 'CSE241' AND section_number = 1 LIMIT 1), 'MON', '14:00', '16:00', 'ENG-A', '102', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE241' AND section_number = 1 LIMIT 1), 'WED', '14:00', '16:00', 'ENG-A', '102', 'LEC', NOW(), NOW()),

-- CSE241 Section 2
((SELECT id FROM courses WHERE code = 'CSE241' AND section_number = 2 LIMIT 1), 'TUE', '09:00', '11:00', 'ENG-B', '201', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE241' AND section_number = 2 LIMIT 1), 'THU', '09:00', '11:00', 'ENG-B', '201', 'LEC', NOW(), NOW()),

-- CSE102 Section 1
((SELECT id FROM courses WHERE code = 'CSE102' AND section_number = 1 LIMIT 1), 'MON', '11:00', '13:00', 'ENG-A', '103', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE102' AND section_number = 1 LIMIT 1), 'WED', '11:00', '13:00', 'ENG-A', '103', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE102' AND section_number = 1 LIMIT 1), 'THU', '14:00', '17:00', 'ENG-A', 'LAB3', 'LAB', NOW(), NOW()),

-- CSE102 Section 2
((SELECT id FROM courses WHERE code = 'CSE102' AND section_number = 2 LIMIT 1), 'TUE', '14:00', '16:00', 'ENG-B', '202', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE102' AND section_number = 2 LIMIT 1), 'THU', '14:00', '16:00', 'ENG-B', '202', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'CSE102' AND section_number = 2 LIMIT 1), 'FRI', '11:00', '14:00', 'ENG-B', 'LAB4', 'LAB', NOW(), NOW()),

-- MATH101 Section 1
((SELECT id FROM courses WHERE code = 'MATH101' AND section_number = 1 LIMIT 1), 'MON', '09:00', '11:00', 'SCI-A', '301', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'MATH101' AND section_number = 1 LIMIT 1), 'WED', '09:00', '11:00', 'SCI-A', '301', 'LEC', NOW(), NOW()),
((SELECT id FROM courses WHERE code = 'MATH101' AND section_number = 1 LIMIT 1), 'FRI', '09:00', '11:00', 'SCI-A', '301', 'LEC', NOW(), NOW()),

-- BA101 Section 1
((SELECT id FROM courses WHERE code = 'BA101' AND section_number = 1 LIMIT 1), 'TUE', '14:00', '17:00', 'BUS-A', '101', 'LEC', NOW(), NOW());

SELECT 'Mock data inserted successfully!' as status;
