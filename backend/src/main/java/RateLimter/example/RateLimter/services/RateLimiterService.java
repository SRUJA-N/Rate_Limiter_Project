package RateLimter.example.RateLimter.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class RateLimiterService {


    @Value("${rate.limiter.capacity}")
    private int capacity;

    @Value("${rate.limiter.refill-rate}")
    private int refill;

    private final Map<String,TokenBucket> buckets=new HashMap<>();
    public boolean access( String userId){

        TokenBucket bucket=buckets.get(userId);

        if(bucket==null){
            bucket = new TokenBucket(capacity,refill);
            buckets.put(userId,bucket);

        }

        return bucket.allowRefil();

    }
}
