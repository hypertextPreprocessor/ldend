package org.example.app.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory, 
            ObjectMapper objectMapper) {
        
        // 1. Key 依然使用标准的 String 序列化器
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // 2. Value 使用基于 tools.jackson.databind.ObjectMapper 的自定义 JSON 序列化器
        RedisSerializer<Object> valueSerializer = new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object t) {
                if (t == null) {
                    return new byte[0];
                }
                try {
                    return objectMapper.writeValueAsBytes(t);
                } catch (Exception e) {
                    throw new RuntimeException("Jackson serialization error", e);
                }
            }

            @Override
            public Object deserialize(byte[] bytes) {
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                try {
                    // 反序列化为通用 Object.class 或其它具体业务类型
                    return objectMapper.readValue(bytes, Object.class);
                } catch (Exception e) {
                    throw new RuntimeException("Jackson deserialization error", e);
                }
            }
        };

        // 3. 构建 RedisSerializationContext
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext(keySerializer)
                .key(keySerializer)
                .string(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        // 4. 返回 ReactiveRedisTemplate 实例
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}