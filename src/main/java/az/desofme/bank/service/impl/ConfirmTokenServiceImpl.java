package az.desofme.bank.service.impl;

import az.desofme.bank.entity.ConfirmToken;
import az.desofme.bank.exceptions.BankException;
import az.desofme.bank.repository.ConfirmTokenRepository;
import az.desofme.bank.service.ConfirmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ConfirmTokenServiceImpl implements ConfirmTokenService {
    private final ConfirmTokenRepository confirmTokenRepository;


    public ConfirmToken getByToken(String token) throws BankException {
        var confirmToken = confirmTokenRepository.findByToken(token)
                .orElseThrow(() -> new BankException(
                        "Token not found: " + token,
                        HttpStatus.BAD_REQUEST.toString()
                ));
        validateToken(confirmToken);
        return confirmToken;
    }

    private void validateToken(ConfirmToken confirmToken) throws BankException {
        if (confirmToken.getExpiredAt().before(Date.from(Instant.now()))) {
            throw new BankException(
                    "Token has expired: " + confirmToken.getToken(),
                    HttpStatus.BAD_REQUEST.toString()
            );
        }
    }
}
