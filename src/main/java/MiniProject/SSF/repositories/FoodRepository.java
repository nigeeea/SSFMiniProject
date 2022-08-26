package MiniProject.SSF.repositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class FoodRepository {
    
    @Autowired
    //@Qualifier("redislab")
    private RedisTemplate<String,Object> redisTemplate;

    public String saveIt(String cuisine, String recipeName){

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(cuisine, recipeName);
        
        ValueOperations<String, Object> ops2 = redisTemplate.opsForValue();
        Object zzz = ops2.get(cuisine);

        return zzz.toString();
    }
}
