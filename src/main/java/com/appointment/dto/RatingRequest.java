package com.appointment.dto;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {
    private Long patientId;
    private Long doctorId;
    @Min(1)
    @Max(5)
    private int rating;
}