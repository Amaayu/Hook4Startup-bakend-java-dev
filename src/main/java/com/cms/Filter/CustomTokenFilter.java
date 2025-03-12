package com.cms.Filter;

import com.cms.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class CustomTokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ **Fix: Bypass token validation for `/auth/**` APIs**
        String path = request.getRequestURI();
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response); // ✅ **Allow request**
            return;
        }
        
        String token = null;

        // ✅ **Extract token from cookies**
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("session_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        System.out.println(token);
        if (token != null) {
            Optional<String> username = tokenService.validateToken(token);
            if (username.isPresent()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // ❌ **Unauthorized response (only for secured routes)**
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
