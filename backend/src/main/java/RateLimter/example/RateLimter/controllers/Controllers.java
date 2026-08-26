package RateLimter.example.RateLimter.controllers;

import RateLimter.example.RateLimter.services.RateLimiterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class Controllers {

    private final StringRedisTemplate stringRedisTemplate;
    private final RateLimiterService rateLimiterService;

    public Controllers(
            StringRedisTemplate stringRedisTemplate,
            RateLimiterService rateLimiterService
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/redis-test")
    public String testing() {

        stringRedisTemplate.opsForValue()
                .set("user:127.0.0.1:tokens", "5");

        return stringRedisTemplate.opsForValue()
                .get("user:127.0.0.1:tokens");
    }

    @GetMapping("/redis-decrement")
    public String decrement() {

        Long remainingToken = stringRedisTemplate
                .opsForValue()
                .decrement("user:127.0.0.1:tokens");

        return "Remaining tokens: " + remainingToken;
    }

    @GetMapping("/redis-increment")
    public String increment() {

        Long incrementedToken = stringRedisTemplate
                .opsForValue()
                .increment("user:127.0.0.1:tokens");

        return "Increased token: " + incrementedToken;
    }

    @GetMapping("/redis-ttl")
    public String redisttl() {

        stringRedisTemplate.opsForValue().set(
                "user:127.0.0.1:tokens",
                "it will be deleted after",
                10,
                TimeUnit.SECONDS
        );

        return "The Redis key will be deleted in 10 seconds";
    }

    @GetMapping("/hello")
    public String print() {

        if (rateLimiterService.access("127.0.0.1")) {
            return "Request Accepted";
        }

        return "Too many requests";
    }
}