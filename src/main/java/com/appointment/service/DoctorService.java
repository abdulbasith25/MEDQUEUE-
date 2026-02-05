package com.appointment.service;
import com.appointment.dto.DoctorRequest;
import com.appointment.dto.DoctorResponse;
import com.appointment.entity.Doctor;
import com.appointment.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        Doctor saved = doctorRepository.save(doctor);
        return mapToResponse(saved);
    }
    private DoctorResponse mapToResponse(Doctor doctor) {
        return new DoctorResponse(
            doctor.getId(),
            doctor.getName(),
            doctor.getSpecialization(),
            doctor.getAvailable()
        );
    }
}
