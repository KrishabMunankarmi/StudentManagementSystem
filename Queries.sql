-- 1. STUDENTS
CREATE TABLE students (
  student_id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  email varchar(150) NOT NULL,
  programme varchar(100) DEFAULT NULL,
  year_of_study int(11) DEFAULT NULL,
  password varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (student_id),
  UNIQUE KEY email (email)
);

-- 2. SUBJECTS
CREATE TABLE subjects (
  subject_id int(11) NOT NULL AUTO_INCREMENT,
  subject_name varchar(100) NOT NULL,
  subject_code varchar(20) NOT NULL,
  PRIMARY KEY (subject_id)
);

-- 3. TEACHERS
CREATE TABLE teachers (
  teacher_id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  subject_id int(11) DEFAULT NULL,
  email varchar(150) NOT NULL DEFAULT '',
  password varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (teacher_id),
  UNIQUE KEY email (email),
  FOREIGN KEY (subject_id) REFERENCES subjects (subject_id)
);

-- 4. ASSIGNMENTS
CREATE TABLE assignments (
  id int(11) NOT NULL AUTO_INCREMENT,
  student_id int(11) DEFAULT NULL,
  subject_id int(11) DEFAULT NULL,
  title varchar(200) DEFAULT NULL,
  due_date date DEFAULT NULL,
  submitted tinyint(1) DEFAULT 0,
  submission_file varchar(500) DEFAULT NULL,
  submitted_at datetime DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (student_id) REFERENCES students (student_id),
  FOREIGN KEY (subject_id) REFERENCES subjects (subject_id)
);

-- 5. ATTENDANCE
CREATE TABLE attendance (
  id int(11) NOT NULL AUTO_INCREMENT,
  student_id int(11) DEFAULT NULL,
  subject_id int(11) DEFAULT NULL,
  total_classes int(11) DEFAULT 0,
  attended int(11) DEFAULT 0,
  PRIMARY KEY (id),
  FOREIGN KEY (student_id) REFERENCES students (student_id),
  FOREIGN KEY (subject_id) REFERENCES subjects (subject_id)
);

-- 6. GRADES
CREATE TABLE grades (
  id int(11) NOT NULL AUTO_INCREMENT,
  student_id int(11) DEFAULT NULL,
  subject_id int(11) DEFAULT NULL,
  marks float DEFAULT NULL,
  grade varchar(5) DEFAULT NULL,
  semester varchar(20) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (student_id) REFERENCES students (student_id),
  FOREIGN KEY (subject_id) REFERENCES subjects (subject_id)
);

-- 7. EXAMS
CREATE TABLE exams (
  id int(11) NOT NULL AUTO_INCREMENT,
  subject_id int(11) DEFAULT NULL,
  exam_date date DEFAULT NULL,
  start_time time DEFAULT NULL,
  room varchar(50) DEFAULT NULL,
  exam_type enum('Midterm','Final','Quiz') DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (subject_id) REFERENCES subjects (subject_id)
);

-- 8. FEES
CREATE TABLE fees (
  id int(11) NOT NULL AUTO_INCREMENT,
  student_id int(11) DEFAULT NULL,
  amount decimal(10,2) DEFAULT NULL,
  due_date date DEFAULT NULL,
  paid tinyint(1) DEFAULT 0,
  PRIMARY KEY (id),
  FOREIGN KEY (student_id) REFERENCES students (student_id)
);

-- 9. TIMETABLE
CREATE TABLE timetable (
  id int(11) NOT NULL AUTO_INCREMENT,
  student_id int(11) DEFAULT NULL,
  subject_id int(11) DEFAULT NULL,
  day_of_week enum('Monday','Tuesday','Wednesday','Thursday','Friday') DEFAULT NULL,
  start_time time DEFAULT NULL,
  end_time time DEFAULT NULL,
  room varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (student_id) REFERENCES students (student_id),
  FOREIGN KEY (subject_id) REFERENCES subjects (subject_id)
);

-- 10. CHAT SESSIONS
CREATE TABLE chat_sessions (
  session_id int(11) NOT NULL AUTO_INCREMENT,
  student_id int(11) NOT NULL,
  started_at datetime DEFAULT current_timestamp(),
  title varchar(200) DEFAULT 'New Chat',
  PRIMARY KEY (session_id),
  FOREIGN KEY (student_id) REFERENCES students (student_id)
);

-- 11. CHAT MESSAGES
CREATE TABLE chat_messages (
  message_id int(11) NOT NULL AUTO_INCREMENT,
  session_id int(11) NOT NULL,
  sender enum('user','bot') NOT NULL,
  message text NOT NULL,
  sent_at datetime DEFAULT current_timestamp(),
  PRIMARY KEY (message_id),
  FOREIGN KEY (session_id) REFERENCES chat_sessions (session_id)
);