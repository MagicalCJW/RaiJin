package org.RaiJin.core.interceptor;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.core.http.RequestBody;
import org.springframework.stereotype.Component;

public class NoOpPreForwardRequestInterceptor implements PreForwardRequestInterceptor{
    @Override
    public void intercept(RequestBody data, MappingProperties mapping) {

    }
}
