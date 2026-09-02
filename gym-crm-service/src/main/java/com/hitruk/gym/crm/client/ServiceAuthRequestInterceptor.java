package com.hitruk.gym.crm.client;

import com.hitruk.gym.crm.security.JwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceAuthRequestInterceptor implements RequestInterceptor {
    private static final String SERVICE_SUBJECT = "gym-crm-service";

    private final JwtService jwtService;

    @Override
    public void apply(RequestTemplate template) {
        String token = jwtService.generateToken(SERVICE_SUBJECT);
        template.header("Authorization", "Bearer " + token);

        String transactionId = MDC.get("transactionId");
        if (transactionId != null) {
            template.header("X-Transaction-Id", transactionId);
        }
    }
}
