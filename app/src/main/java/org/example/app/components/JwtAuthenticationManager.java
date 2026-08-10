package org.example.app.components;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager{

    public JwtAuthenticationManager() {
        
    }
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
       //String token = authentication.getCredentials().toString();
       //String user = authentication.getName();
       //UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(user, "");
       //return Mono.just(token);
      //return Mono.error(new Throwable("invalid user!"));
      return Mono.justOrEmpty(authentication.getCredentials()).map(Object::toString)
                 .flatMap(token->{
                    try{
                        
                    }catch(JwtException exp){
                        return Mono.error(new BadCredentialsException("Invalid JWT Token: " + exp.getMessage(),exp));
                    }
                 });
    }
    
}
