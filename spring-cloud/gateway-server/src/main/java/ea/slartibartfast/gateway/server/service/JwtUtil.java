package ea.slartibartfast.gateway.server.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.LocalDateTime;

@Component
public class JwtUtil {

    private final DateUtil dateUtil;

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    public JwtUtil(DateUtil dateUtil) {
        this.dateUtil = dateUtil;
    }

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        try {
            LocalDateTime tokenExpirationTime = dateUtil.convertToLocalDateTimeViaMillisecond(this.getAllClaimsFromToken(token).getExpiration());
            return tokenExpirationTime.isBefore(LocalDateTime.now());
        } catch (ExpiredJwtException eje) {
            return true;
        }
    }

    public boolean isInvalid(String token) {
        return this.isTokenExpired(token);
    }
}
