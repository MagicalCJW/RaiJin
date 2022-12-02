package org.RaiJin.core.trace;

import org.RaiJin.config.RaiJinProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ProxyingTraceInterceptor {

    protected final RaiJinProperties raiJinProperties;
    protected final TraceInterceptor traceInterceptor;

    public ProxyingTraceInterceptor(RaiJinProperties raiJinProperties, TraceInterceptor traceInterceptor) {
        this.raiJinProperties = raiJinProperties;
        this.traceInterceptor = traceInterceptor;
    }

    public String generateTraceId() {
        return raiJinProperties.getTracing().isEnableTrace()? UUID.randomUUID().toString():null;
    }

    public void onRequestReceived(String traceId, HttpMethod method, String host, String uri, HttpHeaders headers) {
        runIfTracingIsEnabled(()-> {
            IncomingRequest request =getIncomingRequest(method,host,uri,headers);
            traceInterceptor.onRequestReceived(traceId, request);
        });
    }

    public void onNoMappingFound(String traceId, HttpMethod method, String host,String uri, HttpHeaders headers) {
        runIfTracingIsEnabled(()->{
            IncomingRequest request = getIncomingRequest(method, host,uri,headers);
            traceInterceptor.onNoMappingFound(traceId,request);
        });
    }

    public void onForwardStart(String traceId, String mappingName, HttpMethod method, String host, String uri,byte[] body, HttpHeaders headers) {
        runIfTracingIsEnabled(()->{
            ForwardRequest request = new ForwardRequest();
            request.setMappingName(mappingName);
            request.setMethod(method);
            request.setHost(host);
            request.setUri(uri);
            request.setBody(body);
            request.setHeaders(headers);
            traceInterceptor.onForwardStart(traceId, request);
        });
    }

    public void onForwardFail(String traceId, Throwable error) {
        runIfTracingIsEnabled(()-> traceInterceptor.onForwardError(traceId,error));
    }

    public void onForwardComplete(String traceId, HttpStatus status, byte[] body, HttpHeaders headers) {
        runIfTracingIsEnabled(()->{
            ReceivedResponse response = new ReceivedResponse();
            response.setStatus(status);
            response.setBody(body);
            response.setHeaders(headers);
            traceInterceptor.onForwardComplete(traceId, response);
        });
    }

    private IncomingRequest getIncomingRequest(HttpMethod method, String host,String uri,HttpHeaders headers) {
        IncomingRequest request = new IncomingRequest();
        request.setMethod(method);
        request.setHost(host);
        request.setUri(uri);
        request.setHeaders(headers);
        return request;
    }

    protected void runIfTracingIsEnabled(Runnable operation) {
        if(raiJinProperties.getTracing().isEnableTrace()) {
            operation.run();
        }
    }
}
