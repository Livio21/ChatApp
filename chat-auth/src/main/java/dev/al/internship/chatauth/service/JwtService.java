package dev.al.internship.chatauth.service;

import dev.al.internship.chatauth.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {


    //Get the values from the application properties
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        SecretKey key;
        return key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        return createToken(claims,user);
    }

    private String createToken(Map<String, Object> claims, User user) {
        String jwt = Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(user.getId()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
        return jwt;

    }

    public Claims parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }



    public String getUsername(String token) {
        return parseToken(token).get("username").toString();
    }

    public Date getExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    public String getUserId(String token) { return parseToken(token).getSubject(); }

    public boolean isTokenExpired (String token) {
        if(getExpiration(token).before(new Date())) {
            return true;
        }
        return false;
    }
    public boolean isTokenValid (String token, User user) {
        if (token.equals(generateToken(user)) && !isTokenExpired(token) ) {
            return true;
        }
        return false;
    }





}
