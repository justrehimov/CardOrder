package az.desofme.bank.service;


import az.desofme.bank.dto.request.CustomerRequest;
import az.desofme.bank.dto.request.LoginRequest;
import az.desofme.bank.dto.response.CreateCustomerResponse;
import az.desofme.bank.dto.response.LoginResponse;
import az.desofme.bank.dto.response.ResponseModel;

public interface AuthService {
    ResponseModel<CreateCustomerResponse> register(CustomerRequest customerRequest);

    ResponseModel<CreateCustomerResponse> confirm(String token);

    ResponseModel<LoginResponse> login(LoginRequest loginRequest);
}
