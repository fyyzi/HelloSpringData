package com.fyyzi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * SpringBoot启动类
 *
 * @author 息阳
 */
@EnableJpaRepositories(basePackages = "com.fyyzi.dao")
@EntityScan("com.fyyzi.entity")
@SpringBootApplication
@ServletComponentScan
public class FyyziWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FyyziWebApplication.class);
    }

}
