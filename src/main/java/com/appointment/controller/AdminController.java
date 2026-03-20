package com.appointment.controller;

import com.appointment.dto.AppointmentResponse;
import com.appointment.dto.DailyStatsResponse;
import com.appointment.dto.DoctorQueueStatusResponse;
import com.appointment.dto.AuthRequest;
import com.appointment.dto.AuthResponse;
import com.appointment.service.AdminService;
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

    /**
     * GET /admin/queue-status?date=2026-02-21
     * Returns every doctor with their booked/done/skipped/cancelled counts
     * and the current (last done) token number for today or any given date.
     */
    @GetMapping("/queue-status")
    public ResponseEntity<List<DoctorQueueStatusResponse>> getDoctorQueueStatus(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(adminService.getDoctorQueueStatus(targetDate));
    }

    /**
     * GET /admin/daily-stats?date=2026-02-21
     * Returns total/booked/done/skipped/cancelled counts for the whole clinic on that day.
     */
    @GetMapping("/daily-stats")
    public ResponseEntity<DailyStatsResponse> getDailyStats(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(adminService.getDailyStats(targetDate));
    }

    /**
     * GET /admin/appointments?date=2026-02-21
     * Returns every appointment across all doctors for a given date, sorted by doctor then token.
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointmentsByDate(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(adminService.getAllAppointmentsByDate(targetDate));
    }

    /**
     * PUT /admin/appointments/{id}/cancel
     * Admin override: cancels any appointment regardless of who booked it.
     * Cannot cancel an already DONE appointment.
     */
    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.cancelAppointment(id));
    }

    /**
     * PUT /admin/doctors/{id}/toggle-availability
     * Toggles a doctor between AVAILABLE and UNAVAILABLE.
     * Useful when a doctor goes on emergency leave mid-day.
     */
    @PutMapping("/doctors/{id}/toggle-availability")
    public ResponseEntity<String> toggleDoctorAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleDoctorAvailability(id));
    }

    /**
     * POST /admin/users
     * Admin can create any user (Patient, Doctor, Admin)
     */
    @PostMapping("/users")
    public ResponseEntity<AuthResponse> createUser(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }
}
