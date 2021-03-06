package api;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Sales implements Serializable {
    private String companyCode;
    private LocalDateTime time;
    private String authNum;

    public Sales(AuthRequest req, AuthResponse res){
        this.companyCode = req.getCompanyCode();
        this.time = req.getTime();
        this.authNum = res.getAuthNum();
    }
}
