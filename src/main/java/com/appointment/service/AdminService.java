package com.appointment.service;

import com.appointment.dto.AppointmentResponse;
import com.appointment.dto.DailyStatsResponse;
import com.appointment.dto.DoctorQueueStatusResponse;
import com.appointment.dto.DoctorResponse;
import com.appointment.dto.AuthRequest;
import com.appointment.dto.AuthResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.appointment.entity.Appointment;
import com.appointment.entity.AppointmentStatus;
import com.appointment.entity.Doctor;
import com.appointment.exception.InvalidOperationException;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuthService authService;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    // ── Admin can create any user ──────────────────────────────────────────────
    @Transactional
    public com.appointment.dto.AuthResponse createUser(AuthRequest request) {
        return authService.register(request);
    }

    // ── 1. View all doctors with their live queue status for a given date ─────
    // One single DB query (JOIN + GROUP BY) instead of N×5 round trips
    public List<DoctorQueueStatusResponse> getDoctorQueueStatus(LocalDate date) {
        return appointmentRepository.findAllDoctorQueueStatus(date);
    }

    // ── 2. Daily appointment statistics across the whole clinic ───────────────
    public DailyStatsResponse getDailyStats(LocalDate date) {
        return appointmentRepository.getDailyStats(date);
    }

    // ── 3. Get all appointments for a specific date (admin full view) ─────────
    public List<AppointmentResponse> getAllAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByDateOrderByDoctorIdAscTokenNumberAsc(date)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ── 4. Cancel any appointment (admin override) ────────────────────────────
    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
        if (appointment.getStatus() == AppointmentStatus.DONE) {
            throw new InvalidOperationException("Cannot cancel an already completed appointment");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidOperationException("Appointment is already cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);
        sendQueueUpdate(saved.getDoctor().getId(), saved.getDate());
        return mapToResponse(saved);
    }

    // ── 5. Toggle doctor availability (admin can mark doctor on/off duty) ─────
    @Transactional
    public String toggleDoctorAvailability(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        doctor.setAvailable(!doctor.getAvailable());
        doctorRepository.save(doctor);
        // Alert that availability changed
        Map<String, Object> update = new HashMap<>();
        update.put("doctorId", doctorId);
        update.put("available", doctor.getAvailable());
        update.put("type", "AVAILABILITY_TOGGLE");
        messagingTemplate.convertAndSend("/topic/queue/" + doctorId, update);
        
        return "Dr. " + doctor.getName() + " is now " + (doctor.getAvailable() ? "AVAILABLE" : "UNAVAILABLE");
    }

    private void sendQueueUpdate(Long doctorId, LocalDate date) {
        List<Appointment> lastDone = appointmentRepository.findLastDoneAppointment(doctorId, date);
        Integer currentToken = lastDone.isEmpty() ? 0 : lastDone.get(0).getTokenNumber();
        
        List<Appointment> nextBooked = appointmentRepository.findNextBookedAppointment(
            doctorId, date, AppointmentStatus.BOOKED);
        Integer nextWaitingToken = nextBooked.isEmpty() ? null : nextBooked.get(0).getTokenNumber();
        
        long totalWaiting = nextBooked.size();

        Map<String, Object> update = new HashMap<>();
        update.put("doctorId", doctorId);
        update.put("date", date.toString());
        update.put("currentToken", currentToken);
        update.put("nextWaitingToken", nextWaitingToken);
        update.put("totalWaiting", totalWaiting);
        update.put("timestamp", LocalDateTime.now().toString());
        update.put("type", "QUEUE_UPDATE");

        messagingTemplate.convertAndSend("/topic/queue/" + doctorId, update);
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return new AppointmentResponse(
            a.getId(),
            a.getPatient().getId(),
            a.getPatient().getName(),
            a.getDoctor().getId(),
            a.getDoctor().getName(),
            a.getDate(),
            a.getTokenNumber(),
            a.getStatus(),
            a.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public DoctorResponse doctorStatistics(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        List<Appointment> appointments = appointmentRepository.findByDoctorAndStatusAndDate(doctor, AppointmentStatus.DONE, date);
        long totalConsultations = appointments.size();
        long totalDurationTook = calculateTotalDuration(appointments);
        long averageConsultationTime = 0;
        if (totalConsultations > 0) {
            averageConsultationTime = totalDurationTook / totalConsultations;
        }
        return new DoctorResponse(
            doctor.getId(),
            doctor.getName(),
            doctor.getSpecialization(),
            doctor.getAvailable(),
            doctor.getDegree(),
            averageConsultationTime,
            totalConsultatiaons
        );
    }

    private long calculateTotalDuration(List<Appointment> appointments) {
        long totalDurationTook = 0;
        for (Appointment a : appointments) {
            if (a.getActualStartTime() != null && a.getActualEndTime() != null) {
                long diff = java.time.Duration.between(
                    a.getActualStartTime(),
                    a.getActualEndTime()
                ).toMinutes();
                totalDurationTook += diff;
            }
        }
        return totalDurationTook;
    }
}
