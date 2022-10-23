package az.desofme.bank.service.impl;

import az.desofme.bank.controller.AccountController;
import az.desofme.bank.dto.response.BalanceResponse;
import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.entity.Account;
import az.desofme.bank.entity.Customer;
import az.desofme.bank.exceptions.BankException;
import az.desofme.bank.jwt.JwtService;
import az.desofme.bank.repository.AccountRepository;
import az.desofme.bank.service.AccountService;
import az.desofme.bank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final CustomerService customerService;

    @Override
    public Account createAccount(Customer customer) {
        var account = new Account();
        account.setCreatedAt(new Date());
        account.setCustomer(customer);
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    @Override
    public Account getById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BankException(
                        "Account not found with this id:" + id,
                        HttpStatus.BAD_REQUEST.name()
                ));
    }

    @Override
    public ResponseModel<List<BalanceResponse>> getBalance(HttpServletRequest request) {
        var pin = jwtService.getPinFromRequest(request);
        var customer = customerService.getByPin(pin);

        List<Account> accounts = accountRepository.findAccountByCustomer(customer);

        List<BalanceResponse> balanceResponses = accounts.stream()
                .map(account -> new BalanceResponse(account.getBalance(), account.getId(), account.getCard().getCardNumber()))
                .collect(Collectors.toList());

        return ResponseModel.<List<BalanceResponse>>builder()
                .data(balanceResponses)
                .message(HttpStatus.OK.toString())
                .code(HttpStatus.OK.name())
                .build();

    }

    @Override
    public void addBalance(AccountController.BalanceRequest balanceRequest) {
        var account = getById(balanceRequest.getAccountId());
        account.setBalance(account.getBalance().add(balanceRequest.getAmount()));
        accountRepository.save(account);
    }

}
