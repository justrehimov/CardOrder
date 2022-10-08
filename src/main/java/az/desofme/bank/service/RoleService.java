package az.desofme.bank.service;

import az.desofme.bank.entity.Role;
import az.desofme.bank.exceptions.BankException;

public interface RoleService {
    Role getByName(String name) throws BankException;
}
