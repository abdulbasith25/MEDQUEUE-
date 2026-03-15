package com.appointment.service;

import com.appointment.dto.AppointmentRequest;
import com.appointment.dto.AppointmentResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient samplePatient;
    private Doctor sampleDoctor;
    private AppointmentRequest sampleRequest;

    @BeforeEach
    void setUp() {
        samplePatient = new Patient();
        samplePatient.setId(1L);
        samplePatient.setName("John Doe");
        samplePatient.setEmail("john@example.com");

        sampleDoctor = new Doctor();
        sampleDoctor.setId(1L);
        sampleDoctor.setName("Dr. Smith");
        sampleDoctor.setAvailable(true);

        sampleRequest = new AppointmentRequest();
        sampleRequest.setPatientId(1L);
        sampleRequest.setDoctorId(1L);
        sampleRequest.setDate(LocalDate.now().plusDays(1));
    }

    @Test
    @DisplayName("Should successfully book an appointment and return token 1")
    void bookAppointment_Success() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(sampleDoctor));
        when(appointmentRepository.findMaxTokenNumberByDoctorAndDate(anyLong(), any())).thenReturn(Optional.of(0));
        
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment saved = invocation.getArgument(0);
            saved.setId(100L); // Mocking saved ID
            return saved;
        });

        // Act
        AppointmentResponse response = appointmentService.bookAppointment(sampleRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTokenNumber());
        assertEquals(AppointmentStatus.BOOKED, response.getStatus());
        
        // Verify dependencies were called
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(emailNotificationService, times(1)).sendAppointmentConfirmation(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when doctor is not found")
    void bookAppointment_DoctorNotFound() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.bookAppointment(sampleRequest);
        });
    }

    @Test
    @DisplayName("Should throw exception when booking for a past date")
    void bookAppointment_PastDate() {
        // Arrange
        sampleRequest.setDate(LocalDate.now().minusDays(1));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(sampleDoctor));

        // Act & Assert
        InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
            appointmentService.bookAppointment(sampleRequest);
        });
        assertEquals("Cannot book appointment for past date", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when doctor is not available")
    void bookAppointment_DoctorNotAvailable() {
        // Arrange
        sampleDoctor.setAvailable(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(sampleDoctor));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> {
            appointmentService.bookAppointment(sampleRequest);
        });
    }
}
