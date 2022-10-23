package az.desofme.bank.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardToCardRequest {
    private String cardNumber;
    private Long accountId;
    private BigDecimal amount;
}
