package RateLimter.example.RateLimter.controllers;

import RateLimter.example.RateLimter.services.RateLimiterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controllers {


    private final StringRedisTemplate stringRedisTemplate;

    public Controllers(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @GetMapping("/redis-test")
    public String testing(){
        stringRedisTemplate.opsForValue().set("user:127.0.0.1:tokens","5");


        return stringRedisTemplate.opsForValue().get("user:127.0.0.1:tokens");
    }

    @GetMapping("/hello")
    public String print(){
           return "request Accepted";

    }
}
