package com.appointment.service;

import com.appointment.entity.Appointment;
import com.appointment.entity.AppointmentStatus;
import com.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueShufflingService {

    private final AppointmentRepository appointmentRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    // Lock to ensure queue shuffling is atomic per doctor
    private final ReentrantLock shuffleLock = new ReentrantLock();

    /**
     * The heart of the "Smart Shuffling" engine.
     * If a patient checks in, we check if they can be moved up
     * the queue if the people ahead of them haven't arrived yet.
     */
    @Transactional
    public void tryShuffleQueue(Long doctorId, LocalDate date) {
        shuffleLock.lock();
        try {
            log.info("Checking for shuffling opportunities for Doctor ID: {} on {}", doctorId, date);
            
            // Get all BOOKED appointments for today, sorted by token
            List<Appointment> booked = appointmentRepository.findNextBookedAppointment(
                    doctorId, date, AppointmentStatus.BOOKED);
            
            if (booked.size() < 2) return; // Need at least 2 to swap

            // Look for a "Hole": Someone who hasn't checked in but is ahead of someone who has
            for (int i = 0; i < booked.size() - 1; i++) {
                Appointment ahead = booked.get(i);
                Appointment behind = booked.get(i + 1);

                if (!ahead.isCheckedIn() && behind.isCheckedIn()) {
                    log.info("SHUFFLE DETECTED: Swapping Token {} (Away) with Token {} (Present)", 
                            ahead.getTokenNumber(), behind.getTokenNumber());
                    
                    // Swap token numbers
                    int aheadToken = ahead.getTokenNumber();
                    int behindToken = behind.getTokenNumber();
                    
                    ahead.setTokenNumber(behindToken);
                    behind.setTokenNumber(aheadToken);
                    
                    appointmentRepository.save(ahead);
                    appointmentRepository.save(behind);
                    
                    // Notify everyone that the queue has changed!
                    broadcastShuffle(doctorId, ahead, behind);
                    
                    // We only do one swap per execution to keep it simple, 
                    // or we could continue to "bubble up" the checked-in person.
                    break; 
                }
            }
        } finally {
            shuffleLock.unlock();
        }
    }

    private void broadcastShuffle(Long doctorId, Appointment a, Appointment b) {
        String message = String.format("Queue updated! %s is now at Token %d (Arrived Early)", 
                b.getPatient().getName(), b.getTokenNumber());
        
        messagingTemplate.convertAndSend("/topic/queue/" + doctorId + "/alerts", message);
    }
}
