package RateLimter.example.RateLimter.services;

import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class RateLimiterService {
    int LIMIT=5;
    Map<String,Integer> count=new HashMap<>();
    public boolean axcess( String userId){
        int currentCount=count.getOrDefault(userId,0);

        if(currentCount>=LIMIT){
            return false;
        }
        count.put(userId,currentCount+1);
        return true;
    }
}
