package com.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorQueueStatusResponse {
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private Boolean available;
    private long totalBooked;
    private long totalDone;
    private long totalSkipped;
    private long totalCancelled;
    private Integer currentTokenNumber;
}
