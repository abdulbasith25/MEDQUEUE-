package com.appointment.service;
import com.appointment.dto.PatientRequest;
import com.appointment.dto.PatientResponse;
import com.appointment.entity.Patient;
import com.appointment.exception.DuplicateResourceException;
import com.appointment.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
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
    private PatientResponse mapToResponse(Patient patient) {
        return new PatientResponse(
            patient.getId(),
            patient.getName(),
            patient.getPhone(),
            patient.getEmail(),
            patient.getCreatedAt()
        );
    }
}
