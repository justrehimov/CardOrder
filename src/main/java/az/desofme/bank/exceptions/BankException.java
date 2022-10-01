package az.desofme.bank.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BankException extends RuntimeException {
    private String message;
    private String code;


    public BankException(String message, String code) {
        super(message);
        this.message = message;
        this.code = code;
    }
}
