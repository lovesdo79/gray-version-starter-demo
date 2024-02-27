package com.bytenew.gray.publish.servlet.config;

import com.bytenew.gray.publish.common.GrayPublishDynamicProperties;
import com.bytenew.gray.publish.servlet.loadbalancer.GrayVersionLoadBalancer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@ConditionalOnClass(LoadBalancerAutoConfiguration.class)
@LoadBalancerClients(defaultConfiguration = GrayVersionLoadBalancerClientAutoConfig.class)
@AutoConfigureBefore({LoadBalancerAutoConfiguration.class})
public class GrayVersionLoadBalancerClientAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(LoadBalancerClientFactory.class)
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory,
            GrayPublishDynamicProperties grayVersionDynamicProperties) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new GrayVersionLoadBalancer(name,
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                grayVersionDynamicProperties);
    }

}
