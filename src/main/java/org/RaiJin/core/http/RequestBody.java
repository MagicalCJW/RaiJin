package org.RaiJin.core.http;

import org.RaiJin.core.util.BodyConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

public class RequestBody extends OriginReqBody{

    private boolean needRedirect;

    private String redirectUrl;

    public RequestBody(HttpMethod method,
                       String host,
                       String uri,
                       HttpHeaders headers,
                       byte[] body,
                       HttpServletRequest request) {
        super(method, host, uri, headers, body, request);
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(String body) {
        this.body = BodyConverter.convertStringToBody(body);
    }

    public void setNeedRedirect(boolean needRedirect) {
        this.needRedirect = needRedirect;
    }

    public boolean isNeedRedirect() {
        return this.needRedirect;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }
}
