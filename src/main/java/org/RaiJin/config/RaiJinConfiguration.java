package org.RaiJin.config;

import org.RaiJin.common.config.ItachiWebConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({RaiJinProperties.class, ItachiProperties.class})
@Import(value = ItachiWebConfig.class)
public class RaiJinConfiguration {



}
