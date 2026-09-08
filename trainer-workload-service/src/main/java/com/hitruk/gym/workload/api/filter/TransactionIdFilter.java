package com.hitruk.gym.workload.api.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter implements Filter {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String HEADER = "X-Transaction-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String incoming = (request instanceof HttpServletRequest httpRequest)
                ? httpRequest.getHeader(HEADER)
                : null;
        String transactionId = (incoming != null && !incoming.isBlank())
                ? incoming
                : UUID.randomUUID().toString();

        try {
            MDC.put(TRANSACTION_ID, transactionId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }
}
