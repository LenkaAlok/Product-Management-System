package com.jforce.productmanagement.config;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jforce.productmanagement.entity.User;
import com.jforce.productmanagement.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {

            DecodedJWT decodedJWT = jwtService.validateToken(token);
            String email = decodedJWT.getSubject();


            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "User not found");
                return;
            }

            String role = user.getRole().getName();
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + role);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            List.of(authority));

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (com.auth0.jwt.exceptions.JWTVerificationException
                 | IllegalArgumentException exception) {

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT token");
            return;
        }

        // Continue processing the request
        filterChain.doFilter(request, response);
    }
}

