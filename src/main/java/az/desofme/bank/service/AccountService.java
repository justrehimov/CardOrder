package az.desofme.bank.service;

import az.desofme.bank.controller.AccountController;
import az.desofme.bank.dto.response.BalanceResponse;
import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.entity.Account;
import az.desofme.bank.entity.Customer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AccountService {
    Account createAccount(Customer customer);

    Account getById(Long id);

    ResponseModel<List<BalanceResponse>> getBalance(HttpServletRequest request);

    void addBalance(AccountController.BalanceRequest balanceRequest);
}
