package com.ecommerce.backend.config;

// FIX: was in bare package "config" → never loaded by Spring.
// FIX: original only declared a BCryptPasswordEncoder bean; Spring Security
//      auto-configured a session-based form-login that blocked every API call
//      with a redirect to /login.
// FIX: added stateless SecurityFilterChain, JWT filter, and proper RBAC rules.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
            throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // FIX: disable CSRF – not needed for stateless REST + JWT
            .csrf(AbstractHttpConfigurer::disable)

            // FIX: allow H2-console iframes (remove if switching to MySQL)
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

            // FIX: stateless – no HTTP sessions
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Public – register and login never require a token
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                // H2 console (development only)
                .requestMatchers("/h2-console/**").permitAll()
                // Products – read is public, write requires authentication
                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                // Everything else requires a valid JWT
                .anyRequest().authenticated()
            )

            // FIX: plug in our JWT filter before Spring's username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
