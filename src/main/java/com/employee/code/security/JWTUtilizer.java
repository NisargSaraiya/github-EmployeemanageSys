package com.employee.code.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JWTUtilizer {

    private final String SECRET_KEY_STRING = "592e29a8698b2119078c831ccc63573cffe197cf431a71f013df1d3f802e04bcbf23e1c182c33e9f63ed3a01d4a919004c7299ed195558076353a3f653e423516b033fdc15a2093dbf40d5ba0c726fac" ;
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // Overload generateJWTToken to accept managerId
    public String generateJWTToken(String username, String role, Long managerId){
        Map<String, Object> mp = new HashMap<>();
        mp.put("username", username);
        mp.put("role", role);
        if (managerId != null) mp.put("id", managerId);

        return Jwts.builder()
                .setClaims(mp)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Retain the old method for compatibility, but call the new one with null managerId
    public String generateJWTToken(String username, String role){
        return generateJWTToken(username, role, null);
    }

    public Map<String, String> validateToken(String token) {
        Map<String, String> res = new HashMap<>();
        try {
            Claims c = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            res.put("username", c.get("username", String.class));
            res.put("role", c.get("role", String.class));
            // Add managerId if present
            Object idObj = c.get("id");
            if (idObj != null) {
                res.put("id", String.valueOf(idObj));
            }
            res.put("code", "200");

        } catch (ExpiredJwtException e) {
            res.put("code", "401");
            res.put("error", "token expired:Please login again");

        }
        return res;
    }
}
