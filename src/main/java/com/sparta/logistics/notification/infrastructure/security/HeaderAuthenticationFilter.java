package com.sparta.logistics.notification.infrastructure.security;

import com.sparta.logistics.notification.common.security.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userIdStr = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");

        if (StringUtils.hasText(userIdStr) && StringUtils.hasText(userRole)) {
            try {
                UUID userId = UUID.fromString(userIdStr);
                AuthUser authUser = new AuthUser(userId, userRole);

                UserAuthentication authentication = new UserAuthentication(authUser);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IllegalArgumentException e) {
                // X-User-Id가 UUID 형식이 아닌 경우 SecurityContext를 비우고 진행
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}