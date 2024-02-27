package com.bytenew.gray.publish.customer.service;

import com.bytenew.gray.publish.common.service.CustomerService;
import com.bytenew.gray.publish.common.service.ProviderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class CustomerServiceImpl implements CustomerService {

    @Value("${env:default}")
    private String env;

    private int hello_count;
    private int hallo_count;


    @DubboReference
    private ProviderService providerService;

    @Override
    public String hello(String name) {
        hello_count++;
        return "Hello, " + name + ", 当前是Customer服务[env: " + env + "], 这是此接口第" + hello_count + "次被访问!";
    }

    @Override
    public List<String> hallo(String name) {
        hallo_count++;
        List<String> results = new ArrayList<>();
        results.add("Hello, " + name + ", 当前是Customer服务[env: " + env + "], 这是此接口第" + hallo_count + "次被访问!");
        results.add(providerService.hallo(name));
        return results;
    }
}
