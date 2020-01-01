package api;

import lombok.Data;

@Data
public class AuthResponse {
    private String transactionId;
    private String authNum;
    private String errorCode;
}
