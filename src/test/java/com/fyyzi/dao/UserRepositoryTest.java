package com.fyyzi.dao;

import com.fyyzi.entity.User;
import com.fyyzi.entity.base.animal.primate.People;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByNameAndAgeTest(){
        User user = new User();
        People people = new People();
        people.setBirth(new Date());
        user.setPeople(people);
        user.setUserAge(1);
        User save = userRepository.saveAndFlush(user);
        log.info(user.toString());
        log.info(save.toString());

        List<User> all = userRepository.findAll();
        log.info("长度:{},内容:{}",all.size(),all.toString());
    }
}
