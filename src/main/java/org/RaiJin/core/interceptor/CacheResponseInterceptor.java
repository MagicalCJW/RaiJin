package org.RaiJin.core.interceptor;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.core.http.ResponseBody;
import org.springframework.http.HttpHeaders;

import java.util.List;

public class CacheResponseInterceptor implements PostForwardResponseInterceptor{
    @Override
    public void intercept(ResponseBody data, MappingProperties mapping) {
        HttpHeaders respHeaders = data.getHeaders();
        if(respHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
            List<String> values = respHeaders.get(HttpHeaders.CONTENT_TYPE);
            assert values != null;
            if(values.contains("text/html")) {
                //insert header to prevent caching
                respHeaders.set(HttpHeaders.CACHE_CONTROL,"no-cache");
            }
        }
    }
}
