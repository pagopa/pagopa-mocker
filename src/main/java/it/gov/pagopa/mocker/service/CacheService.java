package it.gov.pagopa.mocker.service;

import it.gov.pagopa.mocker.model.ExtractedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CacheService {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, ExtractedResponse> template;

    public ExtractedResponse get(String key, String hashKey) {
        return (ExtractedResponse) template.opsForHash().get(key, hashKey);
    }

    public void set(String key, String hashKey, ExtractedResponse hashValue) {
        template.expire(key, 5, TimeUnit.DAYS);
        template.opsForHash().put(key, hashKey, hashValue);
    }
}