package org.RaiJin.core.interceptor;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.core.http.ResponseBody;

public interface PostForwardResponseInterceptor {
    void intercept(ResponseBody data, MappingProperties mapping);
}
