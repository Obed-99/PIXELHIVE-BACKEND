package com.pixelhive.backend.config;

import com.pixelhive.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Guards every /api endpoint: requests must carry "Authorization: Bearer <token>".
// Only login, registration and CORS preflights are open.
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    private boolean isPublic(HttpServletRequest req) {
        String path = req.getRequestURI();
        String method = req.getMethod();
        if ("OPTIONS".equals(method)) return true;                            // CORS preflight
        if (path.equals("/api/auth/login")) return true;                      // login
        if (path.equals("/api/users") && "POST".equals(method)) return true;  // register
        return !path.startsWith("/api/");                                     // non-API paths
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (isPublic(request)) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                jwtService.validate(header.substring(7));
                chain.doFilter(request, response);
                return;
            } catch (JwtException ignored) {
                // fall through to 401
            }
        }

        // The browser needs the CORS header even on a 401, or it hides the error.
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Missing or invalid token\"}");
    }
}
