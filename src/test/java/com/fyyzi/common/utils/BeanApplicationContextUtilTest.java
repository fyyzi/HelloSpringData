package com.fyyzi.common.utils;

import com.fyyzi.service.DistributedLocker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BeanApplicationContextUtilTest {

    @Test
    public void getBeanTest() {
        DistributedLocker getbean = BeanApplicationContextUtil.getBean(DistributedLocker.class);
        RedisTemplate redisTemplate = BeanApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);


        System.out.println(getbean);
        System.out.println(redisTemplate);
    }
}
