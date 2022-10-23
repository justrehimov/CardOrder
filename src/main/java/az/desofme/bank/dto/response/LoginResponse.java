package az.desofme.bank.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;

    public static LoginResponse withAccessToken(String accessToken) {
        var loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        return loginResponse;
    }

}
