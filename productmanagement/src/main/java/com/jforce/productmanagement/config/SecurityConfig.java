package com.jforce.productmanagement.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // USER registration and login
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()
// Initial Super Admin registration and login
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/super-admin/register",
                                        "/api/super-admin/login"
                                ).permitAll()
                        // ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/admin/register",
                                "/api/admin/login"
                        ).permitAll()

                        // ADMIN and SUPER_ADMIN can manage categories
                        .requestMatchers("/api/categories/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        // ADMIN and SUPER_ADMIN
                        .requestMatchers("/api/products/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")
                        //user can only access
                                .requestMatchers("/api/cart/**").hasRole("USER")
                        //
                                .requestMatchers("/api/addresses/**").hasRole("USER")
                        .requestMatchers("/api/inventory/**")

                        .hasAnyRole("ADMIN", "SUPER_ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}