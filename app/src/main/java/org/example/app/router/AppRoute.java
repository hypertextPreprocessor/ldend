package org.example.app.router;

import org.example.app.controller.AuthController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
@Configuration(proxyBeanMethods = false)
@EnableReactiveMethodSecurity(useAuthorizationManager=true)
public class AppRoute {
    
    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(
        ServerRequest request,
        AuthController authController
    ){    
        return route()
                    .POST("/login",accept(MediaType.APPLICATION_JSON),authController::login)
                .build();
    }
}
