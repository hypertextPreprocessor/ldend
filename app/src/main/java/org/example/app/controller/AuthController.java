package org.example.app.controller;

import java.util.Map;

import org.example.app.components.JwtAuthenticationManager;
import org.example.app.entity.User;
import org.example.app.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class AuthController {
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServerSecurityContextRepository securityContextRepository;
    AuthController(UserRepository userRepository,PasswordEncoder passwordEncoder, JwtAuthenticationManager jwtAuthenticationManager,ServerSecurityContextRepository securityContextRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.securityContextRepository = securityContextRepository;
    }
    public Mono<ServerResponse> login(ServerRequest request){
        return request.bodyToMono(User.class).flatMap(user->{
            UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), user.getPassword());
            return jwtAuthenticationManager.authenticate(token);
        }).flatMap(authentication->{
            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            return securityContextRepository.save(request.exchange(),context);
        }).then(
            ServerResponse.ok().bodyValue(Map.of("code","200"))
        );
    }
}
