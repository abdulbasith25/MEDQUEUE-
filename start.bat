@echo off
echo Starting Appointment System...
call mvn clean install
if %errorlevel% neq 0 (
    echo Build failed.
    pause
    exit /b 1
)
call mvn spring-boot:run
