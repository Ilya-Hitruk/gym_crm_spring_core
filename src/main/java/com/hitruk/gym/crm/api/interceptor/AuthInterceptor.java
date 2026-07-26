package com.hitruk.gym.crm.api.interceptor;

import com.hitruk.gym.crm.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private static final String USERNAME_HEADER = "X-Username";
    private static final String PASSWORD_HEADER = "X-Password";

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String username = request.getHeader(USERNAME_HEADER);
        String password = request.getHeader(PASSWORD_HEADER);

        if (username == null || password == null || !userService.matchCredentials(username, password)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            return false;
        }
        return true;
    }
}
