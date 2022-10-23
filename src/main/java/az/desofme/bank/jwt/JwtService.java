package az.desofme.bank.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {


    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.expiry-time}")
    private long EXPIRY_TIME;

    @Value("${jwt.issuer}")
    private String ISSUER;

    public String generateToken(UserDetails user){
        String token =  Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setIssuer(ISSUER)
                .setExpiration(Date.from(Instant.now().plusSeconds(EXPIRY_TIME)))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        return token;
    }

    public String getPinFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String getPinFromRequest(HttpServletRequest request){
        var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        var token = header.substring(7);
        return getPinFromToken(token);
    }

}
