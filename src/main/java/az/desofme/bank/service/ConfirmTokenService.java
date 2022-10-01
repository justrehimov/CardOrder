package az.desofme.bank.service;

import az.desofme.bank.entity.ConfirmToken;
import az.desofme.bank.exceptions.BankException;

public interface ConfirmTokenService {
    ConfirmToken getByToken(String token) throws BankException;
}
