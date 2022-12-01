package org.RaiJin.core.http;

import io.micrometer.core.instrument.MeterRegistry;
import org.RaiJin.config.MappingProperties;
import org.RaiJin.config.RaiJinProperties;
import org.RaiJin.core.balancer.LoadBalancer;
import org.RaiJin.core.interceptor.PostForwardResponseInterceptor;
import org.RaiJin.core.mappings.MappingsProvider;
import org.RaiJin.core.trace.HttpEntity;
import org.RaiJin.core.trace.ProxyingTraceInterceptor;
import org.RaiJin.core.trace.TraceInterceptor;
import org.RaiJin.exception.RaiJinException;
import org.apache.http.protocol.ResponseDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Optional;

/**
 * core function, forward requests to individual service.
 */
public class ReqForwarder {

    private static final Logger log = LoggerFactory.getLogger(ReqForwarder.class);

    protected final ServerProperties serverProperties;
    protected final RaiJinProperties raiJinProperties;
    protected final HttpClient httpClient;
    protected final MappingsProvider mappingsProvider;
    protected final LoadBalancer loadBalancer;
    protected final Optional<MeterRegistry> meterRegistry;
    protected final ProxyingTraceInterceptor traceInterceptor;
    protected final PostForwardResponseInterceptor postForwardResponseInterceptor;

    public ReqForwarder(ServerProperties serverProperties, RaiJinProperties raiJinProperties, HttpClient httpClient, MappingsProvider mappingsProvider, LoadBalancer loadBalancer, Optional<MeterRegistry> meterRegistry, ProxyingTraceInterceptor traceInterceptor, PostForwardResponseInterceptor postForwardResponseInterceptor) {
        this.serverProperties = serverProperties;
        this.raiJinProperties = raiJinProperties;
        this.httpClient = httpClient;
        this.mappingsProvider = mappingsProvider;
        this.loadBalancer = loadBalancer;
        this.meterRegistry = meterRegistry;
        this.traceInterceptor = traceInterceptor;
        this.postForwardResponseInterceptor = postForwardResponseInterceptor;
    }

    public ResponseEntity<byte[]> forwardHttpRequest(RequestBody data, String traceId, MappingProperties mapping) {
        ForwardDestination destination = resolveForwardDestination(data.getUri(),mapping);
        prepareForwardRequestHeaders(data, destination);
        traceInterceptor.onForwardStart(traceId,destination.getMappingName(),
                data.getMethod(), data.getHost(),destination.getUri().toString(),
                data.getBody(), data.getHeaders());
        RequestEntity<byte[]> request = new RequestEntity<>(data.getBody(), data.getHeaders(),data.getMethod(), destination.getUri());
        ResponseBody response = sendRequest(traceId,request,mapping, destination.getMappingMetricsName(), data);

        log.debug(String.format("Forwarded %s %s %s -> %s %d",data.getMethod(),data.getHost(), data.getUri(), destination.getUri(), response.getStatus().value()));

        traceInterceptor.onForwardComplete(traceId, response.getStatus(),response.getBody(),response.getHeaders());
        postForwardResponseInterceptor.intercept(response, mapping);
        prepareForwardResponseHeaders(response);

        return ResponseEntity.status(response.getStatus())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

    /**
     * Remove all protocol-level headers from remote server's response
     * that do not apply to the new response we are sending.
     *
     * @param response 响应报文
     */
    protected void prepareForwardResponseHeaders(ResponseBody response) {
        HttpHeaders headers = response.getHeaders();
        headers.remove(HttpHeaders.TRANSFER_ENCODING);
        headers.remove(HttpHeaders.CONNECTION);
        headers.remove(HttpHeaders.SERVER);
        headers.remove("Public-Key-Pins");
        headers.remove("Strict-Transport-Security");
    }

    /**
     * Remove all protocol-level headers from the clients request that
     * do not apply to the new request we are sending to the remote server.
     *
     * @param request 请求报文
     * @param destination 转发地址
     */
    protected void prepareForwardRequestHeaders(RequestBody request, ForwardDestination destination) {
        HttpHeaders headers = request.getHeaders();
        headers.remove(HttpHeaders.TE);
    }

    protected ResponseBody sendRequest(String traceId, RequestEntity<byte[]> request, MappingProperties mapping, String mappingMetricsName, RequestBody requestData) {
        ResponseEntity<byte[]> response;
        long startingTime = System.nanoTime();
        try {
            response = httpClient.getHttpClient(mapping.getName()).exchange(request, byte[].class);
            this.recordLatency(mappingMetricsName, startingTime);
        } catch (HttpStatusCodeException e) {
            this.recordLatency(mappingMetricsName,startingTime);
            response = ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            recordLatency(mappingMetricsName, startingTime);
            traceInterceptor.onForwardFail(traceId, e);
            throw e;
        }
        OriginReqBody data = new OriginReqBody(requestData);
        return new ResponseBody(response.getStatusCode(), response.getHeaders(), response.getBody(),data);
    }


    protected ForwardDestination resolveForwardDestination(String originUri, MappingProperties mapping) {
        return new ForwardDestination(createDestinationUrl(originUri,mapping),mapping.getName(),resolveMetricsName(mapping));
    }

    protected URI createDestinationUrl(String uri, MappingProperties mapping) {
        String host = loadBalancer.chooseDestination(mapping.getDestinations());
        try {
            return new URI(host+uri);
        } catch(URISyntaxException e) {
            throw new RaiJinException("Error creating destination URL from HTTP request URI "+ uri+" using mapping "+mapping,e);
        }
    }

    protected void recordLatency(String metricName, Long startingTime) {
        meterRegistry.ifPresent(meterRegistry->meterRegistry.timer(metricName).record(Duration.ofNanos(System.nanoTime()-startingTime)));
    }

    protected String resolveMetricsName(MappingProperties mapping) {
        return raiJinProperties.getMetrics().getNamePrefix()+"."+mapping.getName();
    }

}
