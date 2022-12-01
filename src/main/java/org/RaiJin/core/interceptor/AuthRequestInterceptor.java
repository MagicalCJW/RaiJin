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
import org.RaiJin.config.MappingProperties;
import org.RaiJin.core.http.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
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

    }

    private String setAuthHeader(RequestBody data,MappingProperties mapping) {
        //default to anonymous web when prove otherwise
        String authorization = AuthConstant.AUTHORIZATION_ANONYMOUS_WEB;
        HttpHeaders headers = data.getHeaders();
        Session session = this.getSession(data.getOriginReq());
        if(session != null) {
            if(session.isSupport()) {
                authorization = AuthConstant.
            }
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
