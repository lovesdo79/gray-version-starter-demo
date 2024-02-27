package com.bytenew.gray.publish.dubbo.init;

import com.bytenew.gray.publish.common.constants.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.AbstractConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.context.config.DubboConfigBeanCustomizer;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 灰度标签Dubbo配置自定义器
 * - 服务启动时为所有dubbo provider添加标签参数
 *
 * @author yagushou
 */
public class GrayTagDubboConfigBeanCustomizer implements DubboConfigBeanCustomizer {

    @Value("${env:}")
    private String env;

    @Override
    public void customize(String beanName, AbstractConfig dubboConfig) {
        if (dubboConfig instanceof ProviderConfig) {
            providerConfig((ProviderConfig) dubboConfig);
        }
    }

    private void providerConfig(ProviderConfig providerConfig) {
        if (StringUtils.isEmpty(env)) {
            return;
        }
        Map<String, String> parameters = providerConfig.getParameters();
        if (Objects.isNull(parameters)) {
            parameters = new HashMap<>();
            providerConfig.setParameters(parameters);
        }

        if (!parameters.containsKey(CommonConstant.GRAY_TAG)) {
            parameters.put(CommonConstant.GRAY_TAG, env);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
