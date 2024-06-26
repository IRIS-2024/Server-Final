package app.iris.missingyou.security.jwt;

import app.iris.missingyou.exception.CustomException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenValidator {
    private final Key key;

    public boolean verifyToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰: (만료 시간: " + e.getClaims().getExpiration().toString() + ")");
            throw new CustomException(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
        } catch (IllegalArgumentException | MalformedJwtException | SecurityException e) {
            log.warn("Token: {}", token);
            throw new CustomException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new CustomException(HttpStatus.UNAUTHORIZED, "알 수 없는 오류");
        }
        return true;
    }

    public String getMemberId(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return claims.get("id", String.class);
    }

    public String getSubject(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    private Claims parseClaims(String token) throws JwtException, IllegalArgumentException {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }catch(MalformedJwtException e) {
            log.error("[in parseClaims] token: {} ", token);
            throw e;
        }
    }
}
