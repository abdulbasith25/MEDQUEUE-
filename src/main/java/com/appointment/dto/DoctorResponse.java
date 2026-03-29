package com.appointment.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private String name;
    private String specialization;
    private Boolean available;
    private String degree;
    private Long averageConsultationTime;
    private Long totalConsultaions;
}
