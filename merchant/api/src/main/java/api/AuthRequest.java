package api;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuthRequest {
    private String pan;
    private LocalDateTime time;
    private int price;
    private String merchantCode;
}
