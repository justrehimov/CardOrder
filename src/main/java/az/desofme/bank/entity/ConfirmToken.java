package az.desofme.bank.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static az.desofme.bank.constants.Times.TOKEN_EXPIRED_TIME;

@Entity
@Data
@NoArgsConstructor
public class ConfirmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expiredAt;
    private String email;

    public ConfirmToken(Customer customer) {
        this.email = customer.getEmail();
        this.token = UUID.randomUUID().toString();
        this.expiredAt = Date.from(Instant.now().plusSeconds(TOKEN_EXPIRED_TIME));
    }
}
