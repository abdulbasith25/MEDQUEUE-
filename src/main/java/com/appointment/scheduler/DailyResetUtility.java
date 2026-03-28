package com.appointment.scheduler;

import com.appointment.repository.AppointmentRepository;
import com.appointment.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;    
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyResetUtility {
    private final AppointmentService appointmentService;
    private static final Logger logger = LoggerFactory.getLogger(DailyResetUtility.class);
   

    @Scheduled(cron = "0 59 23 * * ?")
    public void resetDailyAppointments() {
        logger.info("Starting daily reset of appointments at 11:59 PM");
        appointmentService.resetDailyAppointments();
        logger.info("Daily reset of appointments completed successfully");
    }
}