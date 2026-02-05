package com.appointment.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Specialization is required")
    private String specialization;
    private Boolean available = true;
}
