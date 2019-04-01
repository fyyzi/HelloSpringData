package com.fyyzi.web.controller;

import com.fyyzi.dao.UserRepository;
import com.fyyzi.entity.User;
import com.fyyzi.web.view.TestExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import java.util.List;

@Controller
@RequestMapping
public class TestController {

    @GetMapping("/user/{id}/detail")
    @ResponseBody
    public User test(@PathVariable("id") Long id) {
        return new User();
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("test")
    public AbstractXlsxView test001() {
        List<User> all = userRepository.findAll();
        TestExcel testExcel = new TestExcel("123", "223", all);
        return testExcel.getExcel();
    }

}
