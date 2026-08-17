package org.example.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class TestController {
    @PostMapping("/test")
    public Mono<Map<String, Object>> postMethodName() {
        
        return Mono.just(Map.of("code", 200));
    }
    
}
