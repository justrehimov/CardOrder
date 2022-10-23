package az.desofme.bank.repository;

import az.desofme.bank.entity.Account;
import az.desofme.bank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAccountByCustomer(Customer customer);
}
