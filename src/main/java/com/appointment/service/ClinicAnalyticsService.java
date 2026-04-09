package com.appointment.service;

import com.appointment.entity.Appointment;
import com.appointment.entity.AppointmentStatus;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicAnalyticsService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    
    // A standard Java ExecutorService
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * REAL RUNNABLE: Writes check-in events to a physical log file.
     * File I/O is slow, so we do this in the background.
     */
    public void logAuditAsync(String message) {
        Runnable task = () -> {
            String timestamp = LocalDateTime.now().toString();
            String logEntry = String.format("[%s] %s\n", timestamp, message);
            
            try (FileWriter writer = new FileWriter("clinic_audit.log", true)) {
                writer.write(logEntry);
                log.info("[BACKUP LOG] Successfully wrote to clinic_audit.log");
            } catch (IOException e) {
                log.error("[BACKUP LOG] Failed to write to file", e);
            }
        };
        executor.execute(task);
    }

    /**
     * REAL CALLABLE: Calculates a doctor's actual average consultation speed.
     * Fetches real data from DB and performs calculations.
     */
    public Integer calculateDoctorAverageTime(Long doctorId) {
        Callable<Integer> task = () -> {
            log.info("[ANALYTICS] Fetching history for Doctor ID: {}", doctorId);
            
            // Fetch all COMPLETED appointments for this doctor
            List<Appointment> history = appointmentRepository.findByDoctorAndStatus(
                    doctorRepository.getReferenceById(doctorId), 
                    AppointmentStatus.DONE
            );

            if (history.isEmpty()) return 15; // Return default 15 mins if no data yet

            long totalMinutes = 0;
            int count = 0;

            for (Appointment a : history) {
                if (a.getActualStartTime() != null && a.getActualEndTime() != null) {
                    long minutes = Duration.between(a.getActualStartTime(), a.getActualEndTime()).toMinutes();
                    totalMinutes += minutes;
                    count++;
                }
            }

            return count > 0 ? (int) (totalMinutes / count) : 15;
        };

        try {
            Future<Integer> future = executor.submit(task);
            return future.get(); // Main thread waits for the result
        } catch (InterruptedException | ExecutionException e) {
            log.error("[ANALYTICS] Error calculating avg time", e);
            return 15;
        }
    }
}


 