package com.bytenew.gray.publish.dubbo.filter;

import com.bytenew.gray.publish.common.constants.CommonConstant;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Objects;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * 灰度标签过滤器（生产端+消费端）
 *
 * @author yagushou
 */
@Activate(group = {PROVIDER, CONSUMER})
public class GrayPublishTagDubboFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext context = RpcContext.getContext();
        boolean setHolder = false;
        if (context.isConsumerSide()) {
            // 调用方向下游传递
            context.setAttachment(CommonConstant.SERVICE_CHAIN, CommonConstant.SERVER_CHAIN_HOLDER.get());
            context.setAttachment(CommonConstant.COMPANY_ID, CommonConstant.COMPANY_ID_HOLDER.get());

        } else if (context.isProviderSide()) {
            // 通过隐式参数获取
            String sc = CommonConstant.SERVER_CHAIN_HOLDER.get();
            if (Objects.isNull(sc)) {
                CommonConstant.SERVER_CHAIN_HOLDER.set(context.getAttachment(CommonConstant.SERVICE_CHAIN));
                setHolder = true;
            }

            String companyId = CommonConstant.COMPANY_ID_HOLDER.get();
            if (Objects.isNull(companyId)) {
                CommonConstant.COMPANY_ID_HOLDER.set(context.getAttachment(CommonConstant.COMPANY_ID));
                setHolder = true;
            }
        }

        try {
            return invoker.invoke(invocation);
        } finally {
            // 一次dubbo调用结束后清空ThreadLocal和RpcContext
            // TODO ThreadLocal的可以清除？一个Http请求内有多次dubbo请求呢？
            if (setHolder) {
                CommonConstant.SERVER_CHAIN_HOLDER.remove();
                CommonConstant.COMPANY_ID_HOLDER.remove();
            }
            RpcContext.getContext().removeAttachment(CommonConstant.SERVICE_CHAIN);
            RpcContext.getContext().removeAttachment(CommonConstant.COMPANY_ID);
        }
    }
}
