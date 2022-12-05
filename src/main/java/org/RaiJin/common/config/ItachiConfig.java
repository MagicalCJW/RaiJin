package org.RaiJin.common.config;


import feign.RequestInterceptor;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import org.RaiJin.common.auth.AuthorizeInterceptor;
import org.RaiJin.common.auth.FeignRequestHeaderInterceptor;
import org.RaiJin.common.env.EnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.ibit.structlog4j.StructLog4J;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@EnableConfigurationProperties(ItachiProps.class)
public class ItachiConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active:021}")
    private String activeProfile;

    @Value("${spring.application.name:NA}")
    private String appName;

    @Autowired
    ItachiProps itachiProps;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public EnvConfig envConfig() {
        return EnvConfig.getEnvConfig(activeProfile);
    }

    @Bean
    public SentryClient sentryClient() {
        Sentry.init(itachiProps.getSentryDsn());
        SentryClient sentryClient = Sentry.setR
        sentryClient.setEnvironment(activeProfile);
        sentryClient.setRelease(staffjoyProps.getDeployEnv());
        sentryClient.addTag("service", appName);

        return sentryClient;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizeInterceptor());
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return new FeignRequestHeaderInterceptor();
    }

    @PreDestroy
    public void destroy() {
        sentryClient().close();
    }

}
