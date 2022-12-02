package org.RaiJin.core.interceptor;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.core.http.ResponseBody;

public class NoOpPostForwardResponseInterceptor implements PostForwardResponseInterceptor{
    @Override
    public void intercept(ResponseBody data, MappingProperties mapping) {

    }
}
