package it.gov.pagopa.mocker.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.util.ObjectRedisSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConf {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.pwd}")
    private String redisPwd;

    @Bean
    public ObjectMapper objectMapper() {
        final var objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        redisConfiguration.setPassword(redisPwd);
        LettuceClientConfiguration lettuceConfig = LettuceClientConfiguration.builder().useSsl().build();
        return new LettuceConnectionFactory(redisConfiguration, lettuceConfig);
    }

    @Bean
    @Qualifier("redisTemplate")
    public RedisTemplate<String, ExtractedRequest> redisObjectTemplate(final LettuceConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, ExtractedRequest> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        final var objectRedisSerializer = new ObjectRedisSerializer<ExtractedRequest>();
        template.setValueSerializer(objectRedisSerializer);
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}