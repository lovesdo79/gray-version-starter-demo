package com.bytenew.gray.publish.common;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.bytenew.gray.publish.common.constants.CommonConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * 动态配置
 *
 * @author yagushou
 */
@Slf4j
public class GrayPublishDynamicProperties implements InitializingBean {

    public static String GRAY_TAG;

    public static Map<Long, GrayCompanyConfig> companyGrayConfigs;

    private final NacosConfigProperties nacosConfigProperties;

    public GrayPublishDynamicProperties(NacosConfigProperties nacosConfigProperties) {
        this.nacosConfigProperties = nacosConfigProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initGrayTag();
        initAndListenerGrayVersionConfig();
    }

    private void initGrayTag() {
        // 获取灰度标签，用于判断当前服务实例是否是灰度实例
        GrayPublishDynamicProperties.GRAY_TAG = System.getProperty(CommonConstant.GRAY_TAG);
    }

    private void initAndListenerGrayVersionConfig() throws NacosException {
        // 获取灰度公司配置，用于判断具体哪些请求需要灰度
        Properties properties = buildNacosProperties(nacosConfigProperties);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String grayVersionConfig = configService.getConfigAndSignListener(
                CommonConstant.DYNAMIC_GRAY_COMPANY_DATA_ID,
                properties.getProperty("groupId"),
                CommonConstant.CONFIG_TIMEOUT_MS,
                new Listener() {

                    @Override
                    public Executor getExecutor() {
                        return ForkJoinPool.commonPool();
                    }

                    @Override
                    public void receiveConfigInfo(String grayVersionConfig) {
                        parseGrayConfig2Map(grayVersionConfig);
                    }
                });
        parseGrayConfig2Map(grayVersionConfig);
    }

    private static void parseGrayConfig2Map(String grayVersionConfig) {
        if (StringUtils.isBlank(grayVersionConfig)) {
            log.warn("There is no gray version config!");
            return;
        }
        try {
            List<GrayConfig> configs = JSONArray.parseArray(grayVersionConfig).toList(GrayConfig.class);
            companyGrayConfigs = configs.stream().collect(Collectors.toMap(GrayConfig::getCompany, GrayConfig::getConfig, (a, b) -> b));
        } catch (Exception e) {
            log.error("parse gray version config error!", e);
        }
    }

    private Properties buildNacosProperties(NacosConfigProperties nacosConfigProperties) {
        Properties properties = new Properties();
        if (Objects.isNull(nacosConfigProperties)) {
            // 兼容Spring项目的Nacos配置
            properties.put(PropertyKeyConst.SERVER_ADDR, System.getProperty("acm_endpoint"));
            properties.put(PropertyKeyConst.NAMESPACE, System.getProperty("acm_namespace"));
            properties.put(PropertyKeyConst.ACCESS_KEY, System.getProperty("acm_accessKey"));
            properties.put(PropertyKeyConst.SECRET_KEY, System.getProperty("acm_secretKey"));
            properties.put("groupId", System.getProperty("acm_groupId"));
            return properties;
        }
        // SpringCloud项目的Nacos配置
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosConfigProperties.getServerAddr());
        if (StringUtils.isNotBlank(nacosConfigProperties.getNamespace())) {
            properties.put(PropertyKeyConst.NAMESPACE, nacosConfigProperties.getNamespace());
        }
        if (StringUtils.isNotBlank(nacosConfigProperties.getAccessKey())) {
            properties.put(PropertyKeyConst.ACCESS_KEY, nacosConfigProperties.getAccessKey());
        }
        if (StringUtils.isNotBlank(nacosConfigProperties.getSecretKey())) {
            properties.put(PropertyKeyConst.SECRET_KEY, nacosConfigProperties.getSecretKey());
        }
        properties.put("groupId", nacosConfigProperties.getGroup());
        return properties;
    }

    public String getGrayTag(Long company, String application) {
        if(Objects.isNull(companyGrayConfigs)){
            return null;
        }

        GrayCompanyConfig config = companyGrayConfigs.get(company);

        if (Objects.isNull(config)) {
            return null;
        }

        if (GrayConfigType.SINGLE.equals(config.getType())) {
            return config.getEnv();
        }


        for (GraySingleConfig singleConfig : config.getConfigs()) {
            String env = singleConfig.getEnv();

            if (singleConfig.getApplications().contains(application)) {
                return env;
            }
        }

        return null;
    }

    @Data
    public static class GrayConfig {
        private Long company;

        private GrayCompanyConfig config;
    }

    @Data
    public static class GrayCompanyConfig {
        private GrayConfigType type;

        private String env;

        private List<GraySingleConfig> configs;
    }

    @Data
    public static class GraySingleConfig {
        private String env;

        private List<String> applications;
    }

    public enum GrayConfigType {

        /**
         * 所有应用统一配置
         */
        SINGLE("single"),
        /**
         * 分应用多配置
         */
        MULTI("multi"),
        ;

        private String type;

        private void setType(String type) {
            this.type = type;
        }

        private String getType() {
            return this.type;
        }

        GrayConfigType(String type) {
            this.type = type;
        }

    }
}
