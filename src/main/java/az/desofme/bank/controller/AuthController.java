package az.desofme.bank.controller;

import az.desofme.bank.dto.request.CustomerRequest;
import az.desofme.bank.dto.response.CreateCustomerResponse;
import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/confirm-mail/{token}")
    public ResponseModel<CreateCustomerResponse> confirm(@PathVariable("token") String token) {
        return authService.confirm(token);
    }
}
