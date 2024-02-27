package com.bytenew.gray.publish.dubbo.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.bytenew.gray.publish.common.GrayPublishDynamicProperties;
import com.bytenew.gray.publish.common.servlet.GrayTagServletFilter;
import com.bytenew.gray.publish.dubbo.init.GrayTagDubboConfigBeanCustomizer;
import lombok.NonNull;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.context.config.DubboConfigBeanCustomizer;
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
public class GrayPublishDubboAutoConfig {

    @Bean
    @ConditionalOnMissingBean(GrayPublishDynamicProperties.class)
    @Conditional(DynamicPropertiesCondition.class)
    public GrayPublishDynamicProperties dynamicProperties(NacosConfigProperties nacosConfigProperties) {
        return new GrayPublishDynamicProperties(nacosConfigProperties);
    }

    @Bean
    @Conditional(GrayTagDubboConfigBeanCustomizerCondition.class)
    @ConditionalOnClass({ProviderConfig.class, DubboConfigBeanCustomizer.class})
    public GrayTagDubboConfigBeanCustomizer grayTagDubboConfigBeanCustomizer() {
        return new GrayTagDubboConfigBeanCustomizer();
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

    static class GrayTagDubboConfigBeanCustomizerCondition implements Condition {

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
