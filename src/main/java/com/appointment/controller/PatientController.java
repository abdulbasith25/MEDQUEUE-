package com.appointment.controller;
import com.appointment.dto.PatientRequest;
import com.appointment.dto.PatientResponse;
import com.appointment.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import com.appointment.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.createPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my-token")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponse> myToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        PatientResponse response = patientService.myToken(user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{appointmentId}/check-in")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponse> checkIn(@PathVariable Long appointmentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        // We first find the patient associated with the user
        // and then call checkIn.
        PatientResponse response = patientService.checkIn(user.getId(), appointmentId); // userId used as patient lookup internal proxy or fixed in service
        return ResponseEntity.ok(response);
    }
}
