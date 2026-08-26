package RateLimter.example.RateLimter.services;

public class RateLimitResult {
    private final boolean result;

    public RateLimitResult(boolean result, Long remaining,long retryAfterSeconds) {
        this.result = result;
        this.retryAfterSeconds = retryAfterSeconds;
        this.remaining = remaining;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    private final long retryAfterSeconds;
    public Long getRemaining() {
        return remaining;
    }

    public boolean isResult() {
        return result;
    }

    private final Long remaining;






}
