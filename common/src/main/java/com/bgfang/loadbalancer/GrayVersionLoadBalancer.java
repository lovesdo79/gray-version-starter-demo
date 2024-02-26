package com.bgfang.loadbalancer;

import com.bgfang.constants.CommonConstant;
import com.bgfang.init.GrayVersionDynamicProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于实例版本的负载均衡器
 * - 主要基于实例版本负载均衡
 * - 次要方案是随机算法
 *
 * @author yagushou
 */
@Slf4j
public class GrayVersionLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final String serviceId;
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    private final GrayVersionDynamicProperties properties;

    public GrayVersionLoadBalancer(String serviceId,
                                   ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                   GrayVersionDynamicProperties properties) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.properties = properties;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        HttpHeaders httpHeaders = (HttpHeaders) request.getContext();
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get().next().map(instances -> getServiceInstanceByVersion(instances, httpHeaders));
    }

    private Response<ServiceInstance> getServiceInstanceByVersion(List<ServiceInstance> instances, HttpHeaders headers) {
        if (CollectionUtils.isEmpty(instances)) {
            log.warn("[Load Balance] 服务 {} 没有可用的实例", this.serviceId);
            return new EmptyResponse();
        }

        // 如果没有符合条件的灰度版本，则随机返回非灰度版本（可能有多种灰度版本，需要全部跳过）
        List<ServiceInstance> defaultInstances = instances.stream().filter(instance -> {
            Map<String, String> metadata = instance.getMetadata();
            String targetGrayVersion = MapUtils.getString(metadata, CommonConstant.GRAY_TAG);
            if (StringUtils.isNotBlank(targetGrayVersion)) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }).collect(Collectors.toList());


        // 获取请求的灰度版本，无则随机返回可用实例
        String reqGrayVersion = getGrayVersion(headers);
        if (StringUtils.isBlank(reqGrayVersion)) {
            return new DefaultResponse(randomInstance(defaultInstances));
        }

        // 返回符合请求灰度版本的实例
        List<ServiceInstance> grayInstances = instances.stream().filter(instance -> {
            Map<String, String> metadata = instance.getMetadata();
            String targetGrayVersion = MapUtils.getString(metadata, CommonConstant.GRAY_TAG);
            if (StringUtils.isBlank(targetGrayVersion)) {
                return Boolean.FALSE;
            }
            return StringUtils.equalsIgnoreCase(reqGrayVersion, targetGrayVersion);
        }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(grayInstances)) {
            return new DefaultResponse(randomInstance(grayInstances));
        }


        return new DefaultResponse(randomInstance(defaultInstances));
    }

    private String getGrayVersion(HttpHeaders headers) {
        String reqGrayVersion = null;
        String sc = headers.getFirst(CommonConstant.SERVICE_CHAIN);
        if (StringUtils.isNotBlank(sc)) {
            reqGrayVersion = sc;
        } else {
            String companyId = headers.getFirst(CommonConstant.COMPANY_ID);
            // TODO 根据companyId获取当前应用的灰度版本
            if (companyId != null) {
                reqGrayVersion = properties.getGrayTag(Long.valueOf(companyId), serviceId);
            }
        }
        return reqGrayVersion;
    }


    /**
     * 随机获取一个服务实例
     *
     * @param instances 实例列表
     * @return 服务实例
     */
    public static ServiceInstance randomInstance(List<ServiceInstance> instances) {
        return instances.get(RandomUtils.nextInt(0, instances.size()));
    }
}