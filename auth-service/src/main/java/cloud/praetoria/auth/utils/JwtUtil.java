package cloud.praetoria.auth.utils;


import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.praetoria.auth.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration; 
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    
    private static final String ISSUER = "itic-paris-auth-service";
    private static final String AUDIENCE = "itic-paris-students";
    
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("ypareo_id", user.getYpareoId());
        claims.put("email", user.getEmail());
        claims.put("first_name", user.getFirstName());
        claims.put("last_name", user.getLastName());
        claims.put("full_name", user.getFullName());
        claims.put("is_first_login", user.getIsFirstLogin());
        claims.put("is_active", user.getIsActive());
        claims.put("type", "access_token");
        
        return createToken(claims, user.getYpareoId(), expiration);
    }
    
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("ypareo_id", user.getYpareoId());
        claims.put("type", "refresh_token");
        
        return createToken(claims, user.getYpareoId(), refreshExpiration);
    }
    
    private String createToken(Map<String, Object> claims, String subject, Long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuer(ISSUER)
            .setAudience(AUDIENCE)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
    
    public String getYpareoIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    public Long getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("user_id", Long.class));
    }
    
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("email", String.class));
    }
    
    public String getFullNameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("full_name", String.class));
    }
    
    public String getTokenTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("type", String.class));
    }
    
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .requireIssuer(ISSUER)
                .requireAudience(AUDIENCE)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw new JwtException("Token is expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new JwtException("Token is unsupported");
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
            throw new JwtException("Token is malformed");
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid: {}", e.getMessage());
            throw new JwtException("Token is invalid");
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            throw new JwtException("Token validation failed");
        }
    }
    
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true; 
        }
    }
    
    public Boolean validateToken(String token, User user) {
        try {
            final String ypareoId = getYpareoIdFromToken(token);
            final Long userId = getUserIdFromToken(token);
            
            return (ypareoId.equals(user.getYpareoId()) 
                && userId.equals(user.getId())
                && !isTokenExpired(token)
                && user.getIsActive());
        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    public Boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    public Boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "access_token".equals(tokenType);
        } catch (JwtException e) {
            return false;
        }
    }
    
    public Boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "refresh_token".equals(tokenType);
        } catch (JwtException e) {
            return false;
        }
    }
    
    public Long getExpirationTime() {
        return expiration / 1000;
    }
    
    public Long getRefreshExpirationTime() {
        return refreshExpiration / 1000;
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    public Long getTimeUntilExpiration(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long timeLeft = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, timeLeft / 1000); 
        } catch (JwtException e) {
            return 0L;
        }
    }
    
    public Map<String, Object> getUserDetails(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Map<String, Object> userDetails = new HashMap<>();
            
            userDetails.put("user_id", claims.get("user_id"));
            userDetails.put("ypareo_id", claims.getSubject());
            userDetails.put("email", claims.get("email"));
            userDetails.put("first_name", claims.get("first_name"));
            userDetails.put("last_name", claims.get("last_name"));
            userDetails.put("full_name", claims.get("full_name"));
            userDetails.put("is_first_login", claims.get("is_first_login"));
            userDetails.put("is_active", claims.get("is_active"));
            userDetails.put("issued_at", claims.getIssuedAt());
            userDetails.put("expires_at", claims.getExpiration());
            
            return userDetails;
        } catch (JwtException e) {
            log.error("Failed to extract user details from token: {}", e.getMessage());
            return new HashMap<>();
        }
    }
    
    public void logTokenInfo(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            log.debug("Token Info - Subject: {}, Type: {}, Issued: {}, Expires: {}", 
                claims.getSubject(), 
                claims.get("type"), 
                claims.getIssuedAt(), 
                claims.getExpiration());
        } catch (JwtException e) {
            log.error("Cannot log token info: {}", e.getMessage());
        }
    }
}