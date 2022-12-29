package org.RaiJin.config;

import org.RaiJin.common.config.ItachiWebConfig;
import org.RaiJin.common.env.EnvConfig;
import org.RaiJin.core.balancer.LoadBalancer;
import org.RaiJin.core.balancer.RandomLoadBalancer;
import org.RaiJin.core.filter.FaviconFilter;
import org.RaiJin.core.filter.NakedDomainFilter;
import org.RaiJin.core.filter.SecurityFilter;
import org.RaiJin.core.http.HttpClient;
import org.RaiJin.core.http.ReqForwarder;
import org.RaiJin.core.http.RequestDataExtractor;
import org.RaiJin.core.http.ReverseProxyFilter;
import org.RaiJin.core.interceptor.CacheResponseInterceptor;
import org.RaiJin.core.interceptor.LoggingTraceInterceptor;
import org.RaiJin.core.interceptor.PostForwardResponseInterceptor;
import org.RaiJin.core.interceptor.PreForwardRequestInterceptor;
import org.RaiJin.core.mappings.ConfigurationMappingsProvider;
import org.RaiJin.core.mappings.MappingValidator;
import org.RaiJin.core.mappings.MappingsProvider;
import org.RaiJin.core.mappings.ProgrammaticMappingsProvider;
import org.RaiJin.core.trace.ProxyingTraceInterceptor;
import org.RaiJin.core.trace.TraceInterceptor;
import org.RaiJin.view.AssetLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Optional;

@Configuration
@EnableConfigurationProperties({RaiJinProperties.class, ItachiProperties.class})
@Import(value = ItachiWebConfig.class)
public class RaiJinConfiguration {
    protected RaiJinProperties raiJinProperties;
    protected ServerProperties serverProperties;
    protected ItachiProperties itachiProperties;
    protected AssetLoader assetLoader;

    public RaiJinConfiguration(RaiJinProperties raiJinProperties,
                               ServerProperties serverProperties,
                               ItachiProperties itachiProperties,
                               AssetLoader assetLoader) {
        this.raiJinProperties = raiJinProperties;
        this.serverProperties = serverProperties;
        this.itachiProperties = itachiProperties;
        this.assetLoader = assetLoader;
    }

    @Bean
    public FilterRegistrationBean<ReverseProxyFilter> raiJinReverseProxyFilterRegistrationBean(
            ReverseProxyFilter proxyFilter) {
        FilterRegistrationBean<ReverseProxyFilter> registrationBean = new FilterRegistrationBean<>(proxyFilter);
        registrationBean.setOrder(raiJinProperties.getFilterOrder());
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<NakedDomainFilter> nakedDomainFilterFilterRegistrationBean(EnvConfig envConfig) {
        FilterRegistrationBean<NakedDomainFilter> registrationBean =
                new FilterRegistrationBean<>(new NakedDomainFilter(envConfig));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+90);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<SecurityFilter> securityFilterFilterRegistrationBean(EnvConfig envConfig) {
        FilterRegistrationBean<SecurityFilter> registrationBean =
                new FilterRegistrationBean<>(new SecurityFilter(envConfig));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+80);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<FaviconFilter> faviconFilterFilterRegistrationBean()
    {
        FilterRegistrationBean<FaviconFilter> registrationBean =
                new FilterRegistrationBean<>(new FaviconFilter(assetLoader.getFaviconBytes()));
                registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+70);
                return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public ReverseProxyFilter raiJinReverseProxyFilter(
            RequestDataExtractor extractor,
            MappingsProvider provider,
            ReqForwarder forwarder,
            ProxyingTraceInterceptor interceptor,
            PreForwardRequestInterceptor requestInterceptor
    ) {
        return new ReverseProxyFilter(raiJinProperties,extractor,provider,forwarder,interceptor,requestInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClient raiJinHttpClientProvider() {
        return new HttpClient();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestDataExtractor raiJinRequestDataExtractor() {
        return new RequestDataExtractor();
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingsProvider raiJinConfigurationMappingsProvider(EnvConfig envConfig,
                                                                MappingValidator validator,
                                                                HttpClient httpClientProvider) {
        if(raiJinProperties.isEnableProgrammaticMapping()) {
            return new ProgrammaticMappingsProvider(envConfig,serverProperties,raiJinProperties,validator,httpClientProvider);
        } else {
            return new ConfigurationMappingsProvider(serverProperties, raiJinProperties, validator, httpClientProvider);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadBalancer raiJinLoadBalancer() {
        return new RandomLoadBalancer();
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingValidator raiJinMappingsValidator() {
        return new MappingValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public ReqForwarder raiJinRequestForwarder(
            HttpClient httpClient,
            MappingsProvider provider,
            LoadBalancer loadBalancer,
            Optional<MeterRegistry> meterRegistry,
            ProxyingTraceInterceptor traceInterceptor,
            PostForwardResponseInterceptor responseInterceptor
    ) {
        return new ReqForwarder(serverProperties,raiJinProperties,httpClient,provider,loadBalancer,meterRegistry,traceInterceptor,responseInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public TraceInterceptor raiJinTraceInterceptor() {
        return new LoggingTraceInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProxyingTraceInterceptor raiJinProxyingTraceInterceptor(TraceInterceptor traceInterceptor) {
        return new ProxyingTraceInterceptor(raiJinProperties, traceInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public PostForwardResponseInterceptor raiJinPostForwardResponseInterceptor() {
        return new CacheResponseInterceptor();
    }

}
