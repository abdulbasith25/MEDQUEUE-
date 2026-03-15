package com.appointment.service;

import com.appointment.dto.AppointmentResponse;
import com.appointment.dto.DailyStatsResponse;
import com.appointment.dto.DoctorQueueStatusResponse;
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

    // ── 1. View all doctors with their live queue status for a given date ─────
    public List<DoctorQueueStatusResponse> getDoctorQueueStatus(LocalDate date) {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream().map(doctor -> {
            long booked    = appointmentRepository.countByDoctorAndDateAndStatus(doctor.getId(), date, AppointmentStatus.BOOKED);
            long done      = appointmentRepository.countByDoctorAndDateAndStatus(doctor.getId(), date, AppointmentStatus.DONE);
            long skipped   = appointmentRepository.countByDoctorAndDateAndStatus(doctor.getId(), date, AppointmentStatus.SKIPPED);
            long cancelled = appointmentRepository.countByDoctorAndDateAndStatus(doctor.getId(), date, AppointmentStatus.CANCELLED);
            List<Appointment> lastDone = appointmentRepository.findLastDoneAppointment(doctor.getId(), date);
            Integer currentToken = lastDone.isEmpty() ? null : lastDone.get(0).getTokenNumber();
            return new DoctorQueueStatusResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getAvailable(),
                booked, done, skipped, cancelled,
                currentToken
            );
        }).collect(Collectors.toList());
    }

    // ── 2. Daily appointment statistics across the whole clinic ───────────────
    public DailyStatsResponse getDailyStats(LocalDate date) {
        long total     = appointmentRepository.countByDate(date);
        long booked    = appointmentRepository.countByDateAndStatus(date, AppointmentStatus.BOOKED);
        long done      = appointmentRepository.countByDateAndStatus(date, AppointmentStatus.DONE);
        long skipped   = appointmentRepository.countByDateAndStatus(date, AppointmentStatus.SKIPPED);
        long cancelled = appointmentRepository.countByDateAndStatus(date, AppointmentStatus.CANCELLED);
        return new DailyStatsResponse(date, total, booked, done, skipped, cancelled);
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
}
