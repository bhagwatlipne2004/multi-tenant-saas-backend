package com.bhagwat.saas.security;

import com.bhagwat.saas.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            if (jwtService.isTokenValid(token)) {

                Claims claims = jwtService.extractAllClaims(token);

                String email = claims.getSubject();

                Long tenantId = claims.get("tenantId", Long.class);

                TenantContext.setTenantId(tenantId);

                String roles = claims.get("roles", String.class);

                List<SimpleGrantedAuthority> authorities =
                        Arrays.stream(roles.split(","))
                                .map(role ->
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + role
                                        )
                                )
                                .toList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authorities
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

            // Execute the filter chain EXACTLY ONCE
            filterChain.doFilter(request, response);

        } finally {

            // Clean up ThreadLocal after request completes
            TenantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }
}