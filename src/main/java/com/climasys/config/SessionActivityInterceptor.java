package com.climasys.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to extend session timeout on user activity
 * This ensures that the session doesn't expire while the user is actively using
 * the application
 */
@Component
public class SessionActivityInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SessionActivityInterceptor.class);

    // List of paths that should NOT extend session (like login, logout, public
    // endpoints)
    private static final String[] EXCLUDED_PATHS = {
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/session/logout",
            "/error",
            "/favicon.ico",
            "/swagger-ui",
            "/api-docs"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String path = request.getRequestURI();

            // Allow CORS preflight requests
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            // If path is excluded (public), allow it
            if (!shouldExtendSession(path)) {
                return true;
            }

            HttpSession session = request.getSession(false);

            if (session == null) {
                logger.debug("No active session found for protected path: {}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Session expired or invalid\", \"status\": \"error\"}");
                return false;
            }

            // Session exists and path is protected - extend session
            // Access the session to update last accessed time
            // This will automatically reset the inactivity timeout
            session.getAttribute("userId"); // Just touch the session to update last access

            logger.debug("Session activity detected - last access time updated for session: {}", session.getId());
            return true;

        } catch (Exception e) {
            // Don't let interceptor errors break the request
            logger.error("Error in session activity interceptor: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Check if the session should be extended for this path
     */
    private boolean shouldExtendSession(String path) {
        if (path == null) {
            return false;
        }

        // Exclude certain paths from session extension
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath)) {
                return false;
            }
        }

        return true;
    }
}
