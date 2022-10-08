package az.desofme.bank.service.impl;

import az.desofme.bank.exceptions.BankException;
import az.desofme.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String pin) throws UsernameNotFoundException {
        return customerRepository.findByPin(pin)
                .orElseThrow(()->new BankException(
                        "User not found with " + pin + " pin",
                        HttpStatus.NOT_FOUND.toString()
                ));
    }
}
