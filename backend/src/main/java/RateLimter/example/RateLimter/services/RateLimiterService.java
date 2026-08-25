package RateLimter.example.RateLimter.services;

import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class RateLimiterService {
    int LIMIT=5;
    long WINDOWSIZE=10*1000;
    long current=System.currentTimeMillis();

    Map<String,Integer> count=new HashMap<>();
    public boolean axcess( String userId){
         long now=System.currentTimeMillis();
         if(now-current>WINDOWSIZE){
             count.clear();
             current=now;
         }

        int currentCount=count.getOrDefault(userId,0);

        if(currentCount>=LIMIT){
            return false;
        }
        count.put(userId,currentCount+1);
        return true;
    }
}
