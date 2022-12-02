package org.RaiJin.common.config;

import org.RaiJin.common.aop.SentryClientAspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Use this common config for Web app
 */
@Configuration
@Import(value = {ItachiConfig.class, SentryClientAspect.class,})
public class ItachiWebConfig {
}
