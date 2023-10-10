package it.gov.pagopa.mocker.service;

import it.gov.pagopa.mocker.model.ExtractedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CacheService {

    @Autowired
    @Qualifier("extractedRequest")
    private RedisTemplate<String, Object> extractedRequest;

    public boolean healthCheck() {
        boolean isHealthy = true;
        RedisConnectionFactory redisConnectionFactory = extractedRequest.getConnectionFactory();
        if (redisConnectionFactory != null) {
            isHealthy = "PONG".equals(redisConnectionFactory.getConnection().ping());
        }
        return isHealthy;
    }

    public ExtractedResponse get(String key, String hashKey) {
        return (ExtractedResponse) extractedRequest.opsForHash().get(key, hashKey);
    }

    public void set(String key, String hashKey, ExtractedResponse hashValue) {
        extractedRequest.expire(key, 5, TimeUnit.DAYS);
        extractedRequest.opsForHash().put(key, hashKey, hashValue);
    }
}