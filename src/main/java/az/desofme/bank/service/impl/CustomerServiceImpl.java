package az.desofme.bank.service.impl;

import az.desofme.bank.entity.Customer;
import az.desofme.bank.exceptions.BankException;
import az.desofme.bank.repository.CustomerRepository;
import az.desofme.bank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;


    @Override
    public Customer getByEmail(String email) throws BankException {
        var customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankException(
                        "Customer not found with this email:" + email,
                        HttpStatus.BAD_REQUEST.toString()
                ));
        return customer;
    }
}
