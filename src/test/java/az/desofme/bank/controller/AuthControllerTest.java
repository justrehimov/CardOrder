package az.desofme.bank.controller;

import az.desofme.bank.dto.request.CustomerRequest;
import az.desofme.bank.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static az.desofme.bank.constants.TestConstants.EMAIL;
import static az.desofme.bank.constants.TestConstants.NAME;
import static az.desofme.bank.constants.TestConstants.PASSWORD;
import static az.desofme.bank.constants.TestConstants.PIN;
import static az.desofme.bank.constants.TestConstants.SURNAME;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@WithMockUser
@ContextConfiguration(classes = AuthController.class)
public class AuthControllerTest {

    private static final String AUTH_PATH = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private static CustomerRequest customerRequest;

    @BeforeAll
    static void setUp() {
        customerRequest = new CustomerRequest();
        customerRequest.setName(NAME);
        customerRequest.setSurname(SURNAME);
        customerRequest.setEmail(EMAIL);
        customerRequest.setPassword(PASSWORD);
        customerRequest.setPin(PIN);
    }

    @Test
    void register_Success() throws Exception{

        mockMvc.perform(post(AUTH_PATH + "/sign-up")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(customerRequest)))
                .andExpect(status().isOk());

        verify(authService).register(customerRequest);
    }
}
