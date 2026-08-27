package RateLimter.example.RateLimter.services;

import RateLimter.example.RateLimter.config.RateLimiterProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.*;
import org.slf4j.Logger;

@Service
public class RateLimiterService {


   private final RateLimiterProperties properties;

    public RateLimiterService(RateLimiterProperties properties, StringRedisTemplate stringRedisTemplate, DefaultRedisScript<List> defaultRedisScript) {
        this.properties = properties;
        this.stringRedisTemplate = stringRedisTemplate;
        this.defaultRedisScript = defaultRedisScript;
    }

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<List> defaultRedisScript;
    private static final Logger log =
            LoggerFactory.getLogger(RateLimiterService.class);




    private String buildKey(String userId) {
        return "rate-limiter:" + userId;
    }

        public RateLimitResult access (String userId){
        try {
            String baseKey = buildKey(userId);

            String tokenKey = baseKey + ":tokens";
            String refillKey = baseKey + ":last-refill";

            List<Long> result = stringRedisTemplate.execute(
                    defaultRedisScript,
                    List.of(tokenKey, refillKey),
                    String.valueOf(properties.getCapacity()),
                    String.valueOf(properties.getRefillRate()),
                    String.valueOf(System.currentTimeMillis()),
                    String.valueOf(properties.getTtl())
            );
            log.debug("Lua result for user {} = {}", userId, result);

            if (result == null || result.size() < 3) {
                return new RateLimitResult(false, 0L, 1);
            }
            boolean allowed = result.get(0).longValue() == 1L;
            long remainingToken = result.get(1).longValue();
            long retryAfterSeconds = result.get(2).longValue();

            return new RateLimitResult(allowed, remainingToken, retryAfterSeconds);
        }catch(Exception e){
            log.error("Redis is unavailable for user {}", userId, e);
            throw e;
        }

    }

}
