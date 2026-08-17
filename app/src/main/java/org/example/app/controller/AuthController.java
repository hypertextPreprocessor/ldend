package org.example.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.app.components.JwtAuthenticationManager;
import org.example.app.entity.User;
import org.example.app.repository.UserRepository;
import org.example.app.services.JjwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.smallrye.mutiny.Uni;
import reactor.core.publisher.Mono;
/*
登录接口：
1.核对用户的用户名和密码
2.生成 JWT
*/
@Component
public class AuthController {
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JjwtTokenProvider jjwtTokenProvider;
    private final ServerSecurityContextRepository securityContextRepository;
    AuthController(UserRepository userRepository,PasswordEncoder passwordEncoder, JwtAuthenticationManager jwtAuthenticationManager,ServerSecurityContextRepository securityContextRepository,JjwtTokenProvider jjwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.jjwtTokenProvider = jjwtTokenProvider;
    }
    public Mono<ServerResponse> login(ServerRequest request){
        return request.bodyToMono(User.class)
            .switchIfEmpty(Mono.error(new BadCredentialsException("Request body is empty!")))
            .flatMap(user->
                userRepository.findByUsername(user.getUsername())
                    .switchIfEmpty(Mono.error(new BadCredentialsException("user does not exist!")))
                    .flatMap(dbUser->{
                        if(passwordEncoder.matches(user.getPassword(), dbUser.getPassword())){ //这里用户名昵称必须唯一;
                            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_Admin"));
                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                dbUser.getUsername(), 
                                null, 
                                authorities
                            );
                            return Mono.just(authentication);
                        }else{
                            return Mono.error(new BadCredentialsException("password or username error!"));
                        }
                    })
                    .flatMap(authentication->{
                        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
                        context.setAuthentication(authentication);
                        return securityContextRepository.save(request.exchange(),context).thenReturn(authentication);
                    })
                    .flatMap(authentication->{
                        String username = authentication.getName();
                        String token = jjwtTokenProvider.generateToken(username,"admin");
                        Map<String, Object> responseBody = Map.of(
                            "code", 200,
                            "message", "Login Success with WebSession & JWT",
                            "data", token
                        );
                        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(responseBody);
                    })
        ).onErrorResume(e -> {
            Map<String, Object> errorBody = Map.of(
                "code", 401,
                "message", e.getMessage()
            );
            return ServerResponse.status(401).contentType(MediaType.APPLICATION_JSON).bodyValue(errorBody);
        });
    }
}
