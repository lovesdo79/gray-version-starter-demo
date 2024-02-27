package com.bytenew.gray.publish.customer;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class Customer {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Customer.class);
        app.run(args);
    }
}
