package org.example.app.config;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/*
*   任务是从 ServerWebExchange 中提取凭证
    包装成未认证的 Authentication 对象交给 Manager。
    使用 Spring 自带的 UsernamePasswordAuthenticationToken
    以 Token 字符串作为 credentials
*/

public class MyServerAuthenticationConverter implements ServerAuthenticationConverter{
    private static final String BEARER_PREFIX = "LDend ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange){
        //return exchange.getRequest().getHeaders().get("Authorization")
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                   .filter(authHeader->authHeader.startsWith(BEARER_PREFIX))
                   .map(authHeader->authHeader.substring(BEARER_PREFIX.length()).trim())
                   .map(token->new UsernamePasswordAuthenticationToken(token,token));
    }
}
