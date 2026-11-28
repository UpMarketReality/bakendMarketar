package com.upc.webmarketar.security.filters;

import com.upc.webmarketar.security.services.CustomUserDetailsService;
import com.upc.webmarketar.security.util.JwtTokens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokens tokens;
    private final CustomUserDetailsService users;

    public JwtFilter(JwtTokens tokens, CustomUserDetailsService users) {
        this.tokens = tokens;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null) {
            try {
                if (!header.startsWith("Bearer ")) throw new IllegalArgumentException();
                var user = users.loadUserByUsername(tokens.email(header.substring(7)));
                var auth =
                        UsernamePasswordAuthenticationToken.authenticated(
                                user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (io.jsonwebtoken.JwtException
                    | IllegalArgumentException
                    | UsernameNotFoundException e) {
                SecurityContextHolder.clearContext();
                res.setStatus(401);
                res.setContentType("application/json;charset=UTF-8");
                res.getWriter().write("{\"status\":401,\"message\":\"Token inválido o vencido\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
