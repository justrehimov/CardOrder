package az.desofme.bank.controller;

import az.desofme.bank.dto.request.CustomerRequest;
import az.desofme.bank.dto.request.LoginRequest;
import az.desofme.bank.dto.response.CreateCustomerResponse;
import az.desofme.bank.dto.response.LoginResponse;
import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseModel<CreateCustomerResponse> register(@Valid @RequestBody CustomerRequest customerRequest) {
        return authService.register(customerRequest);
    }

    @PostMapping("/sign-in")
    public ResponseModel<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/confirm-mail/{token}")
    public ResponseModel<CreateCustomerResponse> confirm(@PathVariable("token") String token) {
        return authService.confirm(token);
    }
}
