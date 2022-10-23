package az.desofme.bank.service;

import az.desofme.bank.dto.request.CustomerRequest;
import az.desofme.bank.entity.Customer;
import az.desofme.bank.entity.Role;
import az.desofme.bank.exceptions.BankException;
import az.desofme.bank.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static az.desofme.bank.constants.TestConstants.EMAIL;
import static az.desofme.bank.constants.TestConstants.NAME;
import static az.desofme.bank.constants.TestConstants.PASSWORD;
import static az.desofme.bank.constants.TestConstants.PIN;
import static az.desofme.bank.constants.TestConstants.ROLE_CUSTOMER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RoleService roleService;

    @Test
    void whenCustomerExistsByEmail_shouldThrowBankException(){
        //given
        var request = new CustomerRequest();
        request.setEmail(EMAIL);

        var expected = new Customer();
        expected.setEmail(EMAIL);

        //when
        when(customerRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(expected));

        //then
        var actual = customerRepository.findByEmail(EMAIL).get();

        Executable executable = () -> {
            if(expected.getEmail().equals(actual.getEmail())){
                throw new BankException();
            }
        };

        assertThrows(BankException.class, executable);

        verify(customerRepository).findByEmail(EMAIL);

    }

    @Test
    void whenCustomerExistsByPin_shouldThrowBankException() {
        //given
        var request = new CustomerRequest();
        request.setPin(PIN);

        var expected = new Customer();
        expected.setPin(PIN);


        //when
        when(customerRepository.findByPin(PIN)).thenReturn(Optional.of(expected));

        //then
        var actual = customerRepository.findByPin(PIN);

        assertThrows(BankException.class, ()->{
            if(Optional.of(expected).equals(actual)){
                throw new BankException();
            }
        });

        verify(customerRepository).findByPin(PIN);

    }

    @Test
    void register_Success() {
        //given
        var request = new CustomerRequest();
        request.setName(NAME);
        request.setPassword(PASSWORD);

        var expected = new Customer();
        var savedCustomer = new Customer();

        String expectedPassword = "";

        var expectedRole = new Role();

        //when
        when(modelMapper.map(request, Customer.class)).thenReturn(expected);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(expectedPassword);
        when(roleService.getByName(ROLE_CUSTOMER)).thenReturn(expectedRole);
        when(customerRepository.save(expected)).thenReturn(savedCustomer);

        //then
        var actual = modelMapper.map(request, Customer.class);
        var actualPassword = passwordEncoder.encode(request.getPassword());
        var actualRole = roleService.getByName(ROLE_CUSTOMER);
        var actualSavedCustomer = customerRepository.save(expected);

        assertEquals(expected, actual);
        assertEquals(expectedPassword, actualPassword);
        assertEquals(expectedRole, actualRole);
        assertEquals(savedCustomer, actualSavedCustomer);

        verify(modelMapper).map(request, Customer.class);
        verify(passwordEncoder).encode(request.getPassword());
        verify(roleService).getByName(ROLE_CUSTOMER);
        verify(customerRepository).save(expected);

    }

}
