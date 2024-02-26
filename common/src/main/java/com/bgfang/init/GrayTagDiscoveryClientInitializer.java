package com.bgfang.init;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.bgfang.constants.CommonConstant;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * 灰度标签服务发现客户端初始化器
 * - 服务启动时在注册中心为服务添加标签元数据
 *
 * @author yagushou
 */
public class GrayTagDiscoveryClientInitializer implements InitializingBean {

    @Value("${env:}")
    private String env;

    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    public GrayTagDiscoveryClientInitializer(NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isBlank(env) || ObjectUtils.isEmpty(nacosDiscoveryProperties)) {
            return;
        }

        if (!nacosDiscoveryProperties.getMetadata().containsKey(CommonConstant.GRAY_TAG)) {
            nacosDiscoveryProperties.getMetadata().put(CommonConstant.GRAY_TAG, env);
        }
    }
}
