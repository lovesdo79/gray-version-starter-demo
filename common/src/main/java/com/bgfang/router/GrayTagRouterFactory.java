package com.bgfang.router;

import com.bgfang.init.GrayVersionDynamicProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.CacheableRouterFactory;
import org.apache.dubbo.rpc.cluster.Router;

/**
 * 灰度标签路由工厂
 *
 * @author yagushou
 */
@Activate(order = 100)
public class GrayTagRouterFactory extends CacheableRouterFactory {

    private GrayVersionDynamicProperties dynamicProperties;

    public void setDynamicProperties(GrayVersionDynamicProperties properties) {
        this.dynamicProperties = properties;
    }

    @Override
    protected Router createRouter(URL url) {
        return new GrayTagRouter(url, dynamicProperties);
    }
}
