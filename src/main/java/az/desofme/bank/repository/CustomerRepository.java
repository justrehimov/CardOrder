package az.desofme.bank.repository;

import az.desofme.bank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c where lower(c.email)=lower(:email) ")
    Optional<Customer> findByEmail(String email);

    @Query("select c from Customer c where lower(c.pin)=lower(:pin) ")
    Optional<Customer> findByPin(String pin);
}
