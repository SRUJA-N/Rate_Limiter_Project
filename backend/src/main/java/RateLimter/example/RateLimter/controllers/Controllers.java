package RateLimter.example.RateLimter.controllers;

import RateLimter.example.RateLimter.services.RateLimitResult;
import RateLimter.example.RateLimter.services.RateLimiterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class Controllers {




    @GetMapping("/hello")
    public String print() {

        return "request Accepted";
    }
}