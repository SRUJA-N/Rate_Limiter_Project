package RateLimter.example.RateLimter.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class RateLimiterService {


    @Value("${rate.limiter.capacity}")
    private int capacity;

    @Value("${rate.limiter.refill-rate}")
    private int refill;

    @Value("${rate.limiter.ttl}")
    private int ttl;
    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> defaultRedisScript;
    public RateLimiterService(StringRedisTemplate stringRedisTemplate, DefaultRedisScript<Long> defaultRedisScript) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.defaultRedisScript = defaultRedisScript;
    }








    public boolean access(String userId) {

        String tokenKey = "rate-limiter:" + userId + ":tokens";
        String refillKey = "rate-limiter:" + userId + ":last-refill";

        Long result=stringRedisTemplate.execute(
                defaultRedisScript,
                List.of(tokenKey,refillKey),
                String.valueOf(capacity),
                String.valueOf(refill),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(ttl)
        );
        return result!=null && result==1;

    }
}
