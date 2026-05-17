import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingTryEvent {
    private String patientId;
    private String patientEmail;
    private Long patientPhone;
    
}