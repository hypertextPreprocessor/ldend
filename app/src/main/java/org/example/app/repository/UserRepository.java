package org.example.app.repository;

import org.example.app.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

import java.util.List;


public interface UserRepository extends ReactiveCrudRepository<User,Long> {
    public Mono<List<User>> findByUsername(String username);
    
}
