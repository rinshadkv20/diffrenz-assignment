package com.diffrenz.assignment.security;

import com.diffrenz.assignment.domain.User;
import com.diffrenz.assignment.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * A service class that provides JWT-based authentication and authorization
 * services.
 */
@Service
public class JwtService {

    /**
     * The secret key used for signing and verifying JWT tokens.
     */
    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;
    private final Logger log = LoggerFactory.getLogger(JwtService.class);

    /**
     * token validity 5 miutes.
     */

    private final static int TOKEN_VALIDITY_IN_SECONDS = 300000;

    /**
     * The repository for storing and retrieving JWT tokens.
     */
    private final TokenRepository tokenRepository;

    /**
     * Creates a new JwtService instance.
     *
     * @param tokenRepository The repository for storing and retrieving JWT tokens.
     */
    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token.
     * @return The username.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Checks if a JWT token is valid and not expired.
     *
     * @param token The JWT token.
     * @param user  The user details.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);

        boolean validToken = tokenRepository
                .findByToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);
        log.info("Checking if token is valid");
        return (username.equals(user.getUsername())) && !isTokenExpired(token) && validToken;
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token.
     * @return True if the token has expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        log.info("Checking if token has expired");
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a claim from a JWT token.
     *
     * @param token    The JWT token.
     * @param resolver The function for resolving the claim.
     * @return The claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token.
     * @return The claims.
     */
    private Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Generates a new JWT token for a user.
     *
     * @param user The user.
     * @return The JWT token.
     */
    public String generateToken(User user) {
        log.debug(" generating token");
        return Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY_IN_SECONDS))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Gets the signing key used for JWT token signing and verification.
     *
     * @return The signing key.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}