package sn.estm.managingrestauranttickets.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Elements.JWT;

@Service
public class JwtService {
    /**
     * The class encapsulates JWT logic: generating, extracting, and validating tokens
     */

    // private final String SECRET_KEY = "your-256-bit-secret-key";

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    /**
     * Generate a JWT token
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
       /* le refresh-token must be created after the access-token
       String jwtRefreshToken = JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+15*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algo1); // Algorithm algo1=Algorithm.HMAC256("mySecret1234");
        Map<String,String> idToken=new HashMap<>();
        idToken.put("access-token", jwtAccesToken);
        idToken.put("access-token", jwtRefreshToken);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), idToken);  */

        return Jwts.builder()
                .setSubject(userDetails.getUsername())  // sets the username in the token payload
                .setIssuedAt(new Date())                // sets the creation time
               /* .setExpiration(new Date(System.currentTimeMillis()
                        + 1000 * 60 * 60 * 10))*/ // 10h expiration
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // sets expiry (10 hours)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)  // signs the JWT using your secret key and HS256 algorithm
                .compact();            // builds and returns the final JWT string
    }

    /**
     * This method extracts the username (subject) from a given JWT token string (JWT payload)
     * @param token
     * @return
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * This method verifies:
     * The username in the token matches the authenticated user & The token is not expired.
     * This ensures both integrity and validity of the token
     * @param token
     * @param userDetails
     * @return
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * This method checks whether the JWT token's expiration date is before the current time,
     * returning true if the token has expired
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * This method parses the JWT token using the signing key and returns all the claims (data)
     * contained in its payload
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * This method converts the SECRET_KEY string into a secure Key object
     * This is necessary for signing and validating JWT tokens securely with HMAC SHA-256 (HS256)
     * @return
     */
    private Key getSignKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
