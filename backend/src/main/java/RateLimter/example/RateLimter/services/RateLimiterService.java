package RateLimter.example.RateLimter.services;

import RateLimter.example.RateLimter.config.RateLimiterProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.*;
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








    public RateLimitResult access(String userId) {

        String tokenKey = "rate-limiter:" + userId + ":tokens";
        String refillKey = "rate-limiter:" + userId + ":last-refill";

        List<Long> result=stringRedisTemplate.execute(
                defaultRedisScript,
                List.of(tokenKey,refillKey),
                String.valueOf(properties.getCapacity()),
                String.valueOf(properties.getRefillRate()),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(properties.getTtl())
        );
        System.out.println("Lua result = " + result);

        if(result==null || result.size()<3){
            return new RateLimitResult(false,0L,1);
        }
        boolean allowed=result.get(0).longValue()==1L;
        long remainingToken = result.get(1).longValue();
        long retryAfterSeconds = result.get(2).longValue();

        return new RateLimitResult(allowed,remainingToken,retryAfterSeconds);

    }
}
