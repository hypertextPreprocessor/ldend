package org.example.app.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveDelegatingServerAuthenticationConverter implements ServerAuthenticationConverter {
    private final List<ServerAuthenticationConverter> delegates;
    public ReactiveDelegatingServerAuthenticationConverter(ServerAuthenticationConverter... delegates){
        this.delegates = Arrays.asList(delegates);
    }
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange){
        return Mono.justOrEmpty(delegates).flatMap(
            converters->Flux.fromIterable(converters).concatMap(converter->converter.convert(exchange)).next()
        );
    }
}
