package com.hitruk.gym.crm.config;

import com.hitruk.gym.crm.security.JwtAuthenticationFilter;
import com.hitruk.gym.crm.security.JwtService;
import com.hitruk.gym.crm.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private static final String HEADER_AUTH = "Authorization";
    private static final String HEADER_CONTENT_TYPE = "Content-type";
    private static final String PREFIX = "Bearer ";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter,
                                           JwtService jwtService,
                                           TokenBlacklistService tokenBlacklistService, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(OPTIONS, "/**").permitAll()
                                .requestMatchers(POST, "/api/v1/trainees", "/api/v1/trainers").permitAll()
                                .requestMatchers("/api/v1/auth/login").permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
                .addFilterBefore(jwtAuthenticationFilter,  UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout
                                .logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(getLogoutHandler(jwtService, tokenBlacklistService))
                                .logoutSuccessHandler((request, response, authentication) ->
                                        response.setStatus(HttpServletResponse.SC_NO_CONTENT))
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of(GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name()));
        configuration.setAllowedHeaders(List.of(HEADER_AUTH, HEADER_CONTENT_TYPE));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @NonNull
    private static LogoutHandler getLogoutHandler(JwtService jwtService, TokenBlacklistService tokenBlacklistService) {
        return (request, response, authentication) -> {
            String header = request.getHeader(HEADER_AUTH);
            if (header != null && header.startsWith(PREFIX)) {
                String token = header.substring(PREFIX.length());

                try {
                    String jti = jwtService.extractJti(token);
                    Instant expiresAt = jwtService.extractExpiration(token).toInstant();
                    tokenBlacklistService.blacklist(jti, expiresAt);
                } catch (Exception ignored) {}
            }
        };
    }
}
