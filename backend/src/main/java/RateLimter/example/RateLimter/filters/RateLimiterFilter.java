package RateLimter.example.RateLimter.filters;

import RateLimter.example.RateLimter.services.RateLimitResult;
import RateLimter.example.RateLimter.services.RateLimiterService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class RateLimiterFilter implements Filter {

    private final RateLimiterService rateLimiterService;

    public RateLimiterFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        String userId = httpRequest.getRemoteAddr();

        try {

            RateLimitResult result =
                    rateLimiterService.access(userId);

            httpResponse.setHeader(
                    "X-RateLimit-Remaining",
                    String.valueOf(result.getRemaining())
            );

            if (result.isResult()) {

                chain.doFilter(request, response);

            } else {

                httpResponse.setStatus(429);

                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");

                httpResponse.setHeader(
                        "Retry-After",
                        String.valueOf(result.getRetryAfterSeconds())
                );

                String responseBody =
                        "{"
                                + "\"error\":\"Too many requests\","
                                + "\"status\":429,"
                                + "\"retryAfter\":"
                                + result.getRetryAfterSeconds()
                                + "}";

                httpResponse.getWriter().write(responseBody);
            }

        } catch (Exception e) {

            // Redis is unavailable
            httpResponse.setStatus(503);

            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");

            String responseBody =
                    "{"
                            + "\"error\":\"Rate limiter temporarily unavailable\","
                            + "\"status\":503,"
                            + "\"message\":\"Please try again later\""
                            + "}";

            httpResponse.getWriter().write(responseBody);
        }
    }

}