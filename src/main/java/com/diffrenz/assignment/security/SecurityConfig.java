package com.diffrenz.assignment.security;

import com.diffrenz.assignment.domain.Role;
import com.diffrenz.assignment.service.CustomLogoutHandler;
import com.diffrenz.assignment.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final UserService userService;

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        private final CustomLogoutHandler logoutHandler;

        /**
         * Constructs a new SecurityConfig instance.
         *
         * @param userService             the user service
         * @param jwtAuthenticationFilter the JWT authentication filter
         * @param logoutHandler           the custom logout handler
         */
        public SecurityConfig(UserService userService,
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        CustomLogoutHandler logoutHandler) {
                this.userService = userService;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.logoutHandler = logoutHandler;
        }

        /**
         * Creates the security filter chain.
         *
         * @return the security filter chain
         * @throws Exception if an error occurs
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(
                                                req -> req.requestMatchers("/api/authenticate")
                                                                .permitAll()
                                                                .requestMatchers("/api/**")
                                                                .hasAnyAuthority("USER", "ADMIN")
                                                                .anyRequest()
                                                                .authenticated())
                                .userDetailsService(userService)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(
                                                e -> e.accessDeniedHandler(
                                                                (request, response, accessDeniedException) -> response
                                                                                .setStatus(403))
                                                                .authenticationEntryPoint(new HttpStatusEntryPoint(
                                                                                HttpStatus.UNAUTHORIZED)))
                                .logout(l -> l
                                                .logoutUrl("/logout")
                                                .addLogoutHandler(logoutHandler)
                                                .logoutSuccessHandler((request, response,
                                                                authentication) -> SecurityContextHolder
                                                                                .clearContext()))
                                .build();

        }

        /**
         * Creates a password encoder.
         *
         * @return the password encoder
         */

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * Creates the authentication manager.
         *
         * @param configuration the authentication configuration
         * @return the authentication manager
         * @throws Exception if an error occurs
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

}
