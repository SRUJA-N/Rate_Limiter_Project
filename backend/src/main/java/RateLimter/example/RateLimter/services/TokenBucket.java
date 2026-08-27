package RateLimter.example.RateLimter.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class TokenBucket {

    private final int capacity;

    private final int refilRate;
    private long lastRefillTime;

    private int avaliableTokens;

    public TokenBucket(int capacity,int refilRate){
        this.capacity=capacity;
        this.refilRate=refilRate;
    }

    public void refillTime(){
        long currentTime=System.currentTimeMillis();
        long timePassed=currentTime-lastRefillTime;

        int tokenToAdd=((int)((timePassed)/1000))*refilRate;

        if(tokenToAdd>0){
            avaliableTokens=Math.min(capacity,avaliableTokens+tokenToAdd);
        }
        lastRefillTime=currentTime;

    }

    public boolean allowRefil(){
        refillTime();
        if(avaliableTokens>0){
            avaliableTokens=avaliableTokens-1;
            return true;
        }
        return false;
    }



}
