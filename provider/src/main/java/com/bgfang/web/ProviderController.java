package com.bgfang.web;

import com.bgfang.service.CustomerService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/provider")
public class ProviderController {
    private int hi_count = 0;
    private int hello_count = 0;
    private int hello_count2 = 0;
    private int hallo_count = 0;

    @Value("${env:default}")
    private String env;

    @DubboReference
    private CustomerService customerService;

    @GetMapping("/hi")
    public String hi(String name) {
        hi_count++;
        return "Hello, " + name + ", 当前是Provider-Web服务[env: " + env + "], 这是此接口第" + hi_count + "次被访问!";
    }


    @GetMapping("/hello")
    public List<String> hello(String name) {
        hello_count++;
        List<String> results = new ArrayList<>();
        results.add("Hello, " + name + ", 当前是Provider-Web服务[env: " + env + "], 这是此接口第" + hello_count + "次被访问!");
        results.add(customerService.hello(name));
        return results;
    }


    @GetMapping("/hello2")
    public List<String> hello2(String name) {
        hello_count2++;
        List<String> results = new ArrayList<>();
        results.add("Hello, " + name + ", 当前是Provider-Web服务[env: " + env + "], 这是此接口第" + hello_count2 + "次被访问!");
        results.add(customerService.hello(name));
        results.add(customerService.hello(name));
        return results;
    }


    @GetMapping("/hallo")
    public List<String> hallo(String name) {
        hallo_count++;
        List<String> results = new ArrayList<>();
        results.add("Hello, " + name + ", 当前是Provider-Web服务[env: " + env + "], 这是此接口第" + hallo_count + "次被访问!");
        results.addAll(customerService.hallo(name));
        return results;
    }
}
