package com.diffrenz.assignment.service;

import com.diffrenz.assignment.domain.Token;
import com.diffrenz.assignment.domain.User;
import com.diffrenz.assignment.repository.TokenRepository;
import com.diffrenz.assignment.repository.UserRepository;
import com.diffrenz.assignment.security.JwtService;
import com.diffrenz.assignment.service.VM.LoginVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuthService {

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    /**
     * Constructs a new AuthService instance.
     *
     * @param tokenRepository       the TokenRepository used to manage user tokens
     * @param authenticationManager the AuthenticationManager used to authenticate
     *                              users
     * @param userRepository        the UserRepository used to retrieve user
     *                              information
     * @param passwordEncoder       the PasswordEncoder used to hash passwords
     * @param jwtService            the JwtService used to generate JWT tokens
     */
    public AuthService(TokenRepository tokenRepository, AuthenticationManager authenticationManager,
            UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Returns the set of roles for the currently authenticated user.
     *
     * @return the set of roles for the currently authenticated user
     */
    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    }

    /**
     * Returns the set of roles for the currently authenticated user.
     *
     * @return the set of roles for the currently authenticated user
     */

    public static Set<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getAuthorities(authentication).collect(Collectors.toSet());
    }

    /**
     * Returns the login of the currently authenticated user, if any.
     *
     * @return the login of the currently authenticated user, or an empty optional
     *         if no user is authenticated
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    /**
     * Extracts the principal from an authentication object, if possible.
     *
     * @param authentication the authentication object
     * @return the principal, or null if the authentication object does not contain
     *         a principal
     */
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Authenticates a user using the provided username and password.
     *
     * @param request the LoginVM containing the username and password
     * @return the JWT token if authentication is successful, or an error message if
     *         it fails
     */
    public String authenticate(LoginVM request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        log.info("authenticating user......");
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String jwt = jwtService.generateToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);

        log.debug("successfully authenticated returning jwt");
        return jwt;

    }

    /**
     * Revokes all active tokens for a given user.
     *
     * @param user the user for which to revoke all active tokens
     */
    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> {
            t.setLoggedOut(true);
        });

        log.debug("revoking all active tokens");
        tokenRepository.saveAll(validTokens);
    }

    /**
     * Saves a new active token for a given user.
     *
     * @param jwt  the JWT token to save
     * @param user the user for which to save the token
     */
    private void saveUserToken(String jwt, User user) {
        log.debug("saving new active token");
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    /**
     * Returns the user for the currently authenticated user, if any.
     *
     * @return the user for the currently authenticated user, or an empty optional
     *         if no user is authenticated
     */

    public User getCurrentUser() {

        return userRepository.findByUsername(getCurrentUserLogin().get()).get();
    }
}
