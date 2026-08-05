package org.example.app.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class MyServerAuthenticationConverter implements ServerAuthenticationConverter{
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange){
        //return exchange.getRequest().getHeaders().get("Authorization")
        
    }
}
