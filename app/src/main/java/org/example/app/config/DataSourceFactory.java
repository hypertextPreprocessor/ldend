package org.example.app.config;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;

import io.smallrye.mutiny.Uni;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Configuration
public class DataSourceFactory {
    @Bean
    Mutiny.SessionFactory entityManagerFactory(){
        // EntityManagerFactory emf = Persistence.createEntityManagerFactory("ldend");
        // Mutiny.SessionFactory sessionFactory = emf.unwrap(Mutiny.SessionFactory.class);
        // //Uni<Session> sessionUni = sessionFactory.openSession();
        // //sessionUni.chain(session->session.find(User.class,id))
        // return sessionFactory;

        // Map<String, Object> props = new HashMap<>();
        // //props.put("jakarta.persistence.jdbc.url", "vert.x-reactive:postgresql://127.0.0.1/ldend");
        // props.put("jakarta.persistence.jdbc.url", "postgresql://127.0.0.1/ldend");
        // props.put("jakarta.persistence.jdbc.user", "youyou");
        // props.put("jakarta.persistence.jdbc.password", "ninia@0210");
        // props.put("hibernate.connection.provider_class", "org.hibernate.reactive.pool.impl.DefaultSqlClientPool");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ldend");
        return emf.unwrap(Mutiny.SessionFactory.class);
    }
}
