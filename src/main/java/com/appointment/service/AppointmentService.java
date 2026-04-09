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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;


@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EmailNotificationService emailNotificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final InsuranceService insuranceService;
    
    private static final int coreThreads = 2;
    private static final int maxThreads = 10;
    private static final long timeOut = 50L;
    private static final BlockingQueue<Runnable> queueCapacity = new ArrayBlockingQueue<>(50);

    private final ExecutorService insuranceExecutor = new ThreadPoolExecutor(
        coreThreads,                                  
        maxThreads,                                 
        timeOut, 
        TimeUnit.SECONDS, 
        queueCapacity 
    );
    private final ExecutorService dynamicExecutor = Executors.newCachedThreadPool();

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));
        
        // --- INSURANCE VERIFICATION (COMPLETABLEFUTURE) ---
        // Using supplyAsync with our custom ThreadPoolExecutor
        CompletableFuture<Boolean> insuranceFuture = CompletableFuture.supplyAsync(
            () -> insuranceService.checkValidInsurance(patient.getEmail(), patient.getPhone()),
            insuranceExecutor
        );

        // Blocking here for the demo to ensure we get a result, but we allow booking regardless
        Boolean isInsuranceValid = insuranceFuture.join();
        patient.setInsuranceVerified(isInsuranceValid);
        patientRepository.save(patient); // Update the patient's record with the verification result
        
        // --- BACKGROUND AUDIT (CACHED THREAD POOL) ---
        // Fire-and-forget logging using our dynamic executor
        dynamicExecutor.execute(() -> {
            System.out.println("[AUDIT] Thread " + Thread.currentThread().getName() + " is processing audit for patient " + patient.getName());
        });

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
        AppointmentResponse response = mapToResponse(saved);
        sendQueueUpdate(doctor.getId(), saved.getDate());
        return response;
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

        // End the currently IN_PROGRESS appointment (if any) and update doctor stats
        List<Appointment> inProgressList = appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatus.IN_PROGRESS);
        if (!inProgressList.isEmpty()) {
            completeAppointment(inProgressList.get(0), doctor);
        }

        Appointment appointment = appointments.get(0);
        appointment.setActualStartTime(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        Appointment updated = appointmentRepository.save(appointment);
        sendQueueUpdate(doctor.getId(), request.getDate());
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
        sendQueueUpdate(updated.getDoctor().getId(), updated.getDate());
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
    private void sendQueueUpdate(Long doctorId, LocalDate date) {
        // Find the last "DONE" appointment to get the "Current Token"
        List<Appointment> lastDone = appointmentRepository.findLastDoneAppointment(doctorId, date);
        Integer currentToken = lastDone.isEmpty() ? 0 : lastDone.get(0).getTokenNumber();
        
        // Find the next "BOOKED" appointment
        List<Appointment> nextBooked = appointmentRepository.findNextBookedAppointment(
            doctorId, date, AppointmentStatus.BOOKED);
        Integer nextWaitingToken = nextBooked.isEmpty() ? null : nextBooked.get(0).getTokenNumber();
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(()-> new ResourceNotFoundException("doctor not found" + doctorId));
        long averageConsultationTime = 0L;
        if (doctor.getTotalConsultations() > 0) {
            averageConsultationTime = doctor.getTotalDurationTook() / doctor.getTotalConsultations();
        }
        
        long remainingCurrentTime = 0;
        List<Appointment> inProgress = appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatus.IN_PROGRESS);
        if (!inProgress.isEmpty()) {
            Appointment current = inProgress.get(0);
            if (current.getActualStartTime() != null) {
                remainingCurrentTime = Math.max(0, averageConsultationTime - 
                    java.time.Duration.between(current.getActualStartTime(), LocalDateTime.now()).toMinutes());
            }
        }
        
        long totalWaiting = (nextBooked.size() * averageConsultationTime) + remainingCurrentTime; 

        Map<String, Object> update = new HashMap<>();
        update.put("doctorId", doctorId);
        update.put("date", date.toString());
        update.put("currentToken", currentToken);
        update.put("nextWaitingToken", nextWaitingToken);
        update.put("totalWaiting", totalWaiting);
        update.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/queue/" + doctorId, update);
    }

    @Transactional
    public AppointmentResponse startConsultation(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
        
        Doctor doctor = appointment.getDoctor();
        // End any existing in-progress consultation for this doctor
        List<Appointment> inProgress = appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatus.IN_PROGRESS);
        for (Appointment p : inProgress) {
            if (!p.getId().equals(appointmentId)) {
                completeAppointment(p, doctor);
            }
        }

        appointment.setActualStartTime(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        Appointment updated = appointmentRepository.save(appointment);
        
        sendQueueUpdate(doctor.getId(), updated.getDate());
        return mapToResponse(updated);
    }

    @Transactional
    public AppointmentResponse endConsultation(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
        
        Doctor doctor = appointment.getDoctor();
        completeAppointment(appointment, doctor);
        
        sendQueueUpdate(doctor.getId(), appointment.getDate());
        return mapToResponse(appointment);
    }

    private void completeAppointment(Appointment appointment, Doctor doctor) {
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            return;
        }
        appointment.setStatus(AppointmentStatus.DONE);
        appointment.setActualEndTime(LocalDateTime.now());
        long duration = java.time.Duration.between(
            appointment.getActualStartTime(), 
            appointment.getActualEndTime()
        ).toMinutes();
        
        doctor.setTotalDurationTook(doctor.getTotalDurationTook() + duration);
        doctor.setTotalConsultations(doctor.getTotalConsultations() + 1);
        
        appointmentRepository.save(appointment);
        doctorRepository.save(doctor);
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

    @Transactional
    public void resetDailyAppointments(){
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = appointmentRepository.findByStatusAndDateLessThanEqual(AppointmentStatus.BOOKED, today);
        for(Appointment a : appointments){
            a.setStatus(AppointmentStatus.CANCELLED);
        }
        appointmentRepository.saveAll(appointments); 
    }

}
