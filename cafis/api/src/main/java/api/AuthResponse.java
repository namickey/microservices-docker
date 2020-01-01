package api;

import lombok.Data;

@Data
public class AuthResponse {
    private String authNum;
    private String errorCode;
}
