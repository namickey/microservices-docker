package api;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Sales implements Serializable {
    private String companyCode;
    private LocalDateTime time;

    public Sales(AuthRequest req){
        this.companyCode = req.getCompanyCode();
        this.time = req.getTime();
    }
}
