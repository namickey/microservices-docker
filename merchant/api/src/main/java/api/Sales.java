package api;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Sales implements Serializable {
    private String transactionId;
    private LocalDateTime time;
    private String authNum;
    private int price;

    public Sales(AuthRequest req, AuthResponse res){
        this.transactionId = res.getTransactionId();
        this.time = req.getTime();
        this.authNum = res.getAuthNum();
        this.price = req.getPrice();
    }
}
