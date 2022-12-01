package org.RaiJin.core.http;

import lombok.AllArgsConstructor;
import org.RaiJin.config.MappingProperties;
import org.RaiJin.config.RaiJinProperties;
import org.RaiJin.core.interceptor.PreForwardRequestInterceptor;
import org.RaiJin.core.mappings.MappingsProvider;
import org.RaiJin.core.trace.ProxyingTraceInterceptor;
import org.RaiJin.exception.RaiJinException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ReverseProxyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ReverseProxyFilter.class);

    protected static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    protected static final String X_FORWARDED_PROTO_HEADER = "X-forwarded-Proto";
    protected static final String X_FORWARDED_HOST_HEADER = "X-Forwarded-Host";
    protected static final String X_FORWARDED_PORT_HEADER = "X-Forwarded-Port";

    protected final RaiJinProperties raiJinProperties;
    protected final RequestDataExtractor extractor;
    protected final MappingsProvider mappingsProvider;
    protected final ReqForwarder reqForwarder;
    protected final ProxyingTraceInterceptor traceInterceptor;
    protected final PreForwardRequestInterceptor preForwardRequestInterceptor;


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String originUri = extractor.extractUri(request);
        String originHost = extractor.extractHost(request);

        log.debug("Incoming Request Method "+request.getMethod()+" host "+originHost+" uri "+originUri);
        HttpHeaders headers = extractor.extractHttpHeaders(request);
        HttpMethod method = extractor.extractHttpMethod(request);

        String traceId = traceInterceptor.generateTraceId();
        traceInterceptor.onRequestReceived(traceId, method,originHost,originUri,headers);

        MappingProperties mapping = mappingsProvider.resolveMapping(originHost, request);
        if(mapping == null) {
            traceInterceptor.onNoMappingFound(traceId, method, originHost,originUri, headers);

            log.debug(String.format("Forwarding %s %s %s -> no mapping found", method, originHost,originUri));

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Unsupported domain");
        } else {
            log.debug(String.format("Forwarding %s %s %s -> %s",method,originHost,originUri,mapping.getDestinations()));
        }

        byte[] body = extractor.extractBody(request);
        this.addForwardHeaders(request,headers);

        RequestBody dataToForward = new RequestBody(method, originHost, originUri, headers,body,request);
        preForwardRequestInterceptor.intercept(dataToForward,mapping);
        if(dataToForward.isNeedRedirect()&&!StringUtils.isBlank(dataToForward.getRedirectUrl())) {
            log.debug(String.format("Redirecting to -> %s",dataToForward.getRedirectUrl()));
            response.sendRedirect(dataToForward.getRedirectUrl());
            return;
        }

        ResponseEntity<byte[]> responseEntity =
                reqForwarder.forwardHttpRequest(dataToForward,traceId, mapping);
        this.processResponse(response, responseEntity);
    }

    protected void addForwardHeaders(HttpServletRequest request, HttpHeaders headers) {
        List<String> forwardedFor = headers.get(X_FORWARDED_FOR_HEADER);
        if(CollectionUtils.isEmpty(forwardedFor)) {
            forwardedFor = new ArrayList<>(1);
        }
        forwardedFor.add(request.getRemoteAddr());
        headers.put(X_FORWARDED_FOR_HEADER,forwardedFor);
        headers.set(X_FORWARDED_PROTO_HEADER,request.getScheme());
        headers.set(X_FORWARDED_HOST_HEADER,request.getServerName());
        headers.set(X_FORWARDED_PORT_HEADER,String.valueOf(request.getServerPort()));
    }

    protected void processResponse(HttpServletResponse response, ResponseEntity<byte[]> responseEntity) {
        response.setStatus(responseEntity.getStatusCode().value());
        responseEntity.getHeaders().forEach((name,values)->
                values.forEach(value-> response.addHeader(name,value)));
        if(responseEntity.getBody() != null) {
            try {
                response.getOutputStream().write(responseEntity.getBody());
            } catch (IOException e) {
                throw new RaiJinException("Error writing body of HTTP response",e);
            }
        }
    }
}
