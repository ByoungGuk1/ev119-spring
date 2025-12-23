package com.app.ev119.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisRequiredForOAuth2Filter extends OncePerRequestFilter {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${app.frontend.login-url:http://localhost:3000/auth/login}")
    private String frontendLoginUrl;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !(uri != null && uri.startsWith("/oauth2/authorization/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!isRedisAvailable()) {
            log.error("Redis down -> block OAuth2 login. uri={}", request.getRequestURI());

            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontendLoginUrl)
                    .queryParam("error", "REDIS_DOWN")
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRedisAvailable() {
        try {
            stringRedisTemplate.hasKey("health:redis");
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
}
