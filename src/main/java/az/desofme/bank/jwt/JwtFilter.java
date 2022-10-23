package az.desofme.bank.jwt;

import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.exceptions.BankException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {


    private static List<String> WHITE_LIST = List.of(
            "auth",
            "swagger",
            "docs"
    );

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var isInWhiteList = isInWhiteList(request.getServletPath());
            if (isInWhiteList) {
                filterChain.doFilter(request, response);
            } else {
                String header = request.getHeader(HttpHeaders.AUTHORIZATION);
                String token;
                if (ObjectUtils.isEmpty(header) && !StringUtils.hasText(header)) {
                    throw new BankException(
                            "Authorization header is empty: path: " + request.getServletPath(),
                            HttpStatus.BAD_REQUEST.toString()
                    );
                } else {
                    token = header.substring(7);
                    var pin = jwtService.getPinFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(pin);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(pin, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                }
            }
        } catch (BankException ex) {
            log.error(ex.getMessage(), ex);
            var exceptionResponse = ResponseModel.builder()
                    .error(true)
                    .code(ex.getCode())
                    .message(ex.getMessage())
                    .data(null)
                    .build();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), exceptionResponse);
        } catch (JwtException ex) {
            log.error(ex.getMessage(), ex);
            var exceptionResponse = ResponseModel.builder()
                    .error(true)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .message(ex.getLocalizedMessage())
                    .data(null)
                    .build();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), exceptionResponse);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            var exceptionResponse = ResponseModel.builder()
                    .error(true)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .message(ex.getLocalizedMessage())
                    .data(null)
                    .build();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), exceptionResponse);
        }
    }

    private boolean isInWhiteList(String path) {
        boolean ok = false;
        for (String whitePath : WHITE_LIST) {
            if (path.contains(whitePath)) {
                ok = true;
            }
        }
        return ok;
    }
}
