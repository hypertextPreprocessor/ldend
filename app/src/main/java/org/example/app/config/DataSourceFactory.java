package org.example.app.config;

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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ldend");
        Mutiny.SessionFactory sessionFactory = emf.unwrap(Mutiny.SessionFactory.class);
        //Uni<Session> sessionUni = sessionFactory.openSession();
        //sessionUni.chain(session->session.find(User.class,id))
        return sessionFactory;
    }
}
