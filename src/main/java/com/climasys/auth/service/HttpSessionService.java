package com.climasys.auth.service;

import com.climasys.entity.User;
import com.climasys.entity.Clinic;
import com.climasys.auth.entity.AuthDoctorMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing HTTP session data
 */
@Service
public class HttpSessionService {

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionService.class);

    // Session attribute keys
    private static final String USER_ID = "userId";
    private static final String LOGIN_ID = "loginId";
    private static final String FIRST_NAME = "firstName";
    private static final String LANGUAGE_ID = "languageId";
    private static final String DOCTOR_ID = "doctorId";
    private static final String DOCTOR_NAME = "doctorName";
    private static final String DOCTOR_SPECIALITY = "doctorSpeciality";
    private static final String DOCTOR_QUALIFICATION = "doctorQualification";
    private static final String CLINIC_ID = "clinicId";
    private static final String CLINIC_NAME = "clinicName";
    private static final String CLINIC_ADDRESS = "clinicAddress";
    private static final String CLINIC_PHONE = "clinicPhone";
    private static final String LOGIN_TIME = "loginTime";
    private static final String SESSION_TYPE = "sessionType";

    /**
     * Store user session data in HTTP session
     */
    public void storeUserSession(HttpSession session, User user, AuthDoctorMaster doctor, Clinic clinic) {
        if (session == null) {
            logger.warn("Cannot store session data - HttpSession is null");
            return;
        }

        try {
            // Store user information
            session.setAttribute(USER_ID, user.getId());
            session.setAttribute(LOGIN_ID, user.getLoginId());
            session.setAttribute(FIRST_NAME, user.getFirstName());
            session.setAttribute(LANGUAGE_ID, user.getLanguageId());

            // Store doctor information
            session.setAttribute(DOCTOR_ID, user.getDoctorId());
            if (doctor != null) {
                session.setAttribute(DOCTOR_NAME, buildDoctorName(doctor));
                session.setAttribute(DOCTOR_SPECIALITY, doctor.getSpeciality());
                session.setAttribute(DOCTOR_QUALIFICATION, doctor.getDoctorQual());
            }

            // Store clinic information
            if (clinic != null) {
                session.setAttribute(CLINIC_ID, clinic.getClinicId());
                session.setAttribute(CLINIC_NAME, clinic.getClinicName());
                session.setAttribute(CLINIC_ADDRESS, clinic.getClinicAddress());
                session.setAttribute(CLINIC_PHONE, clinic.getPhoneNo());
            }

            // Store session metadata
            session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
            session.setAttribute(SESSION_TYPE, "user_session");

            logger.info("Session data stored successfully for user: {}", user.getLoginId());
            logger.debug("Session ID: {}, Doctor ID: {}, Clinic ID: {}", 
                        session.getId(), user.getDoctorId(), 
                        clinic != null ? clinic.getClinicId() : "null");

        } catch (Exception e) {
            logger.error("Error storing session data for user: {} - {}", user.getLoginId(), e.getMessage());
        }
    }

    /**
     * Get complete session information
     */
    public Map<String, Object> getSessionInfo(HttpSession session) {
        Map<String, Object> sessionInfo = new HashMap<>();
        
        if (session == null) {
            sessionInfo.put("error", "No active session");
            return sessionInfo;
        }

        try {
            // Get user information
            sessionInfo.put("userId", session.getAttribute(USER_ID));
            sessionInfo.put("loginId", session.getAttribute(LOGIN_ID));
            sessionInfo.put("firstName", session.getAttribute(FIRST_NAME));
            sessionInfo.put("languageId", session.getAttribute(LANGUAGE_ID));

            // Get doctor information
            sessionInfo.put("doctorId", session.getAttribute(DOCTOR_ID));
            sessionInfo.put("doctorName", session.getAttribute(DOCTOR_NAME));
            sessionInfo.put("doctorSpeciality", session.getAttribute(DOCTOR_SPECIALITY));
            sessionInfo.put("doctorQualification", session.getAttribute(DOCTOR_QUALIFICATION));

            // Get clinic information
            sessionInfo.put("clinicId", session.getAttribute(CLINIC_ID));
            sessionInfo.put("clinicName", session.getAttribute(CLINIC_NAME));
            sessionInfo.put("clinicAddress", session.getAttribute(CLINIC_ADDRESS));
            sessionInfo.put("clinicPhone", session.getAttribute(CLINIC_PHONE));

            // Get session metadata
            sessionInfo.put("loginTime", session.getAttribute(LOGIN_TIME));
            sessionInfo.put("sessionType", session.getAttribute(SESSION_TYPE));
            sessionInfo.put("sessionId", session.getId());
            sessionInfo.put("lastAccessedTime", session.getLastAccessedTime());
            sessionInfo.put("maxInactiveInterval", session.getMaxInactiveInterval());

            logger.debug("Session info retrieved for session: {}", session.getId());

        } catch (Exception e) {
            logger.error("Error retrieving session info: {}", e.getMessage());
            sessionInfo.put("error", "Error retrieving session information");
        }

        return sessionInfo;
    }

    /**
     * Get doctor ID from session
     */
    public String getDoctorId(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(DOCTOR_ID);
    }

    /**
     * Get clinic ID from session
     */
    public String getClinicId(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(CLINIC_ID);
    }

    /**
     * Get login ID from session
     */
    public String getLoginId(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(LOGIN_ID);
    }

    /**
     * Get user ID from session
     */
    public Long getUserId(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute(USER_ID);
    }

    /**
     * Check if session is valid (has required attributes)
     */
    public boolean isValidSession(HttpSession session) {
        if (session == null) {
            return false;
        }
        
        // Check if essential session attributes exist
        return session.getAttribute(LOGIN_ID) != null && 
               session.getAttribute(DOCTOR_ID) != null;
    }

    /**
     * Check if session is expired
     */
    public boolean isSessionExpired(HttpSession session) {
        if (session == null) {
            return true;
        }
        
        try {
            // Check if session is still valid
            session.getAttribute(LOGIN_ID);
            return false;
        } catch (IllegalStateException e) {
            // Session is invalidated
            return true;
        }
    }

    /**
     * Clear session data
     */
    public void clearSession(HttpSession session) {
        if (session != null) {
            try {
                session.invalidate();
                logger.info("Session invalidated: {}", session.getId());
            } catch (Exception e) {
                logger.error("Error invalidating session: {}", e.getMessage());
            }
        }
    }

    /**
     * Update session timeout
     */
    public void updateSessionTimeout(HttpSession session, int timeoutInSeconds) {
        if (session != null) {
            session.setMaxInactiveInterval(timeoutInSeconds);
            logger.debug("Session timeout updated to {} seconds for session: {}", 
                        timeoutInSeconds, session.getId());
        }
    }

    /**
     * Build doctor name from doctor entity
     */
    private String buildDoctorName(AuthDoctorMaster doctor) {
        StringBuilder name = new StringBuilder();
        if (doctor.getFirstName() != null) name.append(doctor.getFirstName());
        if (doctor.getMiddleName() != null) name.append(" ").append(doctor.getMiddleName());
        if (doctor.getLastName() != null) name.append(" ").append(doctor.getLastName());
        return name.toString().trim();
    }
}
