package az.desofme.bank.service.impl;

import az.desofme.bank.dto.request.CustomerRequest;
import az.desofme.bank.dto.request.LoginRequest;
import az.desofme.bank.dto.response.LoginResponse;
import az.desofme.bank.dto.response.CreateCustomerResponse;
import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.entity.ConfirmToken;
import az.desofme.bank.entity.Customer;
import az.desofme.bank.entity.Role;
import az.desofme.bank.exceptions.BankException;
import az.desofme.bank.jwt.JwtService;
import az.desofme.bank.repository.ConfirmTokenRepository;
import az.desofme.bank.repository.CustomerRepository;
import az.desofme.bank.service.AuthService;
import az.desofme.bank.service.ConfirmTokenService;
import az.desofme.bank.service.CustomerService;
import az.desofme.bank.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import static az.desofme.bank.constants.Roles.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final ConfirmTokenRepository confirmTokenRepository;
    private final ConfirmTokenService confirmTokenService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JavaMailSender javaMailSender;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${app.host}")
    private String appHost;

    @Override
    @Transactional
    public ResponseModel<CreateCustomerResponse> register(CustomerRequest customerRequest) {
        try {
            validateCustomerRequest(customerRequest);
            var customer = modelMapper.map(customerRequest, Customer.class);
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            Role role = roleService.getByName(CUSTOMER);
            customer.setRoles(List.of(role));
            customer.setCreatedAt(new Date());
            var savedCustomer = customerRepository.save(customer);
            sendConfirmMail(savedCustomer);
            var createCustomerResponse = new CreateCustomerResponse(savedCustomer.getId());

            var responseModel = ResponseModel.<CreateCustomerResponse>builder()
                    .data(createCustomerResponse)
                    .code(HttpStatus.CREATED.toString())
                    .message(HttpStatus.CREATED.name())
                    .build();
            return responseModel;

        } catch (BankException ex) {
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.<CreateCustomerResponse>builder()
                    .data(null)
                    .code(ex.getCode())
                    .message(ex.getMessage())
                    .build();
            return responseModel;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.<CreateCustomerResponse>builder()
                    .data(null)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .message(ex.getLocalizedMessage())
                    .build();
            return responseModel;
        }
    }

    @Override
    @Transactional
    public ResponseModel<CreateCustomerResponse> confirm(String token) {
        try {
            var confirmToken = confirmTokenService.getByToken(token);
            var customer = customerService.getByEmail(confirmToken.getEmail());
            customer.setEnabled(true);

            var customerResponse = new CreateCustomerResponse(customer.getId());
            confirmTokenRepository.delete(confirmToken);
            var responseModel = ResponseModel.<CreateCustomerResponse>builder()
                    .data(customerResponse)
                    .code(HttpStatus.OK.toString())
                    .message(HttpStatus.OK.name())
                    .build();
            return responseModel;

        } catch (BankException ex) {
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.<CreateCustomerResponse>builder()
                    .data(null)
                    .error(true)
                    .code(ex.getCode())
                    .message(ex.getMessage())
                    .build();
            return responseModel;
        }
    }

    @Override
    public ResponseModel<LoginResponse> login(LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getPin(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            var accessToken = jwtService.generateToken(userDetails);
            var loginResponse = LoginResponse.withAccessToken(accessToken);

            return ResponseModel.<LoginResponse>builder()
                    .data(loginResponse)
                    .message(HttpStatus.OK.name())
                    .code(HttpStatus.OK.toString())
                    .build();

        }catch (BankException ex){
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.<LoginResponse>builder()
                    .data(new LoginResponse())
                    .error(true)
                    .code(ex.getCode())
                    .message(ex.getMessage())
                    .build();
            return responseModel;
        }catch (Exception ex){
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.<LoginResponse>builder()
                    .data(new LoginResponse())
                    .error(true)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .message(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .build();
            return responseModel;
        }
    }

    private void validateCustomerRequest(CustomerRequest customerRequest) throws BankException {
        if (isExistsCustomerByEmail(customerRequest.getEmail())) {
            throw new BankException(
                    "Customer exists by email: " + customerRequest.getEmail(),
                    HttpStatus.BAD_REQUEST.toString()
            );
        }
        if (isExistsCustomerByPin(customerRequest.getPin())) {
            throw new BankException(
                    "Customer exists by pin: " + customerRequest.getPin(),
                    HttpStatus.BAD_REQUEST.toString()
            );
        }
    }

    private boolean isExistsCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    private boolean isExistsCustomerByPin(String pin) {
        return customerRepository.findByPin(pin).isPresent();
    }

    @Transactional
    @Async
    protected void sendConfirmMail(Customer customer) throws BankException, UnsupportedEncodingException, MessagingException {
        var confirmToken = new ConfirmToken(customer);
        var savedConfirmToken = confirmTokenRepository.save(confirmToken);
        var message = javaMailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message);
        helper.setFrom(new InternetAddress("vusall.rehimovv@gmail.com", "Desofme Bank", "utf-8"));
        helper.setTo(confirmToken.getEmail());
        helper.setSubject("Confirmation mail");
        helper.setText(getConfirmMessage(customer, savedConfirmToken), true);
        javaMailSender.send(message);
    }

    private String getConfirmMessage(Customer customer, ConfirmToken confirmToken) {
        String link = appHost + "/api/v1/auth/confirm-mail/" + confirmToken.getToken();
        String message = "<body>\n" +
                "<h3>Welcome, " + customer.getName().concat(" " + customer.getSurname()) + "</h3>\n" +
                "<div>Please click <a href='" + link + "'>here</a> and confirm your email address</div>\n" +
                "</body>\n" +
                "</html>";
        return message;
    }


}
