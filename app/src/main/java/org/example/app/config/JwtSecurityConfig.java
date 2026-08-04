package org.example.app.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class JwtSecurityConfig {
    @Value("${app.securityKey}")
    private String secretKeyString;

    // @Bean
    // public SecretKey secretKey(){
    //     byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
    //     return new SecretKeySpec(keyBytes, MacAlgorithm.HS256.getName());
    // }
    // // @Bean 没有reacitve的编码器，
    // // ReactiveJwtEncoder 
    // @Bean 
    // ReactiveJwtDecoder reactiveJwtDecoder(SecretKey secretKey){
    //     return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    // }
    // @Bean
    // public JwtEncoder jwtEncoder(SecretKey secretKey){
    //     ImmutableSecret<SecurityContext> secretCnf = new ImmutableSecret<>(secretKey);
    //     return new NimbusJwtEncoder(secretCnf);
    // }
    // @Bean
    // public JwtDecoder jwtDecoder(SecretKey secretKey){
    //     return NimbusJwtDecoder.withSecretKey(secretKey).build();
    // }
    @Bean
    public PasswordEncoder passwordEncoder(){
        //PasswordEncoder encoder = new Argon2Password4jPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
