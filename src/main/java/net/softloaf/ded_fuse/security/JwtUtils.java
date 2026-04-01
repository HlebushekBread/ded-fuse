package net.softloaf.ded_fuse.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.lifetime}")
    private long jwtLifetime;

    public String generateToken(UserDetailsImpl userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> authoritiesList = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("id", String.valueOf(userDetails.getUser().getId()));
        claims.put("authorities", authoritiesList);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtLifetime))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS512)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public String getIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("id", String.class);
    }

    public List<String> getAuthoritiesFromToken(String token) {
        return getAllClaimsFromToken(token).get("authorities", List.class);
    }
}
