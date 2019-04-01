package com.fyyzi.temp;

import com.fyyzi.TestBase;
import com.fyyzi.service.DistributedLocker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Test00 extends TestBase {

    @Autowired
    private DistributedLocker distributedLocker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test004(){

        String key = "CAMELOT_DISTRIBUTED_LOCK:123";

        boolean a = (boolean)redisTemplate.execute((RedisConnection connection) -> {
            StringRedisSerializer serializer = new StringRedisSerializer();
            byte[] serialize = serializer.serialize(key);
            Long result = connection.del(serialize);
            connection.close();
            return result != null && result.intValue() == 1;
        });

        System.out.println(a);
    }

    @Test
    public void test003(){
        Boolean delete = redisTemplate.delete("CAMELOT_DISTRIBUTED_LOCK:123");
        System.out.println(delete);
    }

    @Test
    public void demo(){
        String lock = distributedLocker.lock("123", () -> {
            return invoke();
        });
        System.out.println(lock);
    }
    private String invoke(){
        return "";
    }

    @Test
    public void test001() {
        LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        long l = now.atZone(zone).toEpochSecond();


        System.currentTimeMillis();

        System.out.println(now);


        String a = (String) null;
        System.out.println(a);
    }

}
