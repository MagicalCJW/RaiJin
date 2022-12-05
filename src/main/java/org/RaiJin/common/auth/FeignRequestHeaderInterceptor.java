package org.RaiJin.common.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;

public class FeignRequestHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String userId = AuthContext.getUserId();
        if(!StringUtils.isEmpty(userId)) {
            template.header(AuthConstant.CURRENT_USER_HEADER, userId);
        }
    }
}
