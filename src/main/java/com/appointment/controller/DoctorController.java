package com.appointment.controller;
import com.appointment.dto.DoctorRequest;
import com.appointment.dto.DoctorResponse;
import com.appointment.entity.Doctor;
import com.appointment.repository.DoctorRepository;
import com.appointment.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;
    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);  
    }
    @GetMapping
    public ResponseEntity<java.util.List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/add")
    public ResponseEntity<Long> addDoctor(
            @RequestParam String name,
            @RequestParam(defaultValue = "General") String specialization,
            @RequestParam(required = false) String degree) {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setSpecialization(specialization);
        doctor.setDegree(degree);
        doctor.setAvailable(true);
        Doctor saved = doctorRepository.save(doctor);
        return ResponseEntity.ok(saved.getId());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DoctorResponse>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<DoctorResponse> doctors = doctorService.searchDoctors(name, specialization, page, size);

        return ResponseEntity.ok(doctors);
    }
}
