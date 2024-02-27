package com.bytenew.gray.publish.common.servlet;


import com.bytenew.gray.publish.common.constants.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 灰度标签Servlet过滤器
 *
 * @author yagushou
 */
public class GrayTagServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            String sc = httpServletRequest.getHeader(CommonConstant.SERVICE_CHAIN);
            if (StringUtils.isNotBlank(sc)) {
                CommonConstant.SERVER_CHAIN_HOLDER.set(sc);
            }
            String companyId = httpServletRequest.getHeader(CommonConstant.COMPANY_ID);
            if (StringUtils.isNotBlank(companyId)) {
                CommonConstant.COMPANY_ID_HOLDER.set(companyId);
            }
            chain.doFilter(request, response);
        } finally {
            CommonConstant.COMPANY_ID_HOLDER.remove();
            CommonConstant.SERVER_CHAIN_HOLDER.remove();
        }
    }

    @Override
    public void destroy() {
    }
}
