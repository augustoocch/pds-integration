package ar.com.uade.pds.final_project.security;

import ar.com.uade.pds.final_project.security.exception.SecurityException;
import ar.com.uade.pds.final_project.users.entity.User;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Date;

@AllArgsConstructor
public class SecurityValidator implements ISecurityValidator {
    private static final long EXPIRATION_MS = 1000 * 60 * 60;
    private final SecretKey secretKey;

    @Override
    public String hashPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matchesPassword(String rawPassword, String hashedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("username", user.getUsername())
                .claim("region", user.getRegion())
                .claim("preference", user.getPreference())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public User getUserFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return User.builder()
                    .email(claims.getSubject())
                    .username((String) claims.get("username"))
                    .region((String) claims.get("region"))
                    .preference((String) claims.get("preference"))
                    .build();

        } catch (ExpiredJwtException e) {
            throw new SecurityException("Token has expired");
        } catch (JwtException e) {
            throw new SecurityException("Invalid token");
        }
    }
}