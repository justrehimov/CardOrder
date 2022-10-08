package az.desofme.bank.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String pin;
    private String password;
}
