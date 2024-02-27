package com.bgfang.config;

import com.bgfang.init.GrayVersionDynamicProperties;
import com.bgfang.loadbalancer.GrayVersionLoadBalancer;
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
            GrayVersionDynamicProperties grayVersionDynamicProperties) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new GrayVersionLoadBalancer(name,
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                grayVersionDynamicProperties);
    }

}
