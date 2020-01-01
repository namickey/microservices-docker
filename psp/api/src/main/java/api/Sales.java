package api;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Sales implements Serializable {
    private String merchantCode;
    private LocalDateTime time;
    private String transactionId;
    private String authNum;
    private int price;

    public Sales(AuthRequest req, AuthResponse res){
        this.merchantCode = req.getMerchantCode();
        this.time = req.getTime();
        this.transactionId = res.getTransactionId();
        this.authNum = res.getAuthNum();
        this.price = req.getPrice();
    }
}
