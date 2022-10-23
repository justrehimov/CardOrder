package az.desofme.bank.service;

import az.desofme.bank.entity.Customer;
import az.desofme.bank.exceptions.BankException;

public interface CustomerService {
    Customer getByEmail(String email) throws BankException;
    Customer getByPin(String pin) throws BankException;
}
