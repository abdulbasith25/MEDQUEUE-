package com.appointment.service;
import com.appointment.dto.DoctorRequest;
import com.appointment.dto.DoctorResponse;
import com.appointment.entity.Doctor;
import com.appointment.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true) 
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setDegree(request.getDegree());
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
            doctor.getAvailable(),
            doctor.getDegree()
        );
    }
    public Page<DoctorResponse> searchDoctors(String name, String specialization, int page, int size) {
        name = (name == null) ? "" : name;
        specialization = (specialization == null) ? "" : specialization;
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Doctor> doctorPage = doctorRepository.findByNameContainingIgnoreCaseAndSpecializationContainingIgnoreCase(name, specialization, pageable);
        return doctorPage.map(this::mapToResponse);
    }
    
}
