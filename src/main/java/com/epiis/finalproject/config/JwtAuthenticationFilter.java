package com.epiis.finalproject.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(jwt)) {
                String role = jwtService.extractRole(jwt);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                
                if (role != null && !role.isBlank()) {
                    String cleanRole = role.replace("ROLE_", "").trim().toUpperCase();
                    if ("PROFESOR".equals(cleanRole) || "PROFESSOR".equals(cleanRole) || "DOCENTE".equals(cleanRole)) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_PROFESOR"));
                    } else if ("ADMIN".equals(cleanRole) || "ADMINISTRADOR".equals(cleanRole)) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
                    } else if ("ESTUDIANTE".equals(cleanRole) || "STUDENT".equals(cleanRole)) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
                    } else {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + cleanRole));
                    }
                } else {
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
