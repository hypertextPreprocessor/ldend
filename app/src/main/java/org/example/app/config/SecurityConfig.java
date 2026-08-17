package org.example.app.config;

import java.util.Map;

import org.example.app.components.JwtAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.SessionLimit;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;

import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

//import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

@Configuration
@EnableRedisWebSession
public class SecurityConfig {
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final JwtAuthenticationManager jwtAuthenticationManager;
    public SecurityConfig(JwtAuthenticationManager jwtAuthenticationManager){
        this.jwtAuthenticationManager = jwtAuthenticationManager;
    }
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,ServerSecurityContextRepository securityContextRepository){
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        //final JwtBearerTokenAuthenticationConverter BearerTokenConverter = new JwtBearerTokenAuthenticationConverter();
        //BearerTokenConverter.setBearerTokenHeaderName("ldend");
        //jwtFilter.setServerAuthenticationConverter(BearerTokenConverter);
        MyServerAuthenticationConverter myConverter = new MyServerAuthenticationConverter();
        ReactiveDelegatingServerAuthenticationConverter converter = new ReactiveDelegatingServerAuthenticationConverter(myConverter);
        jwtFilter.setServerAuthenticationConverter(converter);
        // jwtFilter.setAuthenticationFailureHandler(new ServerAuthenticationFailureHandler(){
        //     @Override
        //     public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange,AuthenticationException exception) {
        //         webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        //         webFilterExchange.getExchange().getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //         Map<String,Object> dataMap = Map.of("code",0,"data",false,"message","Invalid credentials");
        //         byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(dataMap);
        //         DataBuffer data = webFilterExchange.getExchange().getResponse().bufferFactory().wrap(bytes);
        //         webFilterExchange.getExchange().getResponse().writeWith(Mono.just(data));
        //         return Mono.empty();
        //     }
        // });
        http
            .securityContextRepository(securityContextRepository)
            .authorizeExchange((authorize)->
            authorize.pathMatchers("/resources/**","/signup","/login").permitAll()
                     .pathMatchers("/admin/**").hasRole("ADMIN")
                     //.anyExchange().denyAll()
                     .anyExchange().authenticated()
            )
            .csrf((csrf) -> csrf.disable())
            .httpBasic((httpBasic)->httpBasic.disable())
            .formLogin((formLogin)->formLogin.disable())
            .exceptionHandling((exceptions)->
                exceptions.authenticationEntryPoint((exchange, ex) -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    Map<String,Object> dataMap = Map.of("code",401,"message","Authentication Required");
                    try {
                        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(dataMap);
                        DataBuffer data = exchange.getResponse().bufferFactory().wrap(bytes);
                        return exchange.getResponse().writeWith(Mono.just(data));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
            )
            .sessionManagement((sessions)->
                sessions.concurrentSessions((cocurrency)->
                    cocurrency.maximumSessions(SessionLimit.of(3))
                )
            )   
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION); //每次请求都会验证jwt ，可优化为有session时，先验证session，没有session再验证jwt;
        return http.build();
    }
    @Bean
    ServerSecurityContextRepository securityContextRepository(){
        return new WebSessionServerSecurityContextRepository();
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
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory,ObjectMapper objectMapper) {

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