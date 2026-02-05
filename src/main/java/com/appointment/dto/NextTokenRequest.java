package com.appointment.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextTokenRequest {
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    @NotNull(message = "Date is required")
    private LocalDate date;
}
