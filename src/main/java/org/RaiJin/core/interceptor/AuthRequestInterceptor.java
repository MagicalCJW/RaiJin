package org.RaiJin.core.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.RaiJin.common.auth.AuthConstant;
import org.RaiJin.common.auth.Sessions;
import org.RaiJin.common.crypto.Sign;
import org.RaiJin.common.env.EnvConfig;
import org.RaiJin.common.services.SecurityConstant;
import org.RaiJin.common.services.Service;
import org.RaiJin.common.services.ServiceDirectory;
import org.RaiJin.config.MappingProperties;
import org.RaiJin.core.http.RequestBody;
import org.RaiJin.exception.RaiJinException;
import org.RaiJin.exception.RaiJinNoAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class AuthRequestInterceptor implements PreForwardRequestInterceptor{

    private static final Logger log = LoggerFactory.getLogger(AuthRequestInterceptor.class);

    private final String signingSecret;
    private final EnvConfig envConfig;

    //Use a map for constant time lookups. Value doesn't matter
    //Hypothetically these should be universally unique, so we don't have to limit by env
    private final Map<String,String> bannedUsers = new HashMap<>() {
        {
            put("d7b9dbed-9719-4856-5f19-23da2d0e3dec", "hidden");
        }
    };

    public AuthRequestInterceptor(String signingSecret, EnvConfig envConfig){
        this.signingSecret = signingSecret;
        this.envConfig = envConfig;
    }

    @Override
    public void intercept(RequestBody data, MappingProperties mapping) {
        // sanitize incoming requests and set authorization information
        String authorization = this.setAuthHeader(data,mapping);

        this.validateRestrict(mapping);
        this.validateSecurity(data, mapping, authorization);
    }

    private void validateSecurity(RequestBody data, MappingProperties mapping, String authorization) {
        // check perimeter authorization
        if(AuthConstant.AUTHORIZATION_ANONYMOUS_WEB.equals(authorization)) {
            Service service = this.getService(mapping);
            if(SecurityConstant.SEC_PUBLIC != service.getSecurity()) {
                log.info("Anonymous user want to access secure service, redirect to login");
                // send to login
                String scheme = "https";
                if(envConfig.isDebug())
                    scheme = "http";
                int port = data.getOriginReq().getServerPort();
                try {
                    URI redirectUrl = new URI(scheme,null,"www"+envConfig.getExternalApex(),
                            port,"/login/",null,null);
                    String returnTo = data.getHost()+data.getUri();
                    String fullRedirectUrl = redirectUrl +"?return_to="+returnTo;

                    data.setNeedRedirect(true);
                    data.setRedirectUrl(fullRedirectUrl);
                } catch (URISyntaxException e) {
                    log.error("fail to build redirect url",e);
                }
            }
        }
    }

    private void validateRestrict(MappingProperties mapping) {
        Service service = getService(mapping);
        if(service.isRestrictDev()&&!envConfig.isDebug()) {
            throw new RaiJinException("This service is restrict to dev and test env only");
        }
    }

    private Service getService(MappingProperties mapping) {
        String host = mapping.getHost();
        String subDomain = host.replace("."+envConfig.getExternalApex(),"");
        Service service = ServiceDirectory.getMapping().get(subDomain.toLowerCase());
        if(service == null) {
            throw new RaiJinException("Unsupported sub-domain "+ subDomain);
        }
        return service;
    }

    private String setAuthHeader(RequestBody data,MappingProperties mapping) {
        //default to anonymous web when prove otherwise
        String authorization = AuthConstant.AUTHORIZATION_ANONYMOUS_WEB;
        HttpHeaders headers = data.getHeaders();
        Session session = this.getSession(data.getOriginReq());
        if(session != null) {
            if(session.isSupport()) {
                authorization = AuthConstant.AUTHORIZATION_SUPPORT_USER;
            } else {
                authorization = AuthConstant.AUTHORIZATION_AUTHENTICATED_USER;
            }
            this.checkBannedUsers(session.getUserId());
            headers.set(AuthConstant.CURRENT_USER_HEADER,session.getUserId());
        } else {
            headers.remove(AuthConstant.CURRENT_USER_HEADER);
        }

        headers.set(AuthConstant.AUTHORIZATION_HEADER, authorization);

        return authorization;
    }

    private void checkBannedUsers(String userId) {
        if(bannedUsers.containsKey(userId)) {
            log.warn(String.format("Banned user accessing service - user %s",userId));
            throw new RaiJinNoAuthException("Banned user accessing");
        }
    }

    private Session getSession(HttpServletRequest request) {
        String token = Sessions.getToken(request);
        if(token == null)
            return null;
        try {
            DecodedJWT decodedJWT = Sign.verifySessionToken(token,signingSecret);
            String userId = decodedJWT.getClaim(Sign.CLAIM_USER_ID).asString();
            boolean support = decodedJWT.getClaim(Sign.CLAIM_SUPPORT).asBoolean();
            return Session.builder().userId(userId).support(support).build();
        } catch (Exception e) {
            log.error("fail to verify token {},{}",token,e);
            return null;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Session {
        private String userId;
        private boolean support;
    }
}
