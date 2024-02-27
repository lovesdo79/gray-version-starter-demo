package com.bgfang.filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * @author zhuxiong
 * @date 2024/02/27
 */
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        app.run(args);
    }
}
