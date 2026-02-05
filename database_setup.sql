CREATE DATABASE IF NOT EXISTS appointment_db;
USE appointment_db;
INSERT INTO patients (name, phone, created_at) VALUES
('John Doe', '9876543210', NOW()),
('Jane Smith', '9876543211', NOW()),
('Bob Johnson', '9876543212', NOW());
INSERT INTO doctors (name, specialization, available) VALUES
('Dr. Sarah Wilson', 'Cardiology', true),
('Dr. John Brown', 'Orthopedics', true),
('Dr. Emily Davis', 'Neurology', true);
INSERT INTO appointments (patient_id, doctor_id, date, token_number, status, created_at) VALUES
(1, 1, '2026-02-10', 1, 'BOOKED', NOW()),
(2, 1, '2026-02-10', 2, 'BOOKED', NOW()),
(3, 1, '2026-02-10', 3, 'BOOKED', NOW()),
(1, 2, '2026-02-10', 1, 'BOOKED', NOW());
SELECT * FROM patients;
SELECT * FROM doctors;
SELECT * FROM appointments;
SELECT 
    a.id,
    a.token_number,
    a.status,
    p.name as patient_name,
    d.name as doctor_name,
    a.date
FROM appointments a
JOIN patients p ON a.patient_id = p.id
JOIN doctors d ON a.doctor_id = d.id
WHERE d.id = 1 AND a.date = '2026-02-10'
ORDER BY a.token_number;

