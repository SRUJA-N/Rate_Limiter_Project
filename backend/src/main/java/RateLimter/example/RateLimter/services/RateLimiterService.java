package RateLimter.example.RateLimter.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class RateLimiterService {


    @Value("${rate.limiter.capacity}")
    private int capacity;

    @Value("${rate.limiter.refill-rate}")
    private int refill;
    private final StringRedisTemplate stringRedisTemplate;
    public RateLimiterService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }




    private final Map<String,TokenBucket> buckets=new HashMap<>();
    public boolean access(String userId) {

        String tokenKey = "rate-limiter:" + userId + ":tokens";
        String refillKey = "rate-limiter:" + userId + ":last-refill";

        String tokens = stringRedisTemplate.opsForValue().get(tokenKey);

        // First request from this user
        if (tokens == null) {

            stringRedisTemplate.opsForValue()
                    .set(tokenKey, String.valueOf(capacity));

            stringRedisTemplate.opsForValue()
                    .set(refillKey, String.valueOf(System.currentTimeMillis()));

            tokens = String.valueOf(capacity);
        }

        long availableTokens = Long.parseLong(tokens);

        long lastRefill = Long.parseLong(
                stringRedisTemplate.opsForValue().get(refillKey)
        );

        long currentTime = System.currentTimeMillis();

        long timeElapsed = currentTime - lastRefill;

        long secondsPassed = timeElapsed / 1000;

        long tokensToAdd = secondsPassed * refill;

        // Refill
        if (tokensToAdd > 0) {

            long newTokenCount = Math.min(
                    capacity,
                    availableTokens + tokensToAdd
            );

            stringRedisTemplate.opsForValue()
                    .set(tokenKey, String.valueOf(newTokenCount));

            stringRedisTemplate.opsForValue()
                    .set(refillKey, String.valueOf(currentTime));

            availableTokens = newTokenCount;
        }

        // No token available
        if (availableTokens <= 0) {
            return false;
        }

        // Consume one token
        stringRedisTemplate.opsForValue()
                .decrement(tokenKey);

        return true;
    }
}
