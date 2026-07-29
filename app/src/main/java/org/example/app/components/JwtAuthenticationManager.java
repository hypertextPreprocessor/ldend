package org.example.app.components;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager{
    private final JwtReactiveAuthenticationManager delegate;
    public JwtAuthenticationManager(ReactiveJwtDecoder jwtDecoder) {
        this.delegate = new JwtReactiveAuthenticationManager(jwtDecoder);
    }
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
       return delegate.authenticate(authentication);
    }
    
}
