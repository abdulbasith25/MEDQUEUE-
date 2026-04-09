package com.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    
    // New fields for myToken
    private Integer tokenNumber;
    private Integer nextWaitingToken;
    private Long estimatedWaitMinutes;
    private LocalDateTime expectedTime;
    private boolean checkedIn;
    private Integer personalWaitEstimate; // Result from our Callable
}
