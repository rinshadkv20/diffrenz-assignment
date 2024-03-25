package com.diffrenz.assignment.security;

import com.diffrenz.assignment.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A Spring Security filter that authenticates a user based on a JWT token in
 * the request's authorization header.
 * If the token is valid, the user is authenticated using Spring Security's
 * authentication manager.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;

    }

    /**
     * Attempts to authenticate the user based on the JWT token in the request's
     * authorization header.
     * If the token is valid, the user is authenticated using Spring Security's
     * authentication manager.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if an error occurs
     * @throws IOException      if an error occurs
     */

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userService.loadUserByUsername(username);

            if (jwtService.isValid(token, userDetails)) {
                // If the token is valid, create a new authentication token with the user's
                // details
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Set the authentication details source to the request so that additional
                // details can be added to the token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authenticated user in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("User" + username + " successfully authenticated");
            } else {
                logger.info("User" + username + " failed to authenticate");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            }
        }
        filterChain.doFilter(request, response);

    }
}
