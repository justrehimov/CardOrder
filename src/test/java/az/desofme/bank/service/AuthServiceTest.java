package az.desofme.bank.service;

import az.desofme.bank.dto.request.CustomerRequest;
import az.desofme.bank.entity.Customer;
import az.desofme.bank.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static az.desofme.bank.constants.TestConstants.PIN;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CustomerRepository customerRepository;



    private static CustomerRequest customerRequest;

    @BeforeAll
    static void setUp() {
        customerRequest = new CustomerRequest();
        customerRequest.setPin(PIN);
    }

    @Test
    void register_Success(){
        //given
        var customer = new Customer();
        customer.setPin(PIN);

        //when
        when(modelMapper.map(customerRequest, Customer.class)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);

        //then
        var mappedCustomer = modelMapper.map(customerRequest, Customer.class);
        var savedCustomer = customerRepository.save(customer);

        assertEquals(customer, mappedCustomer);
        assertEquals(customer, savedCustomer);
        verify(modelMapper).map(customerRequest, Customer.class);
        verify(customerRepository).save(customer);
    }
}
