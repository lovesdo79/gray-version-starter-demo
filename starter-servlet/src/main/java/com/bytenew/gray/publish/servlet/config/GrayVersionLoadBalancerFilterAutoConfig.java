package com.bytenew.gray.publish.servlet.config;

import com.bytenew.gray.publish.servlet.filter.GrayVersionLoadBalancerClientFilter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledGlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;


@ConditionalOnClass(LoadBalancerAutoConfiguration.class)
@AutoConfigureBefore({GatewayReactiveLoadBalancerClientAutoConfiguration.class})
public class GrayVersionLoadBalancerFilterAutoConfig {


    @Bean
    @ConditionalOnBean(LoadBalancerClientFactory.class)
    @ConditionalOnEnabledGlobalFilter
    public ReactiveLoadBalancerClientFilter gatewayLoadBalancerClientFilter(
            LoadBalancerClientFactory clientFactory, LoadBalancerProperties properties) {
        return new GrayVersionLoadBalancerClientFilter(clientFactory, properties);
    }
}
