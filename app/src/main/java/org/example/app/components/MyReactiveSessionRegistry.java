package org.example.app.components;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.session.ReactiveSessionInformation;
import org.springframework.security.core.session.ReactiveSessionRegistry;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MyReactiveSessionRegistry implements ReactiveSessionRegistry {
    private final ReactiveRedisTemplate<String,Object> redis;
    
    private final Duration SESSION_TTL;
    public MyReactiveSessionRegistry(ReactiveRedisTemplate<String,Object> redis,@Value("${app.session.expr:2h}") Duration SESSION_TTL){
        this.redis = redis;
        this.SESSION_TTL = SESSION_TTL;
    }
    private String sessionKey(String sessionId){
        return "security:session:" + sessionId;
    }


    private String userSessionKey(String username){
        return "security:user:sessions:" + username;
    }

    @Override
    public Flux<ReactiveSessionInformation> getAllSessions(Object principal) {
        if(principal == null){
            return Flux.empty();
        }
        String username = principal.toString();
        return redis.opsForSet().members(userSessionKey(username)).cast(String.class).flatMap(this::getSessionInformation);
    }

    @Override
    public Mono<Void> saveSessionInformation(ReactiveSessionInformation information) {
        String sessionId = information.getSessionId();
        String username = information.getPrincipal().toString();
        Mono<Boolean> saveSession = redis.opsForValue().set(sessionKey(username),sessionId);
        Mono<Long> addToUserSet = redis.opsForSet().add(userSessionKey(username),sessionId);
        return Mono.when(saveSession,addToUserSet); //用户索引未设置过期时间
    }

    @Override
    public Mono<ReactiveSessionInformation> getSessionInformation(String sessionId) {
        return redis.opsForValue().get(sessionKey(sessionId)).cast(ReactiveSessionInformation.class);
    }

    @Override
    public Mono<ReactiveSessionInformation> removeSessionInformation(String sessionId) {
        return getSessionInformation(sessionId).flatMap(info->{
            String username = info.getPrincipal().toString();
            Mono<Boolean> deleteSession = redis.opsForValue().delete(sessionKey(sessionId));
            Mono<Long> removeFromSet = redis.opsForSet().remove(userSessionKey(username), sessionId);
            return Mono.when(deleteSession, removeFromSet).thenReturn(info);
        });
    }

    @Override
    public Mono<ReactiveSessionInformation> updateLastAccessTime(String sessionId) {
        return getSessionInformation(sessionId).flatMap(info->{
            info.refreshLastRequest();
            return redis.opsForValue().set(sessionKey(sessionId),info,SESSION_TTL).thenReturn(info);
        });
    }
    
}
