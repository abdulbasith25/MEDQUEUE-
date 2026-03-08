package com.appointment.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    // Use our custom thread pool specific for notifications
    @Async("notificationExecutor")
    public void sendAppointmentConfirmation(String toEmail, String patientName, String doctorName, String date, String time) {
        log.info("Starting email sending process on thread: {}", Thread.currentThread().getName());
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Appointment Confirmed");
            
            String text = String.format(
                "Dear %s,\n\nYour appointment with Dr. %s is confirmed for %s at %s.\n\nThank you for booking with us!",
                patientName, doctorName, date, time
            );
            message.setText(text);
            
            // Simulating connection delay if testing without actual credentials
            // Thread.sleep(2000); 
            
            javaMailSender.send(message);
            log.info("Successfully sent booking confirmation email to {} on thread: {}", toEmail, Thread.currentThread().getName());
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}
