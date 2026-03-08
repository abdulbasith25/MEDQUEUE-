package com.appointment.controller;
import com.appointment.dto.AppointmentRequest;
import com.appointment.dto.AppointmentResponse;
import com.appointment.dto.NextTokenRequest;
import com.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/next")
    public ResponseEntity<AppointmentResponse> getNextToken(@Valid @RequestBody NextTokenRequest request) {
        AppointmentResponse response = appointmentService.getNextToken(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/skip/{id}")
    public ResponseEntity<AppointmentResponse> skipAppointment(@PathVariable Long id) {
        AppointmentResponse response = appointmentService.skipAppointment(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/current-token")
    public ResponseEntity<AppointmentResponse> getCurrentToken(
        @RequestParam Long doctorId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        AppointmentResponse response = appointmentService.getCurrentToken(doctorId, date);
        return ResponseEntity.ok(response);
    }

}
