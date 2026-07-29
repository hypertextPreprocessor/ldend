package org.example.app.controller;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class AuthController {
    public static Mono<ServerResponse> login(ServerRequest request){
        return ServerResponse.ok().bodyValue(Map.of("code","200"));
    }
}
