package org.example.app.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;
import io.jsonwebtoken.security.SignatureAlgorithm;

@Service
public class JjwtTokenProvider {
    private byte[] content;
    JjwtTokenProvider(String content){
        this.content = content.getBytes(StandardCharsets.UTF_8);
    }
    private final SecretKey key =   Jwts.SIG.HS256.key().build();
    SignatureAlgorithm alg = Jwts.SIG.ES512;
    KeyPair pair = alg.keyPair().build();
    
    

    @Value("${app.session.expr:2h}")
    private Duration sessionExpr;
    public String createJws(String username){
        String jws = Jwts.builder().subject(username).signWith(pair.getPrivate(),this.alg).compact();
        return jws;
    }
    public String generateToken(String username){
         Date expirationDate = Date.from(Instant.now().plus(sessionExpr));
        return Jwts.builder()
                   .subject(username)
                   .header()
                   .type("JWT")
                   .add("exp","")
                   .keyId("")
                   .and()
                   .subject("")
                   .content(this.content,"text/plain")
                   .signWith(key)
                   .expiration(expirationDate)
                   .compact();
    }
    public String getUserNameFromToken(String token){
        Claims claims = Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser().sig().add(key)
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
