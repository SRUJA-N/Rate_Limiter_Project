package RateLimter.example.RateLimter.services;

public class TokenBucket {
    private final int capacity;
    private final int refilRate;
    private long lastRefillTime;

    private int avaliableTokens;

    public TokenBucket(int capacity,int refilRate){
        this.capacity=capacity;
        this.refilRate=refilRate;
    }



}
