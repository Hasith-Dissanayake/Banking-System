package com.bank.app.core.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@SecurityLogged
@Interceptor
public class SecurityLoggingInterceptor {
    @AroundInvoke
    public Object logSecurityEvent(InvocationContext ctx) throws Exception {
        System.out.println("[SECURITY] " + ctx.getMethod().getDeclaringClass().getSimpleName() + "." + ctx.getMethod().getName() + " called at " + java.time.LocalDateTime.now());
        return ctx.proceed();
    }
} 