package com.smart.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

    private static final Logger log = LoggerFactory.getLogger(SessionHelper.class);

    public void removeMessageFromSession() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            session.removeAttribute("verificationmessage");
        } catch (RuntimeException ex) {
            log.error("Error while removing message from session: ", ex);
        }
    }

}
