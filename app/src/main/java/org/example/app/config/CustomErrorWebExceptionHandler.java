package org.example.app.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-2) // 确保优先级高于 Spring Boot 默认的错误处理器
public class CustomErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    // 传入 WebProperties 并将其资源属性传递给父类
    public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                          WebProperties webProperties,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        // 设置支持的编解码器
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        // 针对所有错误路由到自定义处理方法
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        // 获取错误属性（包含状态码、异常信息等）
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        
        // 获取 HTTP 状态码（默认为 500）
        int status = (int) errorPropertiesMap.getOrDefault("status", 500);
        HttpStatus httpStatus = HttpStatus.valueOf(status);

        // 如果是 404 错误，返回自定义的 404 页面
        if (httpStatus == HttpStatus.NOT_FOUND) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body(BodyInserters.fromResource(new ClassPathResource("templates/login.html")));
        }
        // if (httpStatus == HttpStatus.NOT_FOUND) {
        //     return ServerResponse.status(HttpStatus.NOT_FOUND)
        //             .contentType(MediaType.TEXT_HTML)
        //             .bodyValue("<html><body><h1>404 - 页面未找到</h1><p>您访问的页面不存在，请检查路径是否正确。</p></body></html>");
        // }

        // 其他错误统一返回通用的错误提示
        return ServerResponse.status(httpStatus)
                    .contentType(MediaType.TEXT_HTML)
                    .bodyValue("<html><body><h1>系统开小差了</h1><p>状态码: " + status + "</p></body></html>");
    }
}