package org.example.app.components;

import java.util.List;

import org.example.app.services.JjwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager{
    private JjwtTokenProvider tokenProvider;
    public JwtAuthenticationManager(JjwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
       //String token = authentication.getCredentials().toString();
       //String user = authentication.getName();
       //UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(user, "");
       //return Mono.just(token);
      //return Mono.error(new Throwable("invalid user!"));
      return Mono.justOrEmpty(authentication.getCredentials())
                 .map(Object::toString)
                 .flatMap(token->{
                    try{
                        if(tokenProvider.validateToken(token)){
                            String username = tokenProvider.getUserNameFromToken(token);
                            //赋予默认权限
                            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_Admin"));
                            UsernamePasswordAuthenticationToken authenticatedToken = new UsernamePasswordAuthenticationToken(username, token, authorities);
                            return Mono.just(authenticatedToken);
                        }else{
                            return Mono.error(new BadCredentialsException("Invalid or expired JWT token"));
                        }
                    }catch(JwtException exp){
                        return Mono.error(new BadCredentialsException("Invalid JWT Token: " + exp.getMessage(),exp));
                    }
                 });
    }
    
}
