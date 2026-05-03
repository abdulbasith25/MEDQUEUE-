package com.appointment.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CommentResponse{


    private Long id;
    private Long patientId;
    private String commentText;
    private Long doctorId;
}