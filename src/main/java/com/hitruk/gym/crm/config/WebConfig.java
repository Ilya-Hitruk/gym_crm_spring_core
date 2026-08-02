package com.hitruk.gym.crm.config;

import com.hitruk.gym.crm.api.interceptor.AuthInterceptor;
import com.hitruk.gym.crm.api.interceptor.RestCallLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final RestCallLoggingInterceptor restCallLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restCallLoggingInterceptor);
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns(
                        "/api/v1/trainees",
                        "/api/v1/trainers",
                        "/api/v1/users/login"
                );
    }
}
