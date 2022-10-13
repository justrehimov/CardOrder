package az.desofme.bank.service;

import az.desofme.bank.entity.Role;
import az.desofme.bank.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static az.desofme.bank.constants.TestConstants.ROLE_NAME;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Test
    void getByName_Success() {
        //given
        var expected = new Role();
        expected.setName(ROLE_NAME);

        //when
        when(roleRepository.findByName(ROLE_NAME)).thenReturn(Optional.of(expected));

        //then
        var actual = roleRepository.findByName(ROLE_NAME);

        assertEquals(Optional.of(expected), actual);
        verify(roleRepository).findByName(ROLE_NAME);
    }
}
