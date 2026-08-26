package RateLimter.example.RateLimter.filters;

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

        HttpServletRequest httpRequest=(HttpServletRequest) request;
        HttpServletResponse httpResponse=(HttpServletResponse) response;
        String userId = httpRequest.getRemoteAddr();
        if (rateLimiterService.access(userId)) {

            chain.doFilter(request, response);

        } else {

            httpResponse.setStatus(429);
            httpResponse.setHeader("Retry-After","1");
            response.getWriter().write(  "{\"error\":\"Too many requests\"}");
        }
    }
}