package org.RaiJin.config;

import org.RaiJin.common.config.ItachiWebConfig;
import org.RaiJin.common.env.EnvConfig;
import org.RaiJin.core.filter.NakedDomainFilter;
import org.RaiJin.core.http.ReverseProxyFilter;
import org.RaiJin.view.AssetLoader;
import org.apache.catalina.Server;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

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


}
