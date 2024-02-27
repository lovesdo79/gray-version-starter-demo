package com.bytenew.gray.publish.servlet.config;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.bytenew.gray.publish.common.GrayPublishDynamicProperties;
import com.bytenew.gray.publish.common.init.GrayTagDiscoveryClientInitializer;
import com.bytenew.gray.publish.common.servlet.GrayTagServletFilter;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.servlet.Filter;
import java.util.Collections;

/**
 * @author 竹熊
 * @date 2024/2/20 10:17
 */
public class GrayPublishServletAutoConfiguration {

    @Bean
    @Conditional(DynamicPropertiesCondition.class)
    @ConditionalOnMissingBean(GrayPublishDynamicProperties.class)
    public GrayPublishDynamicProperties dynamicProperties(NacosConfigProperties nacosConfigProperties) {
        return new GrayPublishDynamicProperties(nacosConfigProperties);
    }


    @Bean
    @Conditional(GrayTagDiscoveryClientInitializerCondition.class)
    @ConditionalOnNacosDiscoveryEnabled
    @ConditionalOnClass({NacosDiscoveryProperties.class})
    public GrayTagDiscoveryClientInitializer grayTagDiscoveryClientInitializer(NacosDiscoveryProperties nacosDiscoveryProperties) {
        return new GrayTagDiscoveryClientInitializer(nacosDiscoveryProperties);
    }


    @Bean
    @Conditional(FilterRegistrationBeanCondition.class)
    @ConditionalOnMissingBean(FilterRegistrationBean.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new GrayTagServletFilter());
        registrationBean.setUrlPatterns(Collections.singletonList("/*"));
        return registrationBean;
    }

    static class DynamicPropertiesCondition implements Condition {

        @Override
        public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
            return Boolean.TRUE;
        }
    }

    static class GrayTagDiscoveryClientInitializerCondition implements Condition {

        @Override
        public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
            return Boolean.TRUE;
        }
    }

    static class FilterRegistrationBeanCondition implements Condition {

        @Override
        public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
            return Boolean.TRUE;
        }
    }
}
