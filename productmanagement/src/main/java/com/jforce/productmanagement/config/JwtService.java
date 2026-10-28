package com.jforce.productmanagement.config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String email, String role) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + expiration);

        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    // Extract email from a verified token.
    public String extractEmail(String token) {
        return validateToken(token).getSubject();
    }

    // Extract role from a verified token.
    public String extractRole(String token) {
        return validateToken(token)
                .getClaim("role")
                .asString();
    }
}



