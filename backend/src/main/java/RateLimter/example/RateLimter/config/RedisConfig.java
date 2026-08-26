package RateLimter.example.RateLimter.config;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
@Configuration
public class RedisConfig {
    @Bean
    public DefaultRedisScript<List> rateLimterScript(){

        DefaultRedisScript<List> script=new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource("rate-limiter.lua")
        );

        script.setResultType(List.class);

        return script;
    }
}
