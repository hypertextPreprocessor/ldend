package org.example.app.config;

import org.example.app.components.JwtAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.SessionLimit;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import tools.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisWebSession
public class SecurityConfig {

    private final JwtAuthenticationManager jwtAuthenticationManager;
    public SecurityConfig(JwtAuthenticationManager jwtAuthenticationManager){
        this.jwtAuthenticationManager = jwtAuthenticationManager;
    }
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        //final JwtBearerTokenAuthenticationConverter BearerTokenConverter = new JwtBearerTokenAuthenticationConverter();
        //BearerTokenConverter.setBearerTokenHeaderName("ldend");
        //jwtFilter.setServerAuthenticationConverter(BearerTokenConverter);

        http.authorizeExchange((authorize)->
            authorize.pathMatchers("/resources/**","/signup","/login").permitAll()
                     .pathMatchers("/admin/**").hasRole("ADMIN")
                     //.anyExchange().denyAll()
                     .anyExchange().authenticated()
        )
            .csrf((csrf) -> csrf.disable())
            .httpBasic((httpBasic)->httpBasic.disable())
            .formLogin((formLogin)->formLogin.disable())
            .sessionManagement((sessions)->
                sessions.concurrentSessions((cocurrency)->
                    cocurrency.maximumSessions(SessionLimit.of(3))
                )
            )   
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory();
    }
    // @Bean
    // ReactiveRedisTemplate<String, String> ReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
    //     return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContext.string());
    // }
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        // objectMapper.serializationConfig();
        // objectMapper.registeredModules();
        //objectMapper.registerModules(SecurityJacksonModules.getModules(getClass().getClassLoader()));
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext(new StringRedisSerializer())
                .value(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJacksonJsonRedisSerializer(objectMapper)))
                .build();
                
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }   
    @Bean
    ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }

    // @Bean
    // JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager(){
    //     JwtReactiveAuthenticationManager jwtRam = new JwtReactiveAuthenticationManager(jwtDecoder);
    //     return jwtRam;
    // }
}
//JwtAuthenticationProvider