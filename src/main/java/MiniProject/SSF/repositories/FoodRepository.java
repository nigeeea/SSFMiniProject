package MiniProject.SSF.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class FoodRepository {
    
    @Autowired
    //@Qualifier("redislab")
    private RedisTemplate<String,Object> redisTemplate;

    //method 1: this method saves AND retrieves data from Redis
    public String saveIt(String cuisine, String recipeName){

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(cuisine, recipeName);
        
        ValueOperations<String, Object> ops2 = redisTemplate.opsForValue();
        Object zzz = ops2.get(cuisine);

        return zzz.toString();
    }

    //method 2: one more method here to retrieve only
    public String retrieveIt(String key){
        ValueOperations<String,Object> ops = redisTemplate.opsForValue();
        String valueRetrieved = "";
        if(null == ops.get(key)){
            valueRetrieved = "nothing";
        }
        else{
            valueRetrieved = ops.get(key).toString();
        }
        return valueRetrieved;
    }

    //method 3: one more method to save only
    public void justSavingIt(String key, String value){
        ValueOperations<String,Object> ops = redisTemplate.opsForValue();
        ops.set(key, value);
    }

    //method 4: existing username in database
    public String matchPassword(String key){
        ValueOperations<String,Object> ops = redisTemplate.opsForValue();
        String valueRetrieved = "";
        if(null == ops.get(key)){
            valueRetrieved = "nothing";
        }
        else{
            valueRetrieved = ops.get(key).toString();
        }
        return valueRetrieved;
    }
}
