package com.bgfang.router;

import com.bgfang.constants.CommonConstant;
import com.bgfang.init.GrayVersionDynamicProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义灰度标签路由
 * - 兼容灰度标签的链路传递逻辑
 * - 兼容公司dubbo版本不一致问题（如2.6和2.7的TagRoute实现区别较大）
 *
 * @author yagushou
 */
public class GrayTagRouter extends AbstractRouter {

    private GrayVersionDynamicProperties properties;

    public void setProperties(GrayVersionDynamicProperties properties) {
        this.properties = properties;
    }

    public GrayTagRouter(URL url, GrayVersionDynamicProperties properties) {
        super(url);
        this.properties = properties;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        String grayTag = getGrayTag(url, invocation);

        if (StringUtils.isBlank(grayTag)) {
            return downgradeInvoke(invokers);
        }

        List<Invoker<T>> filteredInvokers = invokers.stream().filter(invoker -> grayTag.equals(invoker.getUrl().getParameter(CommonConstant.GRAY_TAG)))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(filteredInvokers)) {
            return downgradeInvoke(invokers);
        }

        return filteredInvokers;
    }

    /**
     * 获取灰度环境标识
     *
     * @param url
     * @param invocation
     * @return
     */
    private String getGrayTag(URL url, Invocation invocation) {
        String sc = getServiceChain(invocation);
        String grayTag = null;
        if (StringUtils.isNotBlank(sc)) {
            grayTag = sc;
        } else {
            String companyId = getCompanyId(invocation);
            String application = url.getParameter("remote.application");
            if (StringUtils.isNotBlank(companyId)) {
                grayTag = properties.getGrayTag(Long.valueOf(companyId), application);
            }
        }
        return grayTag;
    }

    private static String getCompanyId(Invocation invocation) {
        String companyId = invocation.getAttachment(CommonConstant.COMPANY_ID);
        if (StringUtils.isBlank(companyId)) {
            companyId = CommonConstant.COMPANY_ID_HOLDER.get();
        }
        return companyId;
    }

    private static String getServiceChain(Invocation invocation) {
        String sc = invocation.getAttachment(CommonConstant.SERVICE_CHAIN);
        if (StringUtils.isBlank(sc)) {
            sc = CommonConstant.SERVER_CHAIN_HOLDER.get();
        }
        return sc;
    }

    /**
     * 筛选出不携带灰度标签的provider
     *
     * @param invokers all provider
     * @return 降级provider
     */
    private <T> List<Invoker<T>> downgradeInvoke(List<Invoker<T>> invokers) {
        return invokers.stream()
                .filter(invoker -> StringUtils.isEmpty(invoker.getUrl().getParameter(CommonConstant.GRAY_TAG)))
                .collect(Collectors.toList());
    }
}
