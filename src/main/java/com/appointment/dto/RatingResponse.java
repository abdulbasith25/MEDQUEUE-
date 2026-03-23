package com.appointment.dto;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse{
    private Long id;
    private int rating;
    private LocalDateTime createdAt;
    private String doctorName;
    private String patientName;
    private Double averageRating;
}