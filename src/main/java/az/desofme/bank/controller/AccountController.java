package az.desofme.bank.controller;

import az.desofme.bank.dto.response.BalanceResponse;
import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.service.AccountService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balance")
    public ResponseModel<List<BalanceResponse>> getBalance(HttpServletRequest request) {
        return accountService.getBalance(request);
    }

    @PostMapping("/add-balance")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void addBalance(@RequestBody BalanceRequest balanceRequest) {
        accountService.addBalance(balanceRequest);
    }

    @Data
    public static class BalanceRequest {
        private Long accountId;
        private BigDecimal amount;
    }
}
