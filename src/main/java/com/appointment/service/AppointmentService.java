package com.appointment.service;
import com.appointment.dto.AppointmentRequest;
import com.appointment.dto.AppointmentResponse;
import com.appointment.dto.NextTokenRequest;
import com.appointment.entity.Appointment;
import com.appointment.entity.AppointmentStatus;
import com.appointment.entity.Doctor;
import com.appointment.entity.Patient;
import com.appointment.exception.InvalidOperationException;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.DoctorRepository;
import com.appointment.repository.PatientRepository;
import com.appointment.service.notification.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EmailNotificationService emailNotificationService;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));
        if (!doctor.getAvailable()) {
            throw new InvalidOperationException("Doctor is not available");
        }
        if (request.getDate().isBefore(LocalDate.now())) {
            throw new InvalidOperationException("Cannot book appointment for past date");
        }
        Integer maxToken = appointmentRepository.findMaxTokenNumberByDoctorAndDate(
            request.getDoctorId(),
            request.getDate()
        ).orElse(0);
        Integer nextToken = maxToken + 1;
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(request.getDate());
        appointment.setTokenNumber(nextToken);
        appointment.setStatus(AppointmentStatus.BOOKED);
        Appointment saved = appointmentRepository.save(appointment);
        
        // Send async email notification if patient has an email
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            emailNotificationService.sendAppointmentConfirmation(
                patient.getEmail(),
                patient.getName(),
                doctor.getName(),
                saved.getDate().toString(),
                "Token " + saved.getTokenNumber()
            );
        }
        
        return mapToResponse(saved);
    }
    @Transactional
    public AppointmentResponse getNextToken(NextTokenRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));
        List<Appointment> appointments = appointmentRepository.findNextBookedAppointment(
            request.getDoctorId(),
            request.getDate(),
            AppointmentStatus.BOOKED
        );
        if (appointments.isEmpty()) {
            throw new ResourceNotFoundException("No booked appointments found for this doctor on " + request.getDate());
        }
        Appointment appointment = appointments.get(0);
        appointment.setStatus(AppointmentStatus.DONE);
        Appointment updated = appointmentRepository.save(appointment);
        return mapToResponse(updated);
    }
    @Transactional
    public AppointmentResponse skipAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new InvalidOperationException("Can only skip BOOKED appointments");
        }
        appointment.setStatus(AppointmentStatus.SKIPPED);
        Appointment updated = appointmentRepository.save(appointment);
        return mapToResponse(updated);
    }
    public AppointmentResponse getCurrentToken(Long doctorId, LocalDate date) {
        doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        List<Appointment> appointments = appointmentRepository.findLastDoneAppointment(doctorId, date);
        if (appointments.isEmpty()) {
            throw new ResourceNotFoundException("No completed appointments found for this doctor on " + date);
        }
        return mapToResponse(appointments.get(0));
    }
    private AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
            appointment.getId(),
            appointment.getPatient().getId(),
            appointment.getPatient().getName(),
            appointment.getDoctor().getId(),
            appointment.getDoctor().getName(),
            appointment.getDate(),
            appointment.getTokenNumber(),
            appointment.getStatus(),
            appointment.getCreatedAt()
        );
    }
}
