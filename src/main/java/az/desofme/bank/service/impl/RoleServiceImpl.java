package az.desofme.bank.service.impl;

import az.desofme.bank.entity.Role;
import az.desofme.bank.exceptions.BankException;
import az.desofme.bank.repository.RoleRepository;
import az.desofme.bank.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getByName(String name) throws BankException {
        return roleRepository.findByName(name)
                .orElseThrow(()->new BankException(
                        "Role not found with " + name + " name",
                        HttpStatus.NOT_FOUND.toString()
                ));
    }
}
