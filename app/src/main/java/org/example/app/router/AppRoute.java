package org.example.app.router;

import org.example.app.controller.AuthController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
@Configuration(proxyBeanMethods = false)
public class AppRoute {
    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(){
        return route()
                    .GET("/login",accept(MediaType.APPLICATION_JSON),AuthController::login)
                .build();
    }
}
