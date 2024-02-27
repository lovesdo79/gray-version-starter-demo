package com.bgfang.constants;

/**
 * 通用常量
 *
 * @author yagushou
 */
public class CommonConstant {

    /**
     * 灰度标签key
     */
    public static final String GRAY_TAG = "gray.tag";

    /**
     * 灰度服务key
     */
    public static final String SERVICE_CHAIN = "serviceChain";

    /**
     * 公司id key
     */
    public static final String COMPANY_ID = "companyId";

    /**
     * 公司id传递器
     */
    public static final ThreadLocal<String> COMPANY_ID_HOLDER = new ThreadLocal<>();

    /**
     * 灰度服务key传递器
     */
    public static final ThreadLocal<String> SERVER_CHAIN_HOLDER = new ThreadLocal<>();

    /**
     * 灰度公司配置文件名
     */
    public static final String DYNAMIC_GRAY_COMPANY_DATA_ID = "dynamic-gray-company.json";

    /**
     * 配置文件读取超时
     */
    public static final long CONFIG_TIMEOUT_MS = 5000;
}
