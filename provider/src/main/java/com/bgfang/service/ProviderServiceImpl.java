package com.bgfang.service;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author 竹熊
 * @date 2024/2/21 13:40
 */
@DubboService
public class ProviderServiceImpl implements ProviderService {

    @Value("${env:default}")
    private String env;

    private int count;
    @Override
    public String hallo(String name) {
        count++;
        return "Hello, " + name + ", 当前是Provider服务[env: " + env + "], 这是此接口第" + count + "次被访问!";
    }
}
