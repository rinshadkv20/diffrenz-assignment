CREATE DATABASE IF NOT EXISTS console;
USE console;



INSERT INTO user (username, password, role) VALUES
('admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC', 'ADMIN'),
('user', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K', 'USER'),
('user2', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K', 'USER');




-- Insert dummy data into Account table
INSERT INTO account (account_type, account_number,user_id) VALUES
('Savings', 1234567890,2),
('Checking', 9876543210,3),
('Credit Card', 5555555555,2);

-- Create Statement table


-- Insert dummy data into Statement table
INSERT INTO statement (account_id, transaction_date, amount) VALUES
(1,'2024-03-01 09:00:00',1000.5),
(1,'2024-03-05 14:30:00',500.75),
(2,'2024-03-02 10:15:00',1500.25),
(3,'2024-03-04 12:45:00',200),
(3,'2024-03-06 11:00:00',750.8),
(1,'2023-12-20 00:00:00',1000.5),
(1,'2023-12-29 00:00:00',500.75),
(1,'2024-01-08 00:00:00',1500.25),
(2,'2023-12-24 00:00:00',2000),
(2,'2024-01-03 00:00:00',750.8),
(3,'2023-12-21 00:00:00',1200),
(3,'2024-01-06 00:00:00',900.5),
(1,'2024-01-13 00:00:00',1800.35),
(2,'2023-12-29 00:00:00',3000.6),
(3,'2023-12-26 00:00:00',1500.75),
(1,'2024-01-18 00:00:00',900.2),
(2,'2023-12-31 00:00:00',2100.9);
