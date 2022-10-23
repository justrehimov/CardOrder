package az.desofme.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    private BigDecimal balance;
    private Long accountId;
    private String cardNumber;
}
