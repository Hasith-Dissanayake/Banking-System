package com.bank.app.core.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Logged
@Interceptor
public class LoggingInterceptor {
    @AroundInvoke
    public Object logMethod(InvocationContext ctx) throws Exception {
        long start = System.currentTimeMillis();
        String method = ctx.getMethod().getDeclaringClass().getSimpleName() + "." + ctx.getMethod().getName();
        System.out.println("[LOG] Entering: " + method);
        try {
            Object result = ctx.proceed();
            long time = System.currentTimeMillis() - start;
            System.out.println("[LOG] Exiting: " + method + " (" + time + " ms)");
            return result;
        } catch (Exception e) {
            System.out.println("[LOG] Exception in: " + method + " - " + e.getMessage());
            throw e;
        }
    }
} 