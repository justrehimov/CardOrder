package az.desofme.bank.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
    private BigDecimal balance = BigDecimal.valueOf(0);
    private Date createdAt;

    @OneToOne(mappedBy = "account")
    private Card card;
}
