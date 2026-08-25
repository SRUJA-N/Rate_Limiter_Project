package RateLimter.example.RateLimter.controllers;

import RateLimter.example.RateLimter.services.RateLimiterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controllers {


    private final RateLimiterService rateLimiterService;
    public Controllers(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/hello")
    public String print(){
           return "request Accepted";

    }
}
