package az.desofme.bank.controller;

import az.desofme.bank.dto.request.CustomerRequest;
import az.desofme.bank.dto.request.LoginRequest;
import az.desofme.bank.service.impl.AuthServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static az.desofme.bank.constants.TestConstants.AUTH_TOKEN;
import static az.desofme.bank.constants.TestConstants.EMAIL;
import static az.desofme.bank.constants.TestConstants.NAME;
import static az.desofme.bank.constants.TestConstants.PASSWORD;
import static az.desofme.bank.constants.TestConstants.PIN;
import static az.desofme.bank.constants.TestConstants.SURNAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = {AuthController.class})
@WithMockUser
public class AuthControllerTest {

    private static final String AUTH_PATH = "/api/v1/auth";


    @MockBean
    private AuthServiceImpl authService;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_Success() throws Exception {
        var request = new CustomerRequest();
        request.setName(NAME);
        request.setSurname(SURNAME);
        request.setEmail(EMAIL);
        request.setPin(PIN);
        request.setPassword(PASSWORD);

        mockMvc.perform(post(AUTH_PATH + "/sign-up")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk());

        verify(authService).register(request);

    }

    @Test
    void login_Success() throws Exception {
        var request = new LoginRequest();
        request.setPin(PIN);
        request.setPassword(PASSWORD);

        mockMvc.perform(post(AUTH_PATH + "/sign-in")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk());

        verify(authService).login(request);

    }

    @Test
    void confirm_Success() throws Exception {

        mockMvc.perform(get(AUTH_PATH + "/confirm-mail/{token}", AUTH_TOKEN))
                .andExpect(status().isOk());

        verify(authService).confirm(AUTH_TOKEN);

    }

}
