package com.appointment.service;
import com.appointment.dto.PatientRequest;
import com.appointment.dto.PatientResponse;
import com.appointment.entity.Appointment;
import com.appointment.entity.AppointmentStatus;
import com.appointment.entity.Doctor;
import com.appointment.entity.Patient;
import com.appointment.exception.DuplicateResourceException;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.DoctorRepository;
import com.appointment.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Patient with phone " + request.getPhone() + " already exists");
        }
        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        Patient saved = patientRepository.save(patient);
        return mapToResponse(saved);
    }

    public PatientResponse myToken(Long userId) {
        LocalDate today = LocalDate.now();
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user ID: " + userId));

        // Find today's earliest BOOKED appointment for this patient
        Appointment appointment = appointmentRepository.findFirstByPatientAndDateOrderByTokenNumberDesc(patient, today)
                .orElseThrow(() -> new ResourceNotFoundException("No appointment found for today"));

        Doctor doctor = appointment.getDoctor();
        
        // Get the person who is supposed to be next (lowest token in BOOKED status)
        List<Appointment> bookedAppointments = appointmentRepository.findNextBookedAppointment(
                doctor.getId(), today, AppointmentStatus.BOOKED);
        
        Integer nextWaitingToken = bookedAppointments.isEmpty() ? appointment.getTokenNumber() : bookedAppointments.get(0).getTokenNumber();
        
        long averageConsultationTime = 0L;
        if (doctor.getTotalConsultations() != null && doctor.getTotalConsultations() > 0) {
            averageConsultationTime = doctor.getTotalDurationTook() / doctor.getTotalConsultations();
        } else {
            averageConsultationTime = 10; // Default to 10 minutes if no data
        }

        long peopleAhead = appointment.getTokenNumber() - nextWaitingToken;
        if (peopleAhead < 0) peopleAhead = 0;
        
        long waitingMinutes = peopleAhead * averageConsultationTime;
        LocalDateTime expectedTime = LocalDateTime.now().plusMinutes(waitingMinutes);

        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .createdAt(patient.getCreatedAt())
                .tokenNumber(appointment.getTokenNumber())
                .nextWaitingToken(nextWaitingToken)
                .estimatedWaitMinutes(waitingMinutes)
                .expectedTime(expectedTime)
                .build();
    }

    private PatientResponse mapToResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}
