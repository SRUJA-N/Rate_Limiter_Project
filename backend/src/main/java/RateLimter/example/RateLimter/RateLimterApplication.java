package RateLimter.example.RateLimter;

import RateLimter.example.RateLimter.config.RateLimiterProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RateLimiterProperties.class)
@SpringBootApplication
public class RateLimterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimterApplication.class, args);
	}

}
