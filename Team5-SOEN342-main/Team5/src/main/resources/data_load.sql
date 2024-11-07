INSERT OR IGNORE INTO administrator (firstName, lastName, email, password) VALUES
('Admin', 'Admin', 'admin@gmail.com', 'password123');

INSERT OR IGNORE INTO client (firstName, lastName, email, password, role) VALUES
('Michael', 'Taylor', 'michael.taylor@gmail.com', 'michael123', 'CLIENT'),
('Emma', 'Williams', 'emma.williams@gmail.com', 'emma123', 'CLIENT'),
('Olivia', 'Martinez', 'olivia.martinez@gmail.com', 'olivia123', 'CLIENT'),
('Liam', 'Johnson', 'liam.johnson@gmail.com', 'liam123', 'CLIENT'),
('Ava', 'Brown', 'ava.brown@gmail.com', 'ava123', 'CLIENT');

INSERT OR IGNORE INTO instructor (firstName, lastName, email, password, role) VALUES
('James', 'Wilson', 'james.wilson@gmail.com', 'james123', 'INSTRUCTOR'),
('Sophia', 'Garcia', 'sophia.garcia@gmail.com', 'sophia123', 'INSTRUCTOR'),
('Ethan', 'Davis', 'ethan.davis@gmail.com', 'ethan123', 'INSTRUCTOR'),
('Isabella', 'Anderson', 'isabella.anderson@gmail.com', 'isabella123', 'INSTRUCTOR');

INSERT OR IGNORE INTO location (name, address, city, province, postalCode) VALUES
('Downtown Gym', '789 Oak St', 'Montreal', 'QC', 'H2Z1A3'),
('Tech Hub', '101 Pine St', 'Montreal', 'QC', 'H3B4J8'),
('Westside Pool', '456 Maple St', 'Montreal', 'QC', 'H4G2K1'),
('Cycling Arena', '123 Elm St', 'Montreal', 'QC', 'H1A2B3');

INSERT OR IGNORE INTO child (firstName, lastName, dateOfBirth, parentId) VALUES
('Noah', 'Taylor', '2014-09-12 00:00:00', 1),
('Ava', 'Taylor', '2016-11-22 00:00:00', 1),
('Ethan', 'Williams', '2015-04-05 00:00:00', 2),
('Mia', 'Johnson', '2013-07-19 00:00:00', 4),
('Lucas', 'Brown', '2017-02-10 00:00:00', 5);

INSERT OR IGNORE INTO lesson (type, title, locationId, assignedInstructorId, isAvailable, startTime, endTime) VALUES
('GROUP', 'Beginner Swimming', 3, 2, TRUE, '09:00:00', '10:00:00'),
('PRIVATE', 'Advanced Cycling Techniques', 4, 3, TRUE, '10:30:00', '11:30:00'),
('PRIVATE', 'Football Skills Development', 1, 1, TRUE, '12:00:00', '13:00:00'),
('GROUP', 'Intermediate Swimming', 3, 4, TRUE, '14:00:00', '15:00:00'),
('PRIVATE', 'Cycling Endurance Training', 4, 4, TRUE, '15:30:00', '16:30:00'),
('GROUP', 'Yoga Basics', 1, -1, FALSE, '08:00:00', '09:00:00'),  -- Unassigned
('PRIVATE', 'Boxing Fundamentals', 2, -1, FALSE, '11:00:00', '12:00:00'),  -- Unassigned
('GROUP', 'Pilates for Beginners', 4, -1, FALSE, '17:00:00', '18:00:00');  -- Unassigned

INSERT OR IGNORE INTO lesson_schedule (lessonId, dayOfWeek) VALUES
(1, 'MONDAY'),
(1, 'WEDNESDAY'),
(2, 'TUESDAY'),
(3, 'THURSDAY'),
(4, 'FRIDAY'),
(5, 'SATURDAY'),
(6, 'SUNDAY'),  -- Yoga Basics
(7, 'TUESDAY'),  -- Boxing Fundamentals
(8, 'FRIDAY');   -- Pilates for Beginners

INSERT OR IGNORE INTO client_children (clientId, childId) VALUES
(1, 1),
(1, 2),
(2, 3),
(4, 4),
(5, 5);

INSERT OR IGNORE INTO instructor_lessons (instructorId, lessonId) VALUES
(1, 3),
(2, 1),
(3, 2),
(4, 4),
(3, 5);