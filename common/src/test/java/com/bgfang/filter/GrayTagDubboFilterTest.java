package com.bgfang.filter;

import com.bgfang.constants.CommonConstant;
import org.apache.dubbo.rpc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GrayTagDubboFilterTest {

    private GrayTagDubboFilter grayTagDubboFilter;

    @Mock
    private Invoker<?> invoker;

    @Mock
    private Invocation invocation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        grayTagDubboFilter = new GrayTagDubboFilter();
    }

    @Test
    void testInvokeConsumerSide() throws RpcException {
        // Mocking the RpcContext
        RpcContext context = mock(RpcContext.class);
        when(context.isConsumerSide()).thenReturn(true);
        when(RpcContext.getContext()).thenReturn(context);

        // Stubbing invoker.invoke()
        when(invoker.invoke(invocation)).thenReturn(mock(Result.class));

        ReflectionTestUtils.setField(grayTagDubboFilter, "SERVICE_CHAIN", "mockServiceChain");
        ReflectionTestUtils.setField(grayTagDubboFilter, "COMPANY_ID", "mockCompanyId");

        Result result = grayTagDubboFilter.invoke(invoker, invocation);

        verify(invoker).invoke(invocation);
        verify(context).setAttachment(CommonConstant.SERVICE_CHAIN, "mockServiceChain");
        verify(context).setAttachment(CommonConstant.COMPANY_ID, "mockCompanyId");
    }

    @Test
    void testInvokeProviderSide() throws RpcException {
        // Mocking the RpcContext
        RpcContext context = mock(RpcContext.class);
        when(context.isProviderSide()).thenReturn(true);
        when(context.getAttachment(CommonConstant.SERVICE_CHAIN)).thenReturn("providerSideChain");
        when(context.getAttachment(CommonConstant.COMPANY_ID)).thenReturn("providerSideCompanyId");
        when(RpcContext.getContext()).thenReturn(context);

        // Stubbing invoker.invoke()
        when(invoker.invoke(invocation)).thenReturn(mock(Result.class));

        grayTagDubboFilter.invoke(invoker, invocation);

        verify(invoker).invoke(invocation);
        verify(CommonConstant.SERVER_CHAIN_HOLDER).set("providerSideChain");
        verify(CommonConstant.COMPANY_ID_HOLDER).set("providerSideCompanyId");
    }

    @Test
    void testInvokeException() throws RpcException {
        // Mocking the RpcContext
        RpcContext context = mock(RpcContext.class);
        when(RpcContext.getContext()).thenReturn(context);

        // Stubbing invoker.invoke() to throw exception
        when(invoker.invoke(invocation)).thenThrow(new RpcException("Test exception"));

        assertThrows(RpcException.class, () -> grayTagDubboFilter.invoke(invoker, invocation));

        // Since we are not testing the cleanup logic here, we skip those verifications.
    }
}
