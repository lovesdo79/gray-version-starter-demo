package com.bytenew.gray.publish.dubbo.router;

import com.bytenew.gray.publish.common.GrayPublishDynamicProperties;
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

    private GrayPublishDynamicProperties dynamicProperties;

    public void setDynamicProperties(GrayPublishDynamicProperties properties) {
        this.dynamicProperties = properties;
    }

    @Override
    protected Router createRouter(URL url) {
        return new GrayTagRouter(url, dynamicProperties);
    }
}
