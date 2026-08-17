package org.example.app.services;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

@Service
public class JjwtTokenProvider {
    @Value("${app.security.jwt.keystore-path}")
    private String keystorePath;
    @Value("${app.security.jwt.keystore-password}")
    private String keystorePassword;
    @Value("${app.security.jwt.key-alias}")
    private String keyAlias;
    @Value("${app.security.jwt.key-password}")
    private String keyPassword;
    private final ResourceLoader resourceLoader;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private byte[] content="".getBytes(StandardCharsets.UTF_8);
    //private final SecretKey key =   Jwts.SIG.HS256.key().build();
    SignatureAlgorithm alg = Jwts.SIG.ES512;
    KeyPair pair = alg.keyPair().build();

    JjwtTokenProvider(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init(){
        try {
            Resource resource = resourceLoader.getResource(keystorePath);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream inputStream = resource.getInputStream()){
                keyStore.load(inputStream,keystorePassword.toCharArray());
            }
            this.privateKey = (PrivateKey) keyStore.getKey(keyAlias, keystorePassword.toCharArray());
            if(this.privateKey == null){
                throw new IllegalStateException("private key not found for alias: " + keyAlias);
            }
            this.publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
            if(this.publicKey == null){
                throw new IllegalStateException("Public Key/Certificate was not found for alias: " + keyAlias);
            }
        } catch(Exception e){
            throw new RuntimeException("Critial error" + e);
        }
    }
    // public KeyPair loadKeyPair(){
    //     Resource resource = resourceLoader.getResource("classpath:cnf/");
    //     KeyStore keystore = KeyStore.getInstance("PKCS12");
    //     //keystore.load();
        
    // }
    

    @Value("${app.session.expr:2h}")
    private Duration sessionExpr;
    public String createJws(String username){
        
        String jws = Jwts.builder().subject(username).signWith(pair.getPrivate(),this.alg).compact();
        return jws;
    }
    public String generateToken(String username,String content){
        this.content = content.getBytes(StandardCharsets.UTF_8);
        Date expirationDate = Date.from(Instant.now().plus(sessionExpr));
        return Jwts.builder()
                   .subject(username)
                   .issuedAt(new Date())
                   .header()
                   .type("JWT")
                   //.add("exp",sessionExpr)
                   .keyId("")
                   .and()
                   //.content(this.content,"text/plain") //content和claim （subject）互斥 (JWE 和 JWT)
                   .signWith(privateKey)
                   .expiration(expirationDate)
                   .compact();
    }
    public String getUserNameFromToken(String token){
        Claims claims = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
