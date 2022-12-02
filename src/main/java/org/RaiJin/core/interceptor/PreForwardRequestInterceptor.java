package org.RaiJin.core.interceptor;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.core.http.RequestBody;

public interface PreForwardRequestInterceptor {
    void intercept(RequestBody data, MappingProperties mapping);
}
