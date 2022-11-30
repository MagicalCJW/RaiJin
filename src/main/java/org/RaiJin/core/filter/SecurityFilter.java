package org.RaiJin.core.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    private final EnvConfig envConfig;

    public SecurityFilter(EnvConfig envConfig) {
        this.envConfig = envConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if(!StringUtils.isEmpty(origin)) {
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE");
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Accept, Content-Type, Content-Length, Cookie, Accept-Encoding, X-CSRF-Token, Authorization");
        }

        if("OPTIONS".equals(request.getMethod())) {
            return;
        }

        if(!envConfig.isDebug()) {
            boolean isSecure = request.isSecure();
            if(!isSecure) {
                if("https".equals(request.getHeader("X-Forwarded-Proto"))) {
                    isSecure = true;
                }
            }
            if(!isSecure) {
                log.info("Insecure quest in uat&prod env, redirect to https");
                try {
                    URI redirectUrl = new URI("https", request.getServerName(), request.getRequestURI(),null);
                    response.sendRedirect(redirectUrl.toString());
                } catch (URISyntaxException e) {
                    log.error("fail to build redirect url", e);
                }
                return;
            }

            response.setHeader("Strict-Transport-Security","max-age=315360000;includeSubDomains;preload");
            response.setHeader("X-Frame-Options","DENY");
            response.setHeader("X-XSS-Protection","1;mode=block");
        }
        filterChain.doFilter(request,response);
    }
}
