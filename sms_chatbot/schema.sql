CREATE DATABASE IF NOT EXISTS sms_db;
USE sms_db;

CREATE TABLE students (
    student_id   INT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) UNIQUE NOT NULL,
    programme    VARCHAR(100),
    year_of_study INT
);

CREATE TABLE subjects (
    subject_id   INT PRIMARY KEY AUTO_INCREMENT,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20)  NOT NULL
);

CREATE TABLE teachers (
    teacher_id   INT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    subject_id   INT,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE attendance (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT,
    subject_id   INT,
    total_classes INT DEFAULT 0,
    attended      INT DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE grades (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT,
    subject_id   INT,
    marks        FLOAT,
    grade        VARCHAR(5),
    semester     VARCHAR(20),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE timetable (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT,
    subject_id   INT,
    day_of_week  ENUM('Monday','Tuesday','Wednesday','Thursday','Friday'),
    start_time   TIME,
    end_time     TIME,
    room         VARCHAR(50),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE exams (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    subject_id   INT,
    exam_date    DATE,
    start_time   TIME,
    room         VARCHAR(50),
    exam_type    ENUM('Midterm','Final','Quiz'),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE assignments (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT,
    subject_id   INT,
    title        VARCHAR(200),
    due_date     DATE,
    submitted    BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE fees (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT,
    amount       DECIMAL(10,2),
    due_date     DATE,
    paid         BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);