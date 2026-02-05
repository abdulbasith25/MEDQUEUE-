# Appointment System

A basic Spring Boot application for managing doctor appointments and patient tokens.

## Setup

1. Create a MySQL database named `appointment_db`.
2. Update `src/main/resources/application.properties` with your database credentials.
3. Run `mvn spring-boot:run`.

## How it works

- Patients and Doctors can be registered.
- Appointments generate a token number for each doctor per day.
- You can fetch the next token or skip an appointment.
