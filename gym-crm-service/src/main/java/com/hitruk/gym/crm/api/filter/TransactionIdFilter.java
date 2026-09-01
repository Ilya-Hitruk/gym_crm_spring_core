package com.hitruk.gym.crm.api.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter implements Filter {

    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }
}
