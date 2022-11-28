package org.RaiJin.core.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OriginReqBody {

    protected HttpMethod method;

    protected String uri;

    protected String host;

    protected HttpHeaders headers;

    protected byte[] body;

    protected HttpServletRequest originReq;

    public OriginReqBody(RequestBody requestBody) {
        this(requestBody.getMethod(),requestBody.getHost(),requestBody.getUri(),requestBody.getHeaders(),requestBody.getBody(),requestBody.getOriginReq());
    }

}
