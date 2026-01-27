package com.climasys.auth.aspect;

import com.climasys.auth.annotation.RefreshSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class SessionRefreshAspect {

    private static final Logger logger = LoggerFactory.getLogger(SessionRefreshAspect.class);

    @org.springframework.beans.factory.annotation.Value("${climasys.auth.session.timeout-seconds:1800}")
    private int sessionTimeoutSeconds;

    @Before("@within(refreshSession) || @annotation(refreshSession)")
    public void refreshSessionTimeout(JoinPoint joinPoint, RefreshSession refreshSession) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpSession session = request.getSession(false); // Don't create if not exists

                if (session != null) {
                    session.setMaxInactiveInterval(sessionTimeoutSeconds);
                    logger.debug("Refreshed session timeout to {} seconds for session: {} due to activity on {}",
                            sessionTimeoutSeconds, session.getId(), joinPoint.getSignature().toShortString());
                }
            }
        } catch (Exception e) {
            logger.error("Error refreshing session timeout in aspect: {}", e.getMessage());
        }
    }
}
