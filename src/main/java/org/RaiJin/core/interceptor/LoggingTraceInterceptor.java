package org.RaiJin.core.interceptor;

import org.RaiJin.core.trace.ForwardRequest;
import org.RaiJin.core.trace.IncomingRequest;
import org.RaiJin.core.trace.ReceivedResponse;
import org.RaiJin.core.trace.TraceInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTraceInterceptor implements TraceInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingTraceInterceptor.class);

    @Override
    public void onRequestReceived(String traceId, IncomingRequest request) {
        log.info("Incoming HTTP request received: traceId {}, method {}, host {}, uri {}, headers {}", traceId,
                request.getMethod(), request.getHost(),
                request.getUri(), request.getHeaders());
    }

    @Override
    public void onNoMappingFound(String traceId, IncomingRequest request) {
        log.info("No mapping found for incoming HTTP request: traceId {}, method {}, host {}, uri {}, headers {}", traceId,
                request.getMethod(), request.getHost(),
                request.getUri(), request.getHeaders());
    }

    @Override
    public void onForwardStart(String traceId, ForwardRequest request) {
        log.info("Forwarding HTTP request started: traceId {}, mappingName {}, method {}, host {}, uri {}, body {}, headers {}", traceId,
                StringUtils.trimToEmpty(request.getMappingName()),
                request.getMethod(), request.getHost(),
                request.getUri(), request.getBody(),
                request.getHeaders());
    }

    @Override
    public void onForwardError(String traceId, Throwable error) {
        log.error("Forwarding HTTP request failed: traceId {}, error {}",traceId, error);
    }

    @Override
    public void onForwardComplete(String traceId, ReceivedResponse response) {
        log.info("Forwarded HTTP response received: traceId {}, status {}, body {}, headers {}",traceId,response.getStatus(),response.getBody(),response.getHeaders());
    }
}
