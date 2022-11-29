package org.RaiJin.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RaiJinProperties.class, ItachiProperties.class})
public class RaiJinConfiguration {
}
