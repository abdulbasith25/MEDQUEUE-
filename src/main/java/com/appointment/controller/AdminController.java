package com.appointment.controller;

import com.appointment.dto.AppointmentResponse;
import com.appointment.dto.DailyStatsResponse;
import com.appointment.dto.DoctorQueueStatusResponse;
import com.appointment.dto.DoctorResponse;
import com.appointment.dto.AuthRequest;
import com.appointment.dto.AuthResponse;
import com.appointment.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    
    @GetMapping("/queue-status")
    public ResponseEntity<List<DoctorQueueStatusResponse>> getDoctorQueueStatus(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(adminService.getDoctorQueueStatus(targetDate));  
    }

    
    @GetMapping("/daily-stats")
    public ResponseEntity<DailyStatsResponse> getDailyStats(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(adminService.getDailyStats(targetDate));
    }

    
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointmentsByDate(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(adminService.getAllAppointmentsByDate(targetDate));
    }

    
    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.cancelAppointment(id));
    }

    
    @PutMapping("/doctors/{id}/toggle-availability")
    public ResponseEntity<String> toggleDoctorAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleDoctorAvailability(id));
    }

    
    @PostMapping("/users")
    public ResponseEntity<AuthResponse> createUser(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @GetMapping("/doctor-statistics")
    public ResponseEntity<DoctorResponse> doctorStatistics(@RequestParam Long id,
     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(adminService.doctorStatistics(id, targetDate));
    }
    
}
