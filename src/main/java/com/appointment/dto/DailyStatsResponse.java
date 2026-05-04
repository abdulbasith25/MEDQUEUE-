package com.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatsResponse {
    private LocalDate date;
    private long totalAppointments;
    private long totalBooked;
    private long totalDone;
    private long totalSkipped;
    private long totalCancelled;
}
