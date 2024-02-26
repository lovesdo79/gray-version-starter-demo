package com.bgfang.config;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.bgfang.filter.GrayTagServletFilter;
import com.bgfang.init.GrayTagDiscoveryClientInitializer;
import com.bgfang.init.GrayTagDubboConfigBeanCustomizer;
import com.bgfang.init.GrayVersionDynamicProperties;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.context.config.DubboConfigBeanCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.servlet.Filter;

/**
 * @author 竹熊
 * @date 2024/2/20 10:17
 */
public class GrayVersionTagAutoConfig {

    @Bean
    @Conditional(DynamicPropertiesCondition.class)
    public GrayVersionDynamicProperties dynamicProperties(NacosConfigProperties nacosConfigProperties) {
        return new GrayVersionDynamicProperties(nacosConfigProperties);
    }


    @Bean
    @Conditional(GrayTagDiscoveryClientInitializerCondition.class)
    @ConditionalOnNacosDiscoveryEnabled
    @ConditionalOnClass({NacosDiscoveryProperties.class})
    public GrayTagDiscoveryClientInitializer grayTagDiscoveryClientInitializer(NacosDiscoveryProperties nacosDiscoveryProperties) {
        return new GrayTagDiscoveryClientInitializer(nacosDiscoveryProperties);
    }

    @Bean
    @Conditional(GrayTagDubboConfigBeanCustomizerCondition.class)
    @ConditionalOnClass({ProviderConfig.class, DubboConfigBeanCustomizer.class})
    public GrayTagDubboConfigBeanCustomizer grayTagDubboConfigBeanCustomizer() {
        return new GrayTagDubboConfigBeanCustomizer();
    }


    @Bean
    @Conditional(FilterRegistrationBeanCondition.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new GrayTagServletFilter());
        registrationBean.setUrlPatterns(Lists.newArrayList("/*"));
        return registrationBean;
    }

    static class DynamicPropertiesCondition implements Condition {

        @Override
        public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
            return Boolean.TRUE;
        }
    }

    static class GrayTagDubboConfigBeanCustomizerCondition implements Condition {

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
